package xzw.shuai.flink.sink.clickhouse.func;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 用于获取连接的接口
 */
public interface Connectable {
    Connection getConnection() throws SQLException;
}
