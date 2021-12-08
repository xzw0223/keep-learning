package xzw.shuai.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author xuzhiwen
 */
public class HttpServer {
    public static void main(String[] args) {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(2);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap =
                new ServerBootstrap()
                        .group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        // 对应的bossGroup
                        //.handler(null)
                        // 对应的workerGroup
                        .childHandler(new HttpServerInitialize());

        try {
            System.out.println(1);
            ChannelFuture channelFuture = bootstrap.bind(8081).sync();
            System.out.println(2);
            channelFuture.channel().closeFuture().sync();
            System.out.println(3);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅的关闭掉他们
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

}
