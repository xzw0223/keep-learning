package xzw.shuai.io.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author xuzhiwen
 */
public class NIOServer {
    public static void main(String[] args) throws Exception {

        // 获取选择器
        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.bind(new InetSocketAddress(6666));
        // 设置非堵塞
        serverSocketChannel.configureBlocking(false);

        // 注册到选择器
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 等待客户端连接/操作
        while (true) {

            int select = selector.select(1000);
            if (select == 0) {
                System.out.println("等待一秒啥也没获取跳了跳了");
                continue;
            }

            // selectionKey : 标识 一个channel与一个selector的注册关系
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();

                if (selectionKey.isAcceptable()) {
                    System.out.println("accept事件触发");
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    // 注册channel到选择器,并关联接一个buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }

                if (selectionKey.isReadable()) {
                    System.out.println("rade事件触发");
                    // 从该key中获取注册的channel,该channel是由我们上面注册的
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    // 这个是上面传入的buffer
                    ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                    // 读数据
                    channel.read(buffer);
                    System.out.println(new String(buffer.array()));
                    buffer.clear();

                }

                // 删除处理完的key,防止重复处理
                keyIterator.remove();
            }

        }

    }




}
