package xzw.shuai.student.flink.sink.hive;

import java.io.InputStream;
import java.net.URL;

public class Tr {
    public static void main(String[] args) {
        InputStream conf = Tr.class.getClassLoader().getResourceAsStream("conf1");
        URL conf1 = Tr.class.getClassLoader().getResource("conf");
        URL conf2 = Tr.class.getClassLoader().getResource("conf1");
//        System.out.println(conf);
        System.out.println(conf1.toString());
        System.out.println(conf2);

    }
}
