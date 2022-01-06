package xzw.shuai.java.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xuzhiwen
 */
@Slf4j
public class ReentrantLockTest {
    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        // 可重入的测试
        // reentrantTest();

        // 可中断的测试
        // interruptable();

        // 尝试获取锁的测试
        tryLockTest();

    }

    private static void tryLockTest() {
        Thread t1 = new Thread(() -> {
            boolean tryLock = lock.tryLock();
            int maxTryUnm = 3;
            int tryUum = 0;
            while (tryUum++ >= maxTryUnm) {
                if (tryLock) {
                    try {
                        log.debug("获取锁");
                    } finally {
                        lock.unlock();
                        break;
                    }
                } else {
                    tryLock = lock.tryLock();
                    log.debug("没有获取锁,再次尝试获取锁");
                }
            }
            log.debug("获取锁的状态 " + tryLock);
        });
        lock.lock();
        try {
            t1.start();
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private static void interruptable() {
        Thread t1 = new Thread(() -> {
            try {
                log.debug("尝试获取锁");
                // 竞争lock,如果没有竞争到进入阻塞队列,可以被打断
                lock.lockInterruptibly();
                // lock.lock(); // 不会被打断
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("没有获取锁,返回");
                return;
            }
            try {
                log.debug("获取到锁");
            } finally {
                lock.unlock();
            }

        });

        lock.lock();
        try {
            t1.start();
            TimeUnit.SECONDS.sleep(1);
            // 进行打断
            t1.interrupt();
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    private static void reentrantTest() {
        lock.lock();
        try {
            // 可重入, 一个锁 可以被一个对象多次加锁, 不可冲入,一个锁被一个对象加锁后无法在被加锁只能等待锁释放
            log.debug("  >>> m1");
            m1();
        } finally {
            lock.unlock();
        }
    }

    public static void m1() {
        lock.lock();
        try {
            m2();
        } finally {
            lock.unlock();
        }
    }

    public static void m2() {
        lock.lock();
        try {
            m3();
        } finally {
            lock.unlock();
        }
    }

    public static void m3() {
        lock.lock();
        try {
            log.debug("m3被调用了");
        } finally {
            lock.unlock();
        }
    }
}
