package xzw.shuai.student.flink.test;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.io.jdbc.JDBCAppendTableSink;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.io.jdbc.JDBCOptions;
import org.apache.flink.api.java.io.jdbc.JDBCUpsertTableSink;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import xzw.shuai.student.util.FlinkEnvUtil;

public class FlinkTableTest {
    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final StreamTableEnvironment tEnv = FlinkEnvUtil.getTableEnv(env, true, true);

        // 通过mysql source 注册table
        DataStreamSource<Row> source = env.createInput(getJdbcInputFormat());
        Table mysqlTable = tEnv.fromDataStream(
                source,
                "id,phone,qq,wechat");

        // one.直接查询table
        tEnv.toRetractStream(tEnv.sqlQuery("select * from " + mysqlTable + " where id > 1"), Row.class)
                .print();

        // two.注册view 查询view
        tEnv.createTemporaryView("test", mysqlTable);
        tEnv.toRetractStream(tEnv.sqlQuery("select * from test where id>1"), Row.class)
                .print();

        // TODO sink mysql

        // Mode 1.
        // sinkMysql(source);

        // Mode 2.
        mysqlSink2(tEnv);

        env.execute("Flink table demo.");

    }

    private static void mysqlSink2(StreamTableEnvironment tEnv) {
        JDBCOptions jdbcOptions = JDBCOptions.builder()
                .setDBUrl("jdbc:mysql://192.168.106.63:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false")
                .setDriverName("com.mysql.jdbc.Driver")
                .setUsername("root")
                .setPassword("root123456")
                .setTableName("flink_table2")
                .build();

        TableSchema tableSchema = TableSchema.builder()
                .field("id", DataTypes.INT())
                .field("phone", DataTypes.STRING())
                .field("qq", DataTypes.STRING())
                .field("wechat", DataTypes.STRING())
                .build();

        JDBCUpsertTableSink sink = JDBCUpsertTableSink.builder()
                .setOptions(jdbcOptions)
                .setTableSchema(tableSchema)
                .setMaxRetryTimes(3)
                .setFlushMaxSize(10)
                .setFlushIntervalMills(100)
                .build();


        tEnv.registerTableSink("mysqlsink",sink);
        tEnv.sqlUpdate("insert into mysqlsink select * from test");
    }

    private static void sinkMysql1(DataStreamSource<Row> source) {
        JDBCAppendTableSink.builder()
                .setDBUrl("jdbc:mysql://192.168.106.63:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false")
                .setDrivername("com.mysql.jdbc.Driver")
                .setUsername("root")
                .setPassword("root123456")
                .setParameterTypes(BasicTypeInfo.INT_TYPE_INFO,
                        BasicTypeInfo.STRING_TYPE_INFO,
                        BasicTypeInfo.STRING_TYPE_INFO,
                        BasicTypeInfo.STRING_TYPE_INFO)
                .setQuery("INSERT INTO flink_table2 (id,phone,qq,wechat) values (?,?,?,?) ON DUPLICATE KEY UPDATE " +
                        "phone=VALUES(phone),qq=VALUES(qq),wechat=VALUES(wechat)")
                .build()
                .emitDataStream(source);
    }

    /**
     * 获取jdbc input
     */
    private static JDBCInputFormat getJdbcInputFormat() {
        return JDBCInputFormat.buildJDBCInputFormat()
                .setDBUrl("jdbc:mysql://192.168.106.63:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false")
                .setDrivername("com.mysql.jdbc.Driver")
                // 设置类型
                .setRowTypeInfo(
                        new RowTypeInfo(
                                BasicTypeInfo.INT_TYPE_INFO,
                                BasicTypeInfo.STRING_TYPE_INFO,
                                BasicTypeInfo.STRING_TYPE_INFO,
                                BasicTypeInfo.STRING_TYPE_INFO)
                )
                .setUsername("root")
                .setPassword("root123456")
                .setQuery("select * from flink_table;")
                .finish();
    }
}
