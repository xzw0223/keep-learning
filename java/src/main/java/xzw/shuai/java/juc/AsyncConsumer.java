package xzw.shuai.java.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class AsyncConsumer {

    public static void main(String[] args) {



        MessageQueue messageQueue = new MessageQueue(2);

        for (int i = 0; i < 3; i++) {
            int id = i;
            new Thread(()->{
                messageQueue.put(new Message(id,"msg-"+id));
            },"producer-"+(i+1)).start();
        }


        new Thread(()->{
            while (true){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                messageQueue.take();

            }
        },"consumer-"+1).start();
    }
@Slf4j
    static class MessageQueue {
        private final LinkedList<Message> queue = new LinkedList<>();
        private final int capacity;

        MessageQueue(int capacity) {
            this.capacity = capacity;
        }

        public Message take() {
            synchronized (queue) {
                try {
                    while (queue.isEmpty()) {
                        log.debug("queue is empty consumer wait.......");
                        queue.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 从头取数据返回
                Message msg = queue.removeFirst();
                // 唤醒其余线程
                queue.notifyAll();
                log.debug("拿到数据");
                return msg;
            }
        }

        public void put(Message msg) {
            synchronized (queue) {
                if (queue.size() >= capacity) {
                    try {
                        log.debug("queue > capacity producer wait ............");
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                queue.addLast(msg);
                queue.notifyAll();
                log.debug("添加数据");
            }
        }
    }

    final static class Message {
        private final int id;
        private final Object value;

        public Message(int id, Object value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "id=" + id +
                    ", value=" + value +
                    '}';
        }

        public int getId() {
            return id;
        }

        public Object getValue() {
            return value;
        }
    }
}
