package xzw.shuai.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class NettyBuferTest {

    public static void main(String[] args) {
        test2();
    }

    public static void test1() {
        ByteBuf byteBuf = Unpooled.buffer(10);
        System.out.println(byteBuf.capacity());
        // 如果不够则会自动扩容
        for (int i = 0; i < 11; i++) {
            byteBuf.writeByte(i);
        }

        System.out.println(byteBuf.capacity());

        //
        for (int i = 0; i < byteBuf.capacity() - 10; i++) {
            // 判断是否可读
            boolean readable = byteBuf.isReadable();
            System.out.println(readable);
            // 如果当前读的位置小于下一个写的索引则可以读,否则抛出异常
            byteBuf.readByte();
            // 如果通过索引的方式获取,如果index没有超过长度则可以一直获取到最后一个元素
            byteBuf.getByte(i);
            // System.out.println(a);
        }

        // 重置 ri,wi
        byteBuf.resetReaderIndex();
        byteBuf.resetWriterIndex();

        System.out.println(1);
    }


    public static void test2(){
        ByteBuf buf = Unpooled.copiedBuffer("徐志文就是帅", CharsetUtil.UTF_8);

        System.out.println(1);
    }
}
