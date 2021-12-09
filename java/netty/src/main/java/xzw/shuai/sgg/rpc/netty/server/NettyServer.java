package xzw.shuai.sgg.rpc.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {

    public static void startServer0(String host, int port) {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();


        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(new NettyRpcServerHandler())
                        ;
                    }
                });
        try {

            ChannelFuture channelFuture = bootstrap
                   // .bind(host, port)
                    .bind( port)
                    .sync();
            System.out.println("server starting..............");
            channelFuture.channel().closeFuture().sync();
            System.out.println("关闭关闭sadasad阿斯利康那咖啡哪里烦死了;开发阿斯洛伐克问你");
        } catch ( InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
