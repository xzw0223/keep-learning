package xzw.shuai.hm.utils;

import xzw.shuai.hm.netty.chatgroup.protocol.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static final Properties PROP;

    static {
        InputStream is = ConfigUtil.class.getResourceAsStream("/application.properties");
        PROP = new Properties();
        try {
            PROP.load(is);
        } catch (IOException e) {
            System.out.println("加载配置文件失败");
        }
    }

    public static Serializer.Algorithm getSerializerAlgorithm() {
        String serializeType = PROP.getProperty("serializer.algorithm");
        if (serializeType == null) {
            return Serializer.Algorithm.Java;
        } else {
            return Serializer.Algorithm.valueOf(serializeType);
        }
    }

    public static int getServerPort() {
        String port = PROP.getProperty("server.port");
        return port == null ? 8080 : Integer.parseInt(port);
    }
}
