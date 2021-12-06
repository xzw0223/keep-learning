package xzw.shuai.java.io.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author xuzhiwen
 */
public class GroupChatClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 18181;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username = "徐志文是真的帅";

    public GroupChatClient(String username) {
        this.username = username;
        try {
            this.selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(HOST, PORT));
            socketChannel.configureBlocking(false)
                    .register(selector, SelectionKey.OP_READ);
            ByteBuffer wrap = ByteBuffer.allocate(1024);
            wrap.put(username.getBytes(StandardCharsets.UTF_8));
            wrap.flip();
            socketChannel.write(wrap);
            wrap.clear();
            System.out.println(username + "  ready!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GroupChatClient client = new GroupChatClient(args[0]);

        new Thread(() -> {
            while (true) {
                client.readMessage();
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String msg = scanner.nextLine();
            client.sendMessage(msg);
        }
    }

    public void sendMessage(String msg) {
        msg = username + "说:" + msg;

        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMessage() {
        try {
            if (selector.select(1000) > 0) {
                Iterator<SelectionKey> selectionsIterator = selector.selectedKeys().iterator();
                while (selectionsIterator.hasNext()) {
                    SelectionKey selectionKey = selectionsIterator.next();

                    if (selectionKey.isReadable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        channel.read(buffer);
                        System.out.println(new String(buffer.array()).trim());
                    }
                    selectionsIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
