package xzw.shuai.java.io.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class NewIOTest {

    public static void main(String[] args) {
        new Thread(NewIOTest::server).start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(NewIOTest::client).start();

    }

    public static void client() {
        try {
            SocketChannel open = SocketChannel.open();
            open.connect(new InetSocketAddress("localhost", 7001));

            // 25519245
            String fileName = "/Users/xuzhiwen/Desktop/54262251-3D85-415E-A7FE-0E47E02B3B17.png";
            FileInputStream fis = new FileInputStream(fileName);
            FileChannel fisChannel = fis.getChannel();
            System.out.println("starting");
            long start = System.currentTimeMillis();
            long c = fisChannel.transferTo(0, fisChannel.size(), open);
            System.out.println("count = " + c + " time = " + (System.currentTimeMillis() - start));

            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void server() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverSocketChannel.bind(new InetSocketAddress(7001)).socket();
            ByteBuffer buffer = ByteBuffer.allocate(2048);

            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                int readCount = 0;
                while (-1 != (readCount = socketChannel.read(buffer))) {
                }
                buffer.rewind();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
