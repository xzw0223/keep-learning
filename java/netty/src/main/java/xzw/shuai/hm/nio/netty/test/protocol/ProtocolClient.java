package xzw.shuai.hm.nio.netty.test.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ProtocolClient {
    public static void main(String[] args) throws InterruptedException {


        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class);
        bootstrap
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler(LogLevel.INFO))
                                // redis协议
                                //.addLast(redisProtocol())

                                // http编解码
                                .addLast(new HttpServerCodec())//.addLast() // 需要自己在实现handler进行发送和接数据
                        ;

                    }
                });
        ChannelFuture cf = bootstrap
                // 连接本地redis 测试
                .connect("localhost", 6379);
        ChannelFuture channelFuture = cf.sync();
        channelFuture.channel().closeFuture().sync();

        // 关闭资源
    }

    private static ChannelInboundHandlerAdapter redisProtocol() {
        // 换行符
        byte[] line = {13, 11};
        return new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                ByteBuf buf = ctx.alloc().buffer();
                // 发送redis    set key value
                /*
                     *3
                     $3
                     set
                     $4
                     name
                     $8
                     zhangsan
                 */
                // *n 代表数组的长度
                // $n 代表发送字符数,后面代表发送的命令/内容
                // 每个数据需要换行符

                buf
                        .writeBytes("*3".getBytes()).writeBytes(line)
                        .writeBytes("$3".getBytes()).writeBytes(line)
                        .writeBytes("set".getBytes()).writeBytes(line)
                        .writeBytes("$4".getBytes()).writeBytes(line)
                        .writeBytes("name".getBytes()).writeBytes(line)
                        .writeBytes("$8".getBytes()).writeBytes(line)
                        .writeBytes("zhangsan".getBytes()).writeBytes(line);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                // 获取redis返回的结果
                System.out.println(msg.toString());
            }
        };
    }
    private static ChannelInboundHandlerAdapter httpProtocol() {
        return new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {

            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            }
        };
    }
}
