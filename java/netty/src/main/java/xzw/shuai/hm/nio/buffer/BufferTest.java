package xzw.shuai.hm.nio.buffer;

import xzw.shuai.hm.utils.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static xzw.shuai.hm.utils.ByteBufferUtil.debugAll;

/**
 * capacity 容量
 * limit 读写的限制
 * position -- 可以认为是指针, 比如读到哪里了 或者 写到哪里了
 */
public class BufferTest {


    public static void main(String[] args) {
       //   test1();




    }



    private static void test1() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a','b','c','d'});

        buffer.flip();

        // rewind 从头开始读
//        buffer.get(new byte[4]);
//        ByteBufferUtil.debugAll(buffer);
//        buffer.rewind();
//        ByteBufferUtil.debugAll(buffer);

        // mark & reset
        // mark 左一个标记,记录position位置,reset是将position重置到mark的位置
        System.out.println(buffer.get());
        System.out.println(buffer.get());
        buffer.mark();
        System.out.println(buffer.get());
        System.out.println(buffer.get());
        buffer.reset();
        System.out.println(buffer.get());
        System.out.println(buffer.get());
    }


}
