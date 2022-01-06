package xzw.shuai.java.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class GuardedTest {


    public static void main(String[] args) {
        //  test1();

    }

    private static void test1() {
        GuardedObject guardedObject = new GuardedObject();

        new Thread(() -> {
            StringBuilder sb = new StringBuilder();
            log.debug("处理数据");
            for (int i = 0; i < 30; i++) {
                sb.append("i");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("完成处理");
            guardedObject.compete(sb);
        }).start();

        // 阻塞等待结果
        // 一个线程等待另一个线程的结果,  可以任务异步处理,同步等待结果
        log.debug("等待结果");
        // main线程被阻塞
        Object o = guardedObject.get();
        log.debug(o.toString().length() + "");
    }

    static class Mailboxes {
        static AtomicInteger id = new AtomicInteger(0);
       static Map<Integer, GuardedObject> boxes = new ConcurrentHashMap<>();

        private static int generateId() {
            return id.incrementAndGet();
        }

        public static GuardedObject createGuardedObject() {
            GuardedObject guardedObject = new GuardedObject(generateId());
            boxes.put(guardedObject.getId(), guardedObject);
            return guardedObject;
        }
        public static GuardedObject getGuardedObject(int id){
            return boxes.remove(id);
        }

        public static Set<Integer> ids() {
            return boxes.keySet();
        }
    }

    static class GuardedObject {
        int id;
        Object response;

        public GuardedObject() {
        }

        public GuardedObject(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object get() {
            synchronized (this) {
                while (response == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response;
        }

        public Object get(long timeout) {

            long start = System.currentTimeMillis();
            long passedTime = 0;
            synchronized (this) {
                while (response == null) {
                    try {
                        // 用于防止虚假唤醒
                        long waitTime = timeout - passedTime;
                        // 超过用时则退出循环
                        if (waitTime < 0) {
                            break;
                        }
                        this.wait(waitTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 获取循环用时
                    passedTime = System.currentTimeMillis() - start;
                }
            }
            return response;
        }

        public void compete(Object o) {

            synchronized (this) {
                response = o;
                // 唤醒等待线程
                notifyAll();
            }

        }
    }
}
