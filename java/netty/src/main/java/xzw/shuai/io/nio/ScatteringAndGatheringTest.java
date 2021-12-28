package xzw.shuai.io.nio;


import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 说白了就是可以一次写入多个buffer或者读取buffer
 *
 * Scattering : 写数据时候可以使用buffer[] 依次写
 * Gathering : 从buffer读数据时可以使用buffer[]依次读
 *
 * @author xuzhiwen
 */
public class ScatteringAndGatheringTest {

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            try {
                serverSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                client();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public static void client() throws Exception {
        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.socket().connect(new InetSocketAddress("localhost", 6666));

        ByteBuffer buff = ByteBuffer.allocate(8);

        Scanner scanner = new Scanner(System.in);
        while (true) {

            // 从控制台接收数据发送
            String next = scanner.next();
            byte[] bytes = next.getBytes();
            buff.put(bytes);
            buff.flip();
            socketChannel.write(buff);
            buff.clear();

            // 从server接收数据
            socketChannel.read(buff);
            buff.flip();
            System.out.println(new String(buff.array()));
            buff.clear();

            System.out.println();
        }

    }


    public static void serverSocket() throws Exception {

        ServerSocketChannel channel = ServerSocketChannel.open();

        channel.socket().bind(new InetSocketAddress(6666));

        ByteBuffer[] byteBuffers = new ByteBuffer[2];

        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        int msgLength = 8;
        SocketChannel acceptChannel = channel.accept();

        while (true) {
            int readSize = 0;
            while (readSize < msgLength) {
                // 读取client发送的数据并累加读取字节数
                long read = acceptChannel.read(byteBuffers);
                readSize += read;
                System.out.println("read size : " + readSize);
                Arrays.asList(byteBuffers).forEach(v -> System.out.println("position = " + v.position() + " limit = " + v.limit()));

            }

            Arrays.asList(byteBuffers).forEach(Buffer::flip);

            // 将数据返回给client
            int writeSize = 0;
            while (writeSize < msgLength) {
                writeSize += acceptChannel.write(byteBuffers);
            }
            Arrays.asList(byteBuffers).forEach(Buffer::clear);
            System.out.println(readSize + " ---- " + writeSize + " --- " + msgLength);
        }


    }
}
