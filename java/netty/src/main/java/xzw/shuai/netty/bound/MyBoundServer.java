package xzw.shuai.netty.bound;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyBoundServer {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new MyServerBoundHandler());

        try {
            ChannelFuture channelFuture = bootstrap.bind(11111).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    static class MyServerBoundHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
                    // 出站(inbound) 进行解码
                    .addLast(new MyByteToLongDecoder())
                    .addLast(new MyBoundClinet.MyLongToByteEncoder())
                    .addLast(new MyServerHandler());
        }
    }

    static class MyByteToLongDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

            System.out.println(this.getClass().getSimpleName() + "decode被调用");

            // 判断如果有8个字节才处理
            if (in.readableBytes() >= 8) {
                out.add(in.readLong());
            }
        }
    }

    static class MyServerHandler extends SimpleChannelInboundHandler<Long> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
            long r =2048;
            System.out.println(ctx.channel().remoteAddress() + " 读取数据 " + msg + " 并返回一个 " + r);

            ctx.writeAndFlush(r);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }

}
