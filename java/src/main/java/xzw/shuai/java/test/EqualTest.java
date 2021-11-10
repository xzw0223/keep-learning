package xzw.shuai.java.test;

import java.util.ArrayList;

public class EqualTest {

    public static void main(String[] args) throws InterruptedException {
        String  a = "a";
        String b = "a";
        System.out.println(a == b);
        equals(a,b);
        equals(new String("a"),a);


        ArrayList<byte[]> list = new ArrayList<>();
        for (;;){
           list.add(new byte[1023]);

           //Thread.sleep(2000);
        }
    }

    public static void equals(String a ,String b){
        System.out.println(a == b);
    }

}
