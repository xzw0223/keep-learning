package xzw.shuai.io.nio;

import java.nio.IntBuffer;

/**
 * nio的buffer
 * @author Administrator
 */
public class NIOBuffer {

    public static void main(String[] args) {
        NIOBuffer nioBuffer = new NIOBuffer();

        nioBuffer.intBuffer();
    }

    public static void bufferMethods(){
        IntBuffer allocate = IntBuffer.allocate(1);

        allocate
                //.mark();
                //.capacity();容量
                //.position() 当前索引位置
                .limit(); // 最大可以读取多少个
    }

    public void intBuffer(){
        // 容量为5的buf 智能存放5个int
        IntBuffer buffer = IntBuffer.allocate(5);
        buffer.put(3,100);
        System.out.println(buffer.get());
        // 向buf存入数据
        for (int i = 0; i < buffer.capacity()-1; i++) {
            buffer.put(i);
        }

        int i1 = buffer.get();
        System.out.println(i1);

        System.out.println(buffer.get(4));

        // 超过的会抛出异常
        // BufferOverflowException
        buffer.put(1);
    }


}
