package xzw.shuai.hm.nio.multi;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static xzw.shuai.hm.utils.ByteBufferUtil.debugAll;

public class MultiThreadServer {

    public static void main(String[] args) throws Exception {

        Thread.currentThread().setName("xzw");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(9090));

        Worker[] workers = new Worker[]{new Worker("w1"), new Worker("w2")};

        AtomicInteger index = new AtomicInteger(0);
        while (true) {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    System.out.println("ok");
                    SocketChannel socketChannel = ssc.accept();
                    socketChannel.configureBlocking(false);
                    System.out.println(socketChannel);
                    workers[index.incrementAndGet() % workers.length]
                            .register(socketChannel);
                }
            }
        }

    }


    static class Worker implements Runnable {

        private Thread thread;
        private Selector selector;
        private String name;
        private volatile boolean started = false;
        private ConcurrentLinkedDeque<Runnable> queue = new ConcurrentLinkedDeque<>();

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel socketChannel) throws IOException {
            if (!started) {
                selector = Selector.open();
                thread = new Thread(this, name);
                thread.start();
            }

            // 通过队列完成main和w1线程直接的数据交互,
            queue.add(() -> {
                try {
                    socketChannel.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });

            // 唤醒线程
            selector.wakeup();
        }

        @Override
        public void run() {

            while (true) {
                try {
                    selector.select();

                    Runnable task = queue.poll();
                    if (task != null) {
                        task.run();
                    }


                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        if (selectionKey.isReadable()) {
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            ByteBuffer buffer = ByteBuffer.allocateDirect(10);
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);

                        }
                    }

                } catch (IOException e) {

                }
            }


        }
    }
}
