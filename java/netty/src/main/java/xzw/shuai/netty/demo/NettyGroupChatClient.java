package xzw.shuai.netty.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @author xuzhiwen
 */
public class NettyGroupChatClient {
    private String host = "localhost";
    private int port = 8081;

    public NettyGroupChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        NettyGroupChatClient client = new NettyGroupChatClient("localhost", 18081);
        try {
            client.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() throws InterruptedException {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline
                                // 添加解码器
                                .addLast("decoder", new StringDecoder())
                                // 添加编码器
                                .addLast("encoder", new StringEncoder())
                                // user define
                                .addLast(new GroupChatClientHandler());
                    }
                });


        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            System.out.println("-----" + channel.localAddress() + " 准备好了");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if (line == null || "".equals(line)) {
                    break;
                }
                channel.writeAndFlush(line + "\r\n");
            }

            channel.closeFuture().sync();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }

    static class GroupChatClientHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(ctx.channel().localAddress() + "收到消息 = " + msg.trim());
        }
    }
}
