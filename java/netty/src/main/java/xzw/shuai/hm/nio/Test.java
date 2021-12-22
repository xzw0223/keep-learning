package xzw.shuai.hm.nio;

public class Test {
    public static void main(String[] args) {
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
