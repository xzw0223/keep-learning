package xzw.shuai.netty.http.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();


        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                // 添加http的编解码器
                                .addLast("httpCodec", new HttpServerCodec())
                                /*
                                HTTP数据传输是分段/块的,
                                添加下面块处理器,和将多个段/块进行居合起来
                                TODO 这也是浏览器为什么在发送大量数据的时候会发送多次http请求
                                 */
                                .addLast(new ChunkedWriteHandler())
                                // 最大的聚合长度 8192
                                .addLast(new HttpObjectAggregator(8192))
                                /*
                                1. 对应 webSocket,它的数据是以帧(frame)的形式传递
                                2. ws://host:port/xxx 表示请求的url
                                3. 该类的核心功能是将http协议升级为ws协议,保持长链接
                                 /xzw请求路径
                                 */
                                .addLast(new WebSocketServerProtocolHandler("/xzw"))
                                .addLast(new TextWebSocketFrameHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.bind(8081).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
