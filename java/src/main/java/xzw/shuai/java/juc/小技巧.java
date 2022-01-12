package xzw.shuai.java.juc;

import java.util.Arrays;
import java.util.Random;

public class 小技巧 {
  public static void main(String[] args) {

    final Random random = new Random();
    String[] s = new String[1];
    for (int i = 0; i < 100; i++) {
      try {
        Thread.sleep(random.nextInt(100));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      s[0] = i + "%";
      // \r 会回退  覆盖之前的内容
      System.out.print("\r" + Arrays.toString(s));
    }
  }
}
