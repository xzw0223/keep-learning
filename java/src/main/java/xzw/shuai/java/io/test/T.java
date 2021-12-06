package xzw.shuai.java.io.test;


public class T {

    public static void main(String[] args) {
        MultiThreadTest multiThreadTest = new MultiThreadTest();
        multiThreadTest.start();
        new SerialTest().start();
    }

    static class MultiThreadTest extends ThreadContextSwitchTest {

        @Override
        public void start() {
            long start = System.currentTimeMillis();

            MyRunnable runnable = new MyRunnable();
            Thread[] threads = new Thread[4];
            for (int i = 0; i < 4; i++) {
                threads[i] = new Thread(runnable);
                threads[i].start();
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            System.out.println("单线程运行时间 : " + (System.currentTimeMillis() - start));
            System.out.println("counter = " + counter);


        }

        class MyRunnable implements Runnable {
            @Override
            public void run() {
                int i = 100000000;
                while (counter < i) {
                    synchronized (this) {
                        if (counter < i) {
                            incCounter();
                        }
                    }
                }
            }
        }
    }

    static class SerialTest extends ThreadContextSwitchTest {
        @Override
        public void start() {
            long start = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                incCounter();
            }
            System.out.println("单线程运行时间 : " + (System.currentTimeMillis() - start));
            System.out.println("counter = " + counter);
        }
    }
}


abstract class ThreadContextSwitchTest {
    public int count = 100000000;

    public volatile int counter = 0;

    public int getCounter() {
        return counter;
    }

    public void incCounter() {
        counter += 1;
    }

    public abstract void start();

}
