package xzw.shuai.hm.netty.test.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ProtocolServer {
    public static void main(String[] args) {

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        new ServerBootstrap()
                .group(boss,worker)
                .channel(NioServerSocketChannel.class);


    }
}
