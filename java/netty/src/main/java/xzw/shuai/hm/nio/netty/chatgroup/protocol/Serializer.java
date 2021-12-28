package xzw.shuai.hm.nio.netty.chatgroup.protocol;

import com.google.common.base.Charsets;
import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * 用于扩展序列化的接口
 */
public interface Serializer {
    <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException, ClassNotFoundException;

    <T> byte[] serialize(T obj) throws IOException;

    enum Algorithm implements Serializer {
        /**
         * java的序列化反序列化
         * <p>
         * 记得关闭资源 这里没关闭
         */
        Java {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException, ClassNotFoundException {
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bis);


                return (T) ois.readObject();
            }

            @Override
            public <T> byte[] serialize(T obj) throws IOException {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(obj);
                return bos.toByteArray();
            }
        },
        /**
         * json
         */
        Json {
            private final Charset utf8 = Charsets.UTF_8;

            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException, ClassNotFoundException {
                return new GsonBuilder()
                        .registerTypeAdapter(
                                 Class.class, new Serializer.ClassCodec())
                        .create()
                        .fromJson(new String(bytes, utf8), clazz);
            }

            @Override
            public <T> byte[] serialize(T obj) throws IOException {
                return new Gson().toJson(obj).getBytes(utf8);
            }
        }
    }

    class ClassCodec implements JsonDeserializer<Class<?>>, JsonSerializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return Class.forName(json.getAsString());
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getName());
        }
    }
}
