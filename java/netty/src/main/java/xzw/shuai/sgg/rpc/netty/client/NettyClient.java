package xzw.shuai.sgg.rpc.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static NettyRpcClientHandler client;

    public NettyClient() {
        init();
    }

    public void init() {
        client = new NettyRpcClientHandler();
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(eventExecutors)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(client);
                    }
                });

        try {
            bootstrap.connect("localhost", 6666)
                    .sync();
            System.out.println(1);
//            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Object getBean(final Class<?> serviceClass, String providerName) {
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serviceClass},
                proxy(providerName));

    }

    private static InvocationHandler proxy(String providerName) {
        return (proxy, method, args) -> {
            client.setArgs(providerName + args[0]);
            return executorService.submit(client).get();
        };
    }


}
