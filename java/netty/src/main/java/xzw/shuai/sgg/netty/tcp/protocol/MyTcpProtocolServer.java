package xzw.shuai.sgg.netty.tcp.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class MyTcpProtocolServer {
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
                    .addLast(new MyMessageDecoder())
                    .addLast(new MyTcpProtocolClient.MyMessageEncoder())
                    .addLast(new MyServerHandler());
        }
    }


    static class MyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {
        int count = 0;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
            int len = msg.getLen();
            byte[] data = msg.getData();
            System.out.println("server 收到数据 len = " + len + " data = " + new String(data, StandardCharsets.UTF_8) + "  count = " + ++count);


            byte[] dst = UUID.randomUUID().toString().substring(0, 10).getBytes(StandardCharsets.UTF_8);
            MessageProtocol messageProtocol = new MessageProtocol();
            messageProtocol.setData(dst);
            messageProtocol.setLen(messageProtocol.getData().length);

            ctx.writeAndFlush(messageProtocol);


        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    static class MyMessageDecoder extends ReplayingDecoder<Void> {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            System.out.println(this.getClass().getSimpleName() + " decode被调用");

            // 转换数据
            int len = in.readInt();
            byte[] data = new byte[len];
            in.readBytes(data);

            MessageProtocol msg = new MessageProtocol();
            msg.setLen(len);
            msg.setData(data);

            out.add(msg);
        }
    }

}
