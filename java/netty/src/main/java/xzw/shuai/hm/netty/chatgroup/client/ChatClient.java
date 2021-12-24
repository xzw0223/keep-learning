package xzw.shuai.hm.netty.chatgroup.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import xzw.shuai.hm.netty.chatgroup.message.impls.*;
import xzw.shuai.hm.netty.chatgroup.protocol.MessageCodecSharable;
import xzw.shuai.hm.netty.chatgroup.protocol.ProcotolFrameDecoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        final MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        CountDownLatch wait_for_login = new CountDownLatch(1);
        AtomicBoolean login = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);


                    ch.pipeline().addLast(new IdleStateHandler(
                            0,1000,0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(
                            // 出入栈都可以处理
                            new ChannelDuplexHandler(){
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    if(evt instanceof IdleStateEvent){
                                        IdleStateEvent event = (IdleStateEvent) evt;
                                        // 写 空闲
                                        // 表示我即是没有写数据,但是我依然存活 请别杀死我
                                        if(event.state()== IdleState.WRITER_IDLE){
                                            System.out.println("发送心跳包");
                                            ctx.writeAndFlush(new PingMessage());
                                        }

                                    }
                                }
                            });


                    ch.pipeline().addLast("client_handler", new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 防止使用nio线程
                            new Thread(() -> {
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码");
                                String password = scanner.nextLine();

                                // 构建登陆对象
                                LoginRequestMessage requestMessage = new LoginRequestMessage(username, password);
                                ctx.writeAndFlush(requestMessage);

                                // 线程不能停, 还得等新的代码
                                try {
                                    wait_for_login.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!login.get()) {
                                    ctx.channel().close();
                                    return;
                                }
                                for (; ; ) {
                                    System.out.println("============ 功能菜单 ============");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String nextLine = scanner.nextLine();
                                    String[] commands = nextLine.split(" ");
                                    switch (commands[0]) {
                                        case "send": // 发送消息
                                            ctx.writeAndFlush(new ChatRequestMessage(username, commands[1], commands[2]));
                                            break;
                                        case "gsend": // 群里 发送消息
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, commands[1], commands[2]));
                                            break;
                                        case "gcreate": // 创建群
                                            final Set<String> userSet = new HashSet(Arrays.asList(commands[2].split(",")));
                                            userSet.add(username);
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(commands[1], userSet));
                                            break;
                                        case "gmembers": // 查看群列表
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(commands[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, commands[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, commands[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close(); // 触发 【channel.closeFuture().sync(); 向下运行】
                                            break;
                                        default:
                                            System.out.println("未知的命令");
                                    }
                                }

                            }, "user in").start();

                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            if (msg instanceof LoginResponseMessage) {
                                boolean success = ((LoginResponseMessage) msg).isSuccess();
                                login.set(success);
                                // 唤醒user in 线程
                                wait_for_login.countDown();
                            }
                            System.out.println(msg);
                        }
                    });


                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
