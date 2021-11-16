package xzw.shuai.student.flink.sink.hive;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.apache.flink.types.Row;
import xzw.shuai.student.util.FlinkEnvUtil;

import java.util.Objects;

/**
 * @author xuzhiwen
 */
public class SqlSinkHiveApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = FlinkEnvUtil.getTableEnv(env, true, true);
        env.setParallelism(1);

        String name = "hcl";
        String defaultDatabase = "default";
        String hiveConfDir = Objects.requireNonNull(SqlSinkHiveApp.class.getClassLoader().getResource("conf")).getPath();
        String version = "1.2.1";
        HiveCatalog hive = new HiveCatalog(name, defaultDatabase, hiveConfDir, version);
        tEnv.registerCatalog("hcl", hive);
        tEnv.useCatalog("hcl");

        tEnv.createTemporaryView("test", test1(env));
        Table table = tEnv.sqlQuery("select * from test");

        tEnv.toAppendStream(table, Row.class).print();



        tEnv.sqlUpdate("insert into xzw_test select * from test");

        env.execute("sql sink hive");
    }

    public static DataStream<Row> test1(StreamExecutionEnvironment env) {
        return env
                .fromElements("a,b", "c,d")
                .map(value -> {
                    Row row = new Row(2);
                    String[] split = value.split(",");
                    for (int i = 0; i < split.length; i++) {
                        row.setField(i, split[i]);
                    }
                    return row;
                }, new RowTypeInfo(
                        new TypeInformation[]{Types.STRING, Types.STRING},
                        new String[]{"co1", "co2"}));

    }
}
