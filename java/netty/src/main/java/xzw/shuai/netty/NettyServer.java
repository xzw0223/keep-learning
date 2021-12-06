package xzw.shuai.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyServer {
    public static void main(String[] args) {
        // 用于处理连接请求,具体处理业务交给workGroup处理
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        // 服务端启动对象
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                // 设置group
                .group(bossGroup, workerGroup)
                // 使用该类作为服务器端的channel实现
                .channel(NioServerSocketChannel.class)
                // 设置线程队列得到的连接个数
                .option(ChannelOption.SO_BACKLOG, 100)
                // 设置保持活动的连接状态
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 给workerGroup的eventLoop设置对应的handler
                .childHandler(
                        // 创建一个通道测试对象
                        new ChannelInitializer<SocketChannel>() {
                            // 给pipeline设置channel
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new NettyServerHandler());
                            }
                        });
        System.out.println("server is running.......");
        try {
            ChannelFuture channelFuture = bootstrap.bind(6666)
                    .sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅的关闭,别问我有多优雅,总之就是很优雅
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    /**
     * 自定义handler 需要继承netty指定好的handlerAdapter
     */
    static class NettyServerHandler extends ChannelInboundHandlerAdapter {
        /**
         * 读取数据的方法
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("server receive msg = " + ((ByteBuf) msg).toString(CharsetUtil.UTF_8));
            System.out.println(ctx.channel().remoteAddress());
        }

        /**
         * 读取数据完成调用
         */
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            // 数据写入缓存并刷新
            // 对发送数据进行编码
            ctx.writeAndFlush(Unpooled.copiedBuffer("听过是真的帅", CharsetUtil.UTF_8));
        }

        /**
         * 异常捕捉调用的方法
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }
    }
}
