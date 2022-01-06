package xzw.shuai.java.juc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadTwoPhase {
    static int i = 0;

    public static void main(String[] args) throws Exception {
        // test1();
        test2();
    }




    public static void test2()  {
        TwoPhaseTermination tpt = new TwoPhaseTermination();
        tpt.start();
        sleep(3);
        tpt.stop();
    }
    public static void test1()  {
        Thread t1 = new Thread(() -> {
            log.debug("start");
            sleep(1);
            log.debug("end");
            i = 10;
        });
        t1.start();
        // 用于等待线程结果结束
//        t1.join();
        log.debug("i:{}", i);
    }

    public static void sleep(int i) {
        try {
            TimeUnit.SECONDS.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Slf4j
    static class TwoPhaseTermination{
      private Thread monitor;
      public void start(){
          monitor=new Thread(()->{
              while (true){
                  Thread currentThread = Thread.currentThread();
                  if(currentThread.isInterrupted()){
                      log.debug("处理后事");
                      break;
                  }
                  try {
                      Thread.sleep(1000);
                      log.debug("一顿处理...");
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                      log.debug("中断异常,重新设置标记,优雅退出");
                      currentThread.interrupt();
                  }

              }
          });
          monitor.start();
      }
      public void stop(){
          monitor.interrupt();
      }

    }
}
