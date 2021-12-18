package xzw.shuai.hm.nio.demo;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static xzw.shuai.hm.utils.ByteBufferUtil.debugRead;

/**
 * 阻塞模式
 *
 * @author Administrator
 */
public class BlockServer {

    public static void main(String[] args) throws Exception {

        // 单线程
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(9090));

        ArrayList<SocketChannel> channels = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 设置非阻塞
        // ssc.configureBlocking(false);

        while (true) {

            System.out.println("连接 ...");
            // 接收连接
            SocketChannel socketChannel = ssc.accept();
            System.out.println("连接上" + socketChannel);
            channels.add(socketChannel);

            // 接收数据
            for (SocketChannel channel : channels) {

                // TODO socketChannel 也可以设置非阻塞

                System.out.println("before read" + channel);
                channel.read(buffer);
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                System.out.println("after read" + channel);
            }
        }
    }
}
