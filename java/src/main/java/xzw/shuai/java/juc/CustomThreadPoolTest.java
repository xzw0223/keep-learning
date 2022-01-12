package xzw.shuai.java.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 拒绝策略
 */
interface RejectPolicy<T> {

    void reject(BlockingQueue<T> taskQueue, T e);

}

@Slf4j

public class CustomThreadPoolTest {
    public static void main(String[] args) {
        final ThreadPool threadPool = new ThreadPool(
                2, 1000, TimeUnit.MILLISECONDS, 10,
//                (RejectPolicy<Runnable>) BlockingQueue::put
                    (RejectPolicy<Runnable>) (q, e) -> {

                        // todo 不同策略等待方式

                       // q.put(e);
                         boolean offer = q.offer(e, 10, TimeUnit.SECONDS);
                       // ...
                    }
        );
        for (int i = 0; i < 15; i++) {
            int j = i;
//            threadPool.execute(()->  log.debug(String.valueOf(j)));
            threadPool.execute(() -> {
                        try {
                            TimeUnit.SECONDS.sleep(100000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        log.debug(String.valueOf(j));
                    }
            );

        }
    }

}

@Slf4j
class ThreadPool {
    private final Set<Worker> workers;
    private final BlockingQueue<Runnable> taskQueue;
    private final int coreSize;
    private final long timeout;
    private final TimeUnit unit;
    private final int capacity;

    private final RejectPolicy<Runnable> rejectPolicy;


    public ThreadPool(int coreSize, long timeout, TimeUnit unit,
                      int capacity, RejectPolicy rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.unit = unit;
        this.capacity = capacity;
        this.taskQueue = new BlockingQueue<>(capacity);
        this.rejectPolicy = rejectPolicy;
        workers = new HashSet<>();
    }

    public void execute(Runnable task) {

        synchronized (workers) {
            // 如果线程数据没超过最大线程数控制,则创建一个线程运行任务,否则将任务加入队列等待执行
            if (workers.size() < coreSize) {
                final Worker worker = new Worker(task);
                worker.start();
                workers.add(worker);
                log.debug("添加线程:{} 和 task:{} ", worker, task);
            } else {
                log.debug("task:{} put taskQueue", task);
                taskQueue.tryPut(rejectPolicy, task);
            }
        }
    }

    class Worker extends Thread {
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 不等于null   || 或者 take ==nulll
//            while (task != null || (task = taskQueue.take()) != null) {
            while (task != null || (task = taskQueue.poll(timeout, unit)) != null) {
                try {
                    log.debug("start task....");
                    task.run();
                    log.debug("succeed task !!!");
                } catch (Exception ignored) {
                } finally {
                    task = null;
                }
            }

            // 退出循环,删除该线程
            synchronized (workers) {
                final int size1 = workers.size();
                workers.remove(this);
                final int size2 = workers.size();
                log.debug("remove this  size1:{},size2:{}", size1, size2);
            }

        }
    }
}

class BlockingQueue<T> {
    private final Deque<T> tasks = new ArrayDeque<>();
    private final ReentrantLock lock = new ReentrantLock();
    // 生成者使用
    private final Condition fullWaitSet = lock.newCondition();
    // 消费者使用
    private final Condition emptyWaitSet = lock.newCondition();
    private final int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    // 带超时的拿取数据
    public T poll(long timeout, TimeUnit unit) {

        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            // 轮训等待任务
            while (tasks.isEmpty()) {
                try {
                    if (nanos <= 0) {
                        return null;
                    }
                    // 返回剩余的时间
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 获取头元素 并返回
            final T t = tasks.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }

    }

    // 堵塞的获取数据
    public T take() {

        lock.lock();
        try {
            // 轮训等待任务
            while (tasks.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 获取头元素 并返回
            final T t = tasks.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }

    }

    public void put(T e) {

        lock.lock();
        try {
            if (tasks.size() == capacity) {
                try {
                    fullWaitSet.await();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            tasks.addLast(e);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }

    }

    public boolean offer(T e, long timeout, TimeUnit unit) {

        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            if (tasks.size() == capacity) {
                try {
                    if (nanos <= 0) {
                        return false;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            tasks.addLast(e);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }


    }

    public int size() {
        lock.lock();
        try {
            return tasks.size();
        } finally {
            lock.unlock();
        }
    }

    public void tryPut(RejectPolicy<T> rejectPolicy, T e) {
        lock.lock();
        try {
            if (tasks.size() >= capacity) {
                rejectPolicy.reject(this, e);
            } else {
                // 空闲ing
                tasks.addLast(e);
                emptyWaitSet.signal();
            }


        } finally {
            lock.unlock();
        }
    }
}
