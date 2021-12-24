package xzw.shuai.hm.netty.chatgroup.protocol;

import java.io.*;

/**
 * 用于扩展序列化的接口
 */
public interface Serializer {
    <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException, ClassNotFoundException;
    <T> byte[] serialize(T obj) throws IOException;

    enum Algorithm implements Serializer{
        /**
         * java的序列化反序列化
         *
         * 记得关闭资源 这里没关闭
         */
        Java{

            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException, ClassNotFoundException {
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bis);


                return (T)ois.readObject();
            }

            @Override
            public <T> byte[] serialize(T obj) throws IOException {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(obj);
                return bos.toByteArray();
            }
        }
    }
}
