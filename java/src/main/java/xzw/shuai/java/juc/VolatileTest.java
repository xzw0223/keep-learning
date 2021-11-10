package xzw.shuai.java.juc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VolatileTest {

    private volatile static int num = 0;

    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            while (num == 0) {

            }
        }).start();
        TimeUnit.SECONDS.sleep(1);
        num = 1;
        System.out.println(num);

        // Map<String, String> map1 = new ConcurrentHashMap<>();
        Map<String, String> map2 = new HashMap<>();
        //  map1.put("a",null);
        map2.put("a", null);
    }
}
