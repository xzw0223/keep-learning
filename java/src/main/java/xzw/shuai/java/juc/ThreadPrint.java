package xzw.shuai.java.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPrint {
       volatile static boolean run = true;
    public static void main(String[] args) throws InterruptedException {


        final Thread thread = new Thread(() -> {
            while (run) {
                final long l = System.nanoTime();
                System.out.println((l - System.nanoTime()));
            }
        });
        thread.start();
        thread.join();
      //  awaitSignalTest();
      //  parkUnparkTest();
    }
   static Thread t1,t2 ,t3;
    public static void parkUnparkTest(){
        final ParkUnpark pu = new ParkUnpark(5);


        t1= new Thread(() -> pu.print("a", t2));
        t2= new Thread(() -> pu.print("b", t3));
        t3= new Thread(() -> pu.print("c\n", t1));

        t1.start();
        t2.start();
        t3.start();

        LockSupport.unpark(t1);
    }
    static class ParkUnpark{
        int loopNum;

        public ParkUnpark(int loopNum) {
            this.loopNum = loopNum;
        }

        public void print(String str , Thread next){
            for (int i = 0; i < loopNum; i++) {
                LockSupport.park();
                System.out.print(str);
                LockSupport.unpark(next);
            }
        }
    }



    public static void awaitSignalTest(){
        final AwaitSignal as = new AwaitSignal(5);
        final Condition c1 = as.newCondition();
        final Condition c2 = as.newCondition();
        final Condition c3 = as.newCondition();
        new Thread(()->{as.print("a",c1,c2);}).start();
        new Thread(()->{as.print("b",c2,c3);}).start();
        new Thread(()->{as.print("c \n",c3,c1);}).start();

        as.lock();
        try {
            Thread.sleep(1000);
            c1.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            as.unlock();
        }

    }
    static class AwaitSignal extends ReentrantLock {
        private final int loopNum;

        public AwaitSignal(int ln) {
            loopNum = ln;
        }

        public void print(String str, Condition cur, Condition next) {
            for (int i = 0; i < loopNum; i++) {
                lock();
                try {
                    cur.await();
                    System.out.print(str);

                    next.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    unlock();
                }
            }
        }
    }
}
