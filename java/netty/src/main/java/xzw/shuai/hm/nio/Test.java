package xzw.shuai.hm.nio;

public class Test {
    public static void main(String[] args) {

        System.out.println(12 * 1024);

        long l = 268435456L/1024/1024;
        System.out.println(l);
        System.out.println(1000*60*30);
        for (int i = 0; i < 5; i++) {
            new Thread(Test::extracted).start();
        }
        extracted();
    }

    private static void extracted() {
        while (true){

        }
    }
}
