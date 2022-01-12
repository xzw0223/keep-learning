package xzw.shuai.java.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/** aqs : abstractQueuedSynchronizer 抽象 队列 同步器 */
@Slf4j
public class AqsTest {
  public static void main(String[] args) {

    System.out.println(TimeUnit.DAYS.toMillis(1));

    final MyLock lock = new MyLock();
    new Thread(
            () -> {
              lock.lock();
              try {
                TimeUnit.SECONDS.sleep(5);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              log.debug("aaaa 释放");
              lock.unlock();
            },
            "t1")
        .start();
    new Thread(
            () -> {
              log.debug("准备加锁");
              lock.lock();

              log.debug("终于加上了锁");
              lock.unlock();
            },
            "t2")
        .start();
  }
}

// 自定义锁 不可冲入
class MyLock implements Lock {
  private final MySync sync = new MySync();

  // 加锁
  @Override
  public void lock() {
    sync.acquire(1);
  }

  // 加锁 可以被打断
  @Override
  public void lockInterruptibly() throws InterruptedException {
    sync.acquireInterruptibly(1);
  }

  // 尝试加锁
  @Override
  public boolean tryLock() {
    return sync.tryAcquire(1);
  }

  // 尝试加锁 带超时
  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    return sync.tryAcquireNanos(1, unit.toNanos(time));
  }

  // 解锁
  @Override
  public void unlock() {
    sync.release(0);
  }

  @Override
  public Condition newCondition() {
    return sync.newCondition();
  }

  // 独占锁
  private static final class MySync extends AbstractQueuedSynchronizer {
    @Override
    protected boolean tryAcquire(int arg) {
      // 更改锁状态
      if (compareAndSetState(0, 1)) {
        // 加上锁,并将owner设置为当前线程
        setExclusiveOwnerThread(Thread.currentThread());
        return true;
      }

      return false;
    }

    @Override
    protected boolean tryRelease(int arg) {
      setExclusiveOwnerThread(null);
      // TODO 细节, state是volatile(会有写屏障)的,所以该代码的指令不会
      //  被重排序,可以将set state放在后面
      setState(0);
      return true;
    }

    // 是否独占锁
    @Override
    protected boolean isHeldExclusively() {
      // ==1 表示持有独占锁
      return getState() == 1;
    }

    public Condition newCondition() {
      return new ConditionObject();
    }
  }
}
