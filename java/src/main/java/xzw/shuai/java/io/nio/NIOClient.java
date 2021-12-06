package xzw.shuai.java.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * @author xuzhiwen
 */
public class NIOClient {
    public static void main(String[] args) throws Exception {


        for (int i = 0; i <= 2; i++) {
            String msg = "第" + i + "次说徐志文真棒";

            new Thread(() -> {
                try {
                    client(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            TimeUnit.SECONDS.sleep(5);
        }

    }

    private static void client(String msg) throws IOException {
        // 获取channel并设置非堵塞
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        if (!socketChannel.connect(new InetSocketAddress("localhost", 6666))) {
            while (!socketChannel.finishConnect()) {
                System.out.println("连接需要时间,客户端不会被堵塞");
            }
        }

        System.out.println("client = " + msg);
        socketChannel.write(ByteBuffer.wrap(msg.getBytes()));

        System.in.read();
    }
}
