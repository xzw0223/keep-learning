package xzw.shuai.flink.sink.clickhouse;

import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author xuzhiwen
 */
public class ClickhouseSinkApp {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(
                env,
                EnvironmentSettings.newInstance().
                        //      useBlinkPlanner().
                                build()
        );

        String jobName = "test";
        tEnv.getConfig().getConfiguration().setString(PipelineOptions.NAME, jobName);


        String source = "CREATE TABLE source (\n" +
                "  `id` int,\n" +
                "  `name` varchar\n" +
                ") WITH (\n" +
                "  'connector' = 'kafka',\n" +
                "  'topic' = 'test1',\n" +
                "  'properties.bootstrap.servers' = '172.16.100.109:9092',\n" +
                "  'properties.group.id' = 'xzw',\n" +
                "  'scan.startup.mode' = 'latest-offset',\n" +
                "  'format' = 'json'\n" +
                ")";
        tEnv.executeSql(source);

        String sink = "CREATE TABLE sink (\n" +
                "       `id` INT,\n" +
                "       `name` VARCHAR\n" +
                ") WITH (\n" +
                // 需要自定义接信息参数    --  option
                "      'connector' = 'xzw_ck',\n" +
                "      'url' = 'jdbc:clickhouse://localhost:8123/default',\n" +
                "      'table-name' = 'test',\n" +
                "      'username' = 'default',\n" +
                "      'password' = '123456'\n" +
                "      )";
        tEnv.executeSql(sink);


        TableResult tableResult = executeSql(tEnv, "insert into sink select * from source", "*");

        // 随意使用
        // Optional<JobClient> jobClient = tableResult.getJobClient();

    }

    private static TableResult executeSql(StreamTableEnvironment tEnv, final String... sqls) {
        StatementSet statementSet = tEnv.createStatementSet();

        for (String sql : sqls) {
            if ("*".equals(sql)) {
                continue;
            }
            statementSet.addInsertSql(sql);
        }

        return statementSet.execute();
    }
}
