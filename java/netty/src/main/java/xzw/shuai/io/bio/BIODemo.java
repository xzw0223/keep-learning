package xzw.shuai.io.bio;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 传统的bio
 * 弊端 每一个请求都会创建一个线程
 */
public class BIODemo {

    //static Logger log = LoggerFactory.getLogger(BIODemo.class);

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            server();
        }).start();

        new Thread(() -> {
            client();
        }).start();


    }

    private static void client() {
        try {
            int i = 0;
            while (true) {
                System.out.println();
                System.out.println();
                System.out.println(++i +" client被创建");
                Socket socket = new Socket(InetAddress.getLoopbackAddress().getHostAddress(), 6666);

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(("xzw shuai "+i) .getBytes());


                TimeUnit.SECONDS.sleep(2);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void server() {
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();

            ServerSocket serverSocket = new ServerSocket(6666);

            System.out.println("server starting");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("connect to a client");

                // 创建一个线程与之通信  读取数据
                executorService.execute(() -> handler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handler(Socket socket) {
        byte[] buffer = new byte[1024];
        InputStream inputStream = null;
        try {
            while (true) {
                System.out.println("线程" +Thread.currentThread().getName() + "被启动,开始读取数据");
                inputStream = socket.getInputStream();
                int r;
                if ((r = inputStream.read(buffer)) != -1) {
                    System.out.println(Thread.currentThread().getName() +"读取到数据 :" +new String(buffer, 0, r, StandardCharsets.UTF_8));
                }
            }

        } catch (IOException e) {
            // log.error("io error", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                // log.error("close failed ", e);
            }
        }
    }
}
