package xzw.shuai.sgg.netty.tcp.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;

/**
 * 粘包拆包演示
 * <p>
 * 粘包 : 多个数据被打包成一个包发送
 * 拆包 : 对打成包的数据进行拆开,很有可能  p1_1 和p1_2没有打在一个包中,导致拆包解析问题
 * ¬
 * 启动多个client既可以看到效果,
 */
public class MyTcpProtocolClient {
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
                    .addLast(new MyMessageEncoder())
                    .addLast(new MyTcpProtocolServer.MyMessageDecoder())
                    .addLast(new MyProtocolClientHandler());

        }
    }


    static class MyProtocolClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {
        int count = 0;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

            int len = msg.getLen();
            byte[] data = msg.getData();
            System.out.println("client收到数据 = " + new String(data, CharsetUtil.UTF_8) + " len" +
                    "= " + len + "  count = " + ++count);

        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            for (int i = 0; i < 5; i++) {
                MessageProtocol messageProtocol = new MessageProtocol();
                messageProtocol.setData(("第" + i + "说徐志文就是帅 ").getBytes(StandardCharsets.UTF_8));
                messageProtocol.setLen(messageProtocol.getData().length);
                ctx.writeAndFlush(messageProtocol);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }


    static class MyMessageEncoder extends MessageToByteEncoder<MessageProtocol> {
        @Override
        protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
            System.out.println(this.getClass().getSimpleName() + " encode被调用");

            // 发送数据
            out.writeInt(msg.getLen());
            out.writeBytes(msg.getData());
        }
    }
}
