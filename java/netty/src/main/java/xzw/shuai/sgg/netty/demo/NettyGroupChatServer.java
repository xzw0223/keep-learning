package xzw.shuai.sgg.netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

public class NettyGroupChatServer {


    private int port = 8081;

    public NettyGroupChatServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        NettyGroupChatServer server = new NettyGroupChatServer(18081);
        try {
            server.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 处理client请求
     */
    public void run() throws InterruptedException {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline
                                // 添加解码器
                                .addLast("decoder", new StringDecoder())
                                // 添加编码器
                                .addLast("encoder", new StringEncoder())
                                // user define
                                .addLast(new GroupChatServerHandler());
                    }
                });

        System.out.println("netty server starting..............");
        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            System.out.println("netty server running..................................");
            channelFuture.channel().closeFuture().sync();
        } finally {
            System.out.println("netty server shutdown!!");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }


    static class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {
        /**
         * 定期channel组,管理所有channel
         * <p>
         * GlobalEventExecutor.INSTANCE : 全局事件执行器
         *
         * TODO 该类需要保证是单例的,如果非单例,则会导致无法转发,因为在维护channel列表的时候会导致
         *      无法转发,因为每个channelGroup是不同的实例,无法在保证所有channel都在一个容器中
         */
        private  static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        /**
         * 处于活动状态
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println(ctx.channel().remoteAddress() + "上线了!");
        }

        /**
         * 非活跃状态
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println(ctx.channel().remoteAddress() + "离线了!");
        }

        /**
         * 断开连接
         */
        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            channelGroup.writeAndFlush("[客户端] " + channel.remoteAddress() + "离开了\n");
            //TODO 这里不需要remove,因为调用这个方法就会帮助我们将channelGroup的channel
            System.out.println(channelGroup.size());
        }

        /**
         * 建立连接后调用
         */
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            // 将该client加入加入信息转发至其他客户
            Channel channel = ctx.channel();
            channelGroup.writeAndFlush("[客户端] " + channel.remoteAddress() + " 加入聊天\n");
            // 将该client加入channel组
            channelGroup.add(channel);
            System.out.println();

        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            Channel channel = ctx.channel();
            channelGroup.forEach(v -> {
                if (v != channel) {
                    v.writeAndFlush("[用户] " + channel.remoteAddress() + " 发送了消息: " + msg);
                } else {
                    v.writeAndFlush("[my] 说: " + msg);
                }
            });
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }


}
