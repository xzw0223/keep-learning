package xzw.shuai.sgg.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MyTcpServer {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new MyServerTcpHandler());

        try {
            ChannelFuture channelFuture = bootstrap.bind(11111).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    static class MyServerTcpHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
                    .addLast(new MyServerHandler());
        }
    }


    static class MyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
        int count = 0;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            byte[] data = new byte[msg.readableBytes()];
            msg.readBytes(data);
            System.out.println("server msg = " + new String(data, StandardCharsets.UTF_8) + "   count" + ++count);

            // 返回一个随机结果
            ByteBuf rMsg = Unpooled.copiedBuffer(UUID.randomUUID().toString()+"   ", CharsetUtil.UTF_8);
            ctx.writeAndFlush(rMsg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

}
