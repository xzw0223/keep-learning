package xzw.shuai.sgg.netty.study;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyClient {

    public static void main(String[] args) {


        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

        // 获取client启动类
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                // 设置线程组
                .group(eventExecutors)
                // 设置服务端channel
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
        System.out.println("client is running....");

        // 启动客户端去连接服务端
        try {
            ChannelFuture channelFuture = bootstrap.connect("localhost", 6666)
                    .sync();
            System.out.println("奥术大师大所");
            ChannelFuture closeFuture = channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventExecutors.shutdownGracefully();
        }


    }

    static class NettyClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("收到服务端说 : "+ ((ByteBuf) msg).toString(CharsetUtil.UTF_8));
        }

        /**
         * 当通道就绪就会调用该方法
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.copiedBuffer("可层听闻徐志文最帅", CharsetUtil.UTF_8));
        }
    }
}
