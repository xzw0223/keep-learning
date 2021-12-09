package xzw.shuai.sgg.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;

/**
 * 粘包拆包演示
 *
 * 粘包 : 多个数据被打包成一个包发送
 * 拆包 : 对打成包的数据进行拆开,很有可能  p1_1 和p1_2没有打在一个包中,导致拆包解析问题
¬
 *  启动多个client既可以看到效果,
 */
public class MyTcpClient {
    public static void main(String[] args) {

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new MyClientTcpHandler());

        try {
            ChannelFuture channelFuture = bootstrap.connect("localhost", 11111).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
        } finally {
            eventExecutors.shutdownGracefully();
        }


    }


    static class MyClientTcpHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
                    // outbound出站  进行编码
                    .addLast(new MyClientHandler());

        }
    }


    static class MyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
        int count = 0;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            byte[] data = new byte[msg.readableBytes()];
            msg.readBytes(data);
            System.out.println("client 收到数据" + new String(data, StandardCharsets.UTF_8) + "   count = " + ++count);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // TODO 循环次数加大即可看到拆包问题
            //      可以看到乱码
            for (int i = 0; i < 10; i++) {
                ByteBuf msg = Unpooled.copiedBuffer("第" + i + "次说徐志文是帅哥", CharsetUtil.UTF_8);
                ctx.writeAndFlush(msg);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
