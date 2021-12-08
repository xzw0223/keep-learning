package xzw.shuai.netty.bound;

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
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class MyBoundClinet {
    public static void main(String[] args) {

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new MyClientBoundHandler());

        try {
            ChannelFuture channelFuture = bootstrap.connect("localhost", 11111).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
        } finally {
            eventExecutors.shutdownGracefully();
        }


    }


    static class MyClientBoundHandler extends ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
                    // outbound出站  进行编码
                    .addLast(new MyLongToByteEncoder())
                    .addLast(new MyBoundServer.MyByteToLongDecoder())
                    .addLast(new MyClientHandler());

        }
    }

    static class MyLongToByteEncoder extends MessageToByteEncoder<Long>{
        @Override
        protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
            System.out.println(this.getClass().getSimpleName() + "的encode方法被调用" + " 发送msg = " + msg);
            out.writeLong(msg);
        }
    }
    static class MyClientHandler extends SimpleChannelInboundHandler<Long>{

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
            System.out.println(ctx.channel().remoteAddress() + "是服务器地址  收到他的消息" + msg);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            long msg = 1024;
            System.out.println(this.getClass().getSimpleName() + "发送数据 " + msg);
            ctx.writeAndFlush(msg);

            // 可以试试下面,如果非long类型 则直接按照字节获取 解析long即可
            // 不就是数据不对了嘛 噢哈哈
            // ctx.writeAndFlush(Unpooled.copiedBuffer("abcabcabcabcabcacb", CharsetUtil.UTF_8));
        }
    }
}
