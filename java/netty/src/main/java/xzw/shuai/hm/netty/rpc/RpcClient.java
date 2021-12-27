package xzw.shuai.hm.netty.rpc;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import xzw.shuai.hm.netty.chatgroup.message.rpc.RpcRequestMessage;
import xzw.shuai.hm.netty.chatgroup.protocol.MessageCodecSharable;
import xzw.shuai.hm.netty.chatgroup.protocol.ProcotolFrameDecoder;

@Slf4j
public class RpcClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable(); // 【使用 asm包方法】

        // rpc 响应消息处理器，待实现
        RpcResponseMessageHandler RPC_RESPONSE_HANDLER = new RpcResponseMessageHandler();
        try {
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
            Channel channel = bootstrap.connect("localhost", 11111).sync().channel();

            final ChannelFuture future = channel.writeAndFlush(
                    new RpcRequestMessage(
                            1,
                            "com.rpc.server.service.HelloService",
                            "sayHello",
                            String.class,
                            new Class[]{String.class},
                            new Object[]{"helloworld!"}
                    )
            ).addListener(promise->{
                if(!promise.isSuccess()){
                    Throwable cause = promise.cause();
                    log.error("error : ", cause);
                }else {
                    Object now = promise.getNow();
                    System.out.println(now);
                }
            });

            channel.closeFuture().sync();
            for (;;){

            }
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}