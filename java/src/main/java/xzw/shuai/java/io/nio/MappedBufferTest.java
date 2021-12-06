package xzw.shuai.java.io.nio;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MappedBufferTest {

    public static void main(String[] args) throws Exception {


        RandomAccessFile file = new RandomAccessFile(
                MappedBufferTest.class.getResource("a.txt").getPath(),
                "rw"
        );

        FileChannel channel = file.getChannel();

        /*
        0 映射的起始位置
        10 是长度
         */
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 10);

        buffer.put(4, (byte) 'a');
        buffer.put(5, (byte) 'b');
        buffer.put(6, (byte) 'c');

        file.close();


    }
}
