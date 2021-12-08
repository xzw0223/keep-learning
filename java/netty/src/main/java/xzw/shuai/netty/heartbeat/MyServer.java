package xzw.shuai.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author xuzhiwen
 */
public class MyServer {

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();


        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // 对bossGroup增加日志处理器
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                // 处理空闲状态的处理器
                                /*
                                -- 会发送数据表确定连接是否存在
                                long readerIdleTime, 多长时间未读
                                long writerIdleTime, 多长时间未写
                                long allIdleTime,    多长时间未读写
                                TimeUnit unit
                                 */
                                // 当事件触发的时候,会调用下游handler的userEventTriggered方法
                                .addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS))
                                .addLast(new MyServerHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.bind(18081).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    static class MyServerHandler extends ChannelInboundHandlerAdapter {
        /**
         * 该code中 空闲事件触发会调用该方法
         *
         * @param evt 时间
         */
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                String eventType;
                switch (event.state()) {
                    case READER_IDLE:
                        eventType = "发生读空闲";
                        break;
                    case WRITER_IDLE:
                        eventType = "发生写空闲";
                        break;
                    case ALL_IDLE:
                        eventType = "发生读写空闲";
                        break;
                    default:
                        eventType = "若无其事";
                }

                System.out.println(ctx.channel().remoteAddress() + " 产生事件--" + eventType);

            }
            // other....
        }
    }
}
