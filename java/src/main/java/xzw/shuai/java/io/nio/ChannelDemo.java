package xzw.shuai.java.io.nio;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

/**
 * channel的测试案例
 */
public class ChannelDemo {
    public static void main(String[] args) throws Exception {

        String path = "C:\\Users\\Administrator\\Desktop\\test\\a.txt";
        sendFileChannelTest(path);
        readFileChannelTest(path);

        file2File();
        transfer();
    }


    public static void sendFileChannelTest(String path) throws Exception {
        File file = new File(path);
        // 获取文件输入流
        FileOutputStream fos = new FileOutputStream(file);

        // 获取channel
        FileChannel channel = fos.getChannel();

        // 创建buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String sendStr = "徐志文是真的帅";

        // 将数据写入buf
        // 如果分配的容量不够用则抛出异常, BufferOverflowException
        buffer.put(sendStr.getBytes());

        // 反转,准备写入数据
        buffer.flip();
        // channel将buf写入到文件
        channel.write(buffer);
        fos.close();
    }

    public static void readFileChannelTest(String path) throws Exception {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);

        FileChannel channel = fis.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate((int) file.length());

        // 读取数据
        channel.read(buffer);

        // buffer.flip();
        System.out.println(new String(buffer.array()));
        fis.close();
    }


    public static void file2File() throws Exception{
        String path = "C:\\Users\\Administrator\\Desktop\\test\\a.txt";
        FileInputStream fis = new FileInputStream(path);
        FileChannel channel1 = fis.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(3);

        String toPath="C:\\Users\\Administrator\\Desktop\\test\\b.txt";
        FileOutputStream fos = new FileOutputStream(toPath);
        FileChannel channel2 = fos.getChannel();
        while (true){
            int read = channel1.read(buffer);
            if (read==-1){
                break;
            }
            buffer.flip();
            channel2.write(buffer);

            // 如果buf不够打,在循环的过程中没有调用clear则会导致循环卡死
            buffer.clear();
        }


        fos.close();
        fis.close();

    }


    public static void transfer() throws IOException {
        String jpgPath = "C:\\Users\\Administrator\\Desktop\\test\\aaa.png";
        String toJpgPath = "C:\\Users\\Administrator\\Desktop\\test\\bbb.png";

        File jpgFile = new File(jpgPath);
        File toJpgFile = new File(toJpgPath);

        FileInputStream fis = new FileInputStream(jpgFile);
        FileOutputStream fos = new FileOutputStream(toJpgFile);

        FileChannel source = fis.getChannel();
        FileChannel out = fos.getChannel();

        source.transferTo(0,source.size(),fos.getChannel());
       //out.transferFrom(source,0,source.size());

        fis.close();
        fos.close();


    }
}
