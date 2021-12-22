package xzw.shuai.hm.nio.multi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.connect(new InetSocketAddress("localhost",9090));

        socketChannel.write(Charset.defaultCharset().encode("xzw1111"));
        for (;;) {

        }
    }
}
