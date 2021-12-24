package xzw.shuai.hm.netty.chatgroup.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import xzw.shuai.hm.netty.chatgroup.protocol.MessageCodecSharable;
import xzw.shuai.hm.netty.chatgroup.protocol.ProcotolFrameDecoder;
import xzw.shuai.hm.netty.chatgroup.server.handler.*;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();


        LoginRequestHandler loginRequestHandler = new LoginRequestHandler();
        ChatRequestMessageHandler chatRequestMessageHandler = new ChatRequestMessageHandler();
        final ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();//--单聊---处理器
        final GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();//--创建群聊---处理器
        final GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();      //--群聊---处理器
        final QuitHandler QUIT_HANDLER = new QuitHandler();  //--断开连接---处理器



        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {

                    ch.pipeline().addLast(new IdleStateHandler(
                            1000,0,0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(
                            // 出入栈都可以处理
                            new ChannelDuplexHandler(){
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

                                    if(evt instanceof IdleStateEvent){
                                        IdleStateEvent event = (IdleStateEvent) evt;
                                        // 读空闲
                                        if(event.state()== IdleState.READER_IDLE){
                                            ctx.channel().close();
                                        }

                                    }

                                }
                            });

                    ch.pipeline().addLast(QUIT_HANDLER);               //--断开连接---处理器
                    ch.pipeline().addLast(new ProcotolFrameDecoder()); // 帧解码器 【与自定义编解码器 MessageCodecSharable一起配置参数】
                    ch.pipeline().addLast(LOGGING_HANDLER);            // 日志
                    ch.pipeline().addLast(MESSAGE_CODEC);              // 出站入站的 自定义编解码器 【 解析消息类型 】
                    // simple处理器 【针对性的对登录进行处理】 【流水线 会向上执行出站Handler,  到 ProcotolFrameDecoder(入站停止)】
                    ch.pipeline().addLast(loginRequestHandler);         //--登录---处理器
                    ch.pipeline().addLast(CHAT_HANDLER);          //--单聊---处理器
                    ch.pipeline().addLast(GROUP_CREATE_HANDLER);  //--创建群聊---处理器
                    ch.pipeline().addLast(GROUP_CHAT_HANDLER);    //--群聊---处理器



                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


}
