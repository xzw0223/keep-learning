package xzw.shuai.java.io.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class OldIOTest {

    public static void main(String[] args) {
        new Thread(OldIOTest::server).start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(OldIOTest::client).start();

    }
    public static void client() {
        try {
            Socket socket = new Socket("localhost",6666);
            DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
            String fileName = "/Users/xuzhiwen/Desktop/54262251-3D85-415E-A7FE-0E47E02B3B17.png";
            FileInputStream fis = new FileInputStream(fileName);

            byte[] bytes = new byte[1024];
            int read=0;
            int count=0;
            long l = System.currentTimeMillis();
            while ((read=fis.read(bytes))>0){
                count+=read;
                stream.write(bytes);
            }
            System.out.println(count + "  " + (System.currentTimeMillis()-l));



        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void server() {
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            Socket accept = serverSocket.accept();
            DataInputStream inputStream = new DataInputStream(accept.getInputStream());
            byte[] bytes = new byte[1024];
            int read =0;

            while (-1 !=(read = inputStream.read(bytes))){

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
