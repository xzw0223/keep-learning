package xzw.shuai.flink.sink.clickhouse.util;

import ru.yandex.clickhouse.BalancedClickhouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseQueryParam;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ClickhouseUtil {

    private static final int MAX_RETRY = 3;

    /**
     * 获取clickhouse连接,尝试三次则抛出异常
     */
    public static Connection getConnection(String url, String username, String password) throws SQLException {
        // TODO 判断输入 url name psd
        Properties properties = new Properties();

        properties.put(ClickHouseQueryParam.USER.getKey(), username);
        properties.put(ClickHouseQueryParam.PASSWORD.getKey(), password);
        Connection connection = null;

        for (int i = 0; ; i++) {
            try {
                connection = createDataSource(url, properties).getConnection();
                try (Statement statement = connection.createStatement()) {
                    statement.execute("select 1");
                    break;
                }
            } catch (SQLException e) {

                if (connection != null) {
                    connection.close();
                }
                if (i == MAX_RETRY - 1) {
                    throw e;
                } else {
                    // 睡一会继续
                    sleep();
                }

            }
        }

        System.out.println("ok 我被获取");

        return connection;
    }

    private static void sleep() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private static DataSource createDataSource(String url, Properties properties) {
        BalancedClickhouseDataSource dataSource;

        // TODO 需要重铸
        if (properties.size() < 1) {
            throw new RuntimeException("死了死了死了");
        } else if (properties.size() < 2) {
            dataSource = new BalancedClickhouseDataSource(url);
        } else {
            dataSource = new BalancedClickhouseDataSource(url, properties);
        }
        return dataSource;
    }


    public static void main(String[] args) throws SQLException {
        Connection connection = getConnection("jdbc:clickhouse://localhost:8123/default", "default", "123456");
        System.out.println(connection);
    }
}
