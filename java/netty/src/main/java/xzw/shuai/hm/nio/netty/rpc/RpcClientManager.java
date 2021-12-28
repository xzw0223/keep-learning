package xzw.shuai.hm.nio.netty.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import xzw.shuai.hm.nio.netty.chatgroup.message.rpc.RpcRequestMessage;
import xzw.shuai.hm.nio.netty.chatgroup.protocol.MessageCodecSharable;
import xzw.shuai.hm.nio.netty.chatgroup.protocol.ProcotolFrameDecoder;
import xzw.shuai.hm.utils.SequenceIdGenerator;

import java.lang.reflect.Proxy;

@Slf4j
public class RpcClientManager {
    private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    private static final MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable(); // 【使用 asm包方法】
    private static final RpcResponseMessageHandler RPC_RESPONSE_HANDLER = new RpcResponseMessageHandler();
    private static Channel channel;

    static {
        //initChannel();
    }

    private RpcClientManager() {

    }

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder()); // 【使用 asm包方法】
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_RESPONSE_HANDLER);
            }
        });
        try {
            ChannelFuture channelFuture = bootstrap.connect("localhost", 11111).sync();
            System.out.println("获取连接");
            channel = channelFuture.channel();

            channel.closeFuture().addListener(future -> group.shutdownGracefully());
        } catch (Exception e) {
            log.error("client error", e);
        }


        HelloService service = getService(HelloService.class);
        String shuai = service.sayHello("shuai");
        System.out.println(shuai);
    }

    public static <T> T getService(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
//                clazz.getInterfaces(),
                (proxy, method, args) -> {
                    // 构建消息
                    int sequenceId = SequenceIdGenerator.nextId();
                    RpcRequestMessage message = new RpcRequestMessage(
                            sequenceId,
                            clazz.getName(),
                            method.getName(),
                            method.getReturnType(),
                            method.getParameterTypes(),
                            args
                    );
                    // 写入消息
                    instance().writeAndFlush(message);
                    Promise<Object> promise = new DefaultPromise<>(instance().eventLoop());
                    RpcResponseMessageHandler.PROMISES
                            .put(sequenceId, promise);

                    // 等待结果
                    promise.await();

                    if (promise.isSuccess()) {
                        return promise.getNow();
                    }
                    throw new RuntimeException(promise.cause());
                });
        return (T) o;
    }

    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder()); // 【使用 asm包方法】
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_RESPONSE_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 11111).sync().channel();
            System.out.println("获取连接");
            channel.closeFuture().addListener(future -> group.shutdownGracefully());
        } catch (Exception e) {
            log.error("client error", e);
        }
    }

    public static Channel instance() {
        return channel;
    }
}