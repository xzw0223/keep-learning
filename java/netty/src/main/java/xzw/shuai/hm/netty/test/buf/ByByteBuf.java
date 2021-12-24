package xzw.shuai.hm.netty.test.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import xzw.shuai.hm.utils.ByteBufUtil;

public class ByByteBuf {
    public static void main(String[] args) {
        // test1();

        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(
                        1024,// 最大字节
                        0, // 长度偏移量
                        4, // 长度几个字节
                        0, // 长度之后是否需要调整
                        0 // 长度之后知否需要剥离
                        // 4  // 如果是四 则会剥离四个字节,也就是说把长度信息剔除掉了
                ),
                // 记得开启日志 不然没有输出哈
                new LoggingHandler(LogLevel.DEBUG)
        );


        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        write(buffer, "hi xu");
        write(buffer, "hi xuzhiwen");

        channel.writeInbound(buffer);
    }

    private static void write(ByteBuf buffer, String str) {
        byte[] bytes = str.getBytes();

        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
    }

    private static void test1() {
        ByteBuf buf = ByteBufAllocator.DEFAULT
//                .directBuffer()
//                .heapBuffer()
                .buffer(10);


        System.out.println(buf.getClass());

        System.out.println(buf);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            sb.append("a");
        }
        buf.writeBytes(sb.toString().getBytes());
        ByteBufUtil.log(buf);
    }
}
