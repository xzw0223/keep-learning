package xzw.shuai.java.io.groupchat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author xuzhiwen
 */
public class GroupChatServer {

    private static final int PORT = 18181;
    private Selector selector;
    private ServerSocketChannel listenChannel;

    // private static final Logger LOG = LoggerFactory.getLogger(GroupChatServer.class);


    public GroupChatServer() {
        try {
            // init selector and serverSocketChannel
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.bind(new InetSocketAddress(PORT))
                    .configureBlocking(false)
                    .register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            // LOG.error("server initialize failed ",e);
        }
    }

    public static void main(String[] args) {
        GroupChatServer server = new GroupChatServer();
        server.listen();
    }

    public void listen() {

        while (true) {
            try {
                int count = selector.select();
                if (count > 0) {
                    // 如果存在则进行处理

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey selectionKey = keyIterator.next();

                        if (selectionKey.isAcceptable()) {
                            isAccept();
                        }

                        if (selectionKey.isReadable()) {
                            readData(selectionKey);
                        }
                        keyIterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // noting
            }
        }


    }

    private void isAccept() throws IOException {
        //                             ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = listenChannel.accept();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.read(buffer);
        System.out.println(new String(buffer.array()).trim() + "上线了");
        buffer.clear();
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void readData(SelectionKey selectionKey) {
        SocketChannel channel = null;

        try {
            channel = (SocketChannel) selectionKey.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int readCount = channel.read(buffer);
            // 如果读取到数据
            if (readCount > 0) {
                String msg = new String(buffer.array());
                System.out.println("from client msg : " + msg.trim());
                // 向其他客户端转发消息
                sendToOtherClient(msg, channel);
            }
        } catch (IOException e) {
            try {
                System.out.println(channel.getRemoteAddress() + "离线了");
                // 取消注册
                selectionKey.cancel();
                // 关闭通道
                channel.close();
            } catch (IOException ex) {
                // LOG.error("cause : ",ex);
            }
        }
    }


    public void sendToOtherClient(String msg, SocketChannel self) throws IOException {
        System.out.println("转发消息中...");
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey selectionKey : keys) {
            SelectableChannel channel = selectionKey.channel();
            // 排除自己
            if (channel instanceof SocketChannel && channel != self) {
                System.out.println("发送1");
                SocketChannel dest = (SocketChannel) channel;
                byte[] msgBytes = msg.getBytes();
                ByteBuffer buffer = ByteBuffer.allocate(msgBytes.length);
                buffer.put(msgBytes);
                buffer.flip();
                dest.write(buffer);
            }
        }

    }
}








