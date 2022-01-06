package xzw.shuai.java.juc;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * @author xuzhiwen
 */
@Slf4j
public class Test {
    public static void main(String[] args) throws Exception {

        ClassLayout classLayout = ClassLayout.parseClass(Test.class);
        Object instance = new Object();
        System.out.println(classLayout.toPrintable(instance));

        log.error("aaa");
        Thread thread = Thread.currentThread();
        Thread aaaa = new Thread() {
            @SneakyThrows
            @Override
            public void run() {
                thread.join();
                log.error("aaaa");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        aaaa.start();

        Thread.sleep(50);
        System.out.println(aaaa.getState());

        for (;;) {

        }
    }
}
