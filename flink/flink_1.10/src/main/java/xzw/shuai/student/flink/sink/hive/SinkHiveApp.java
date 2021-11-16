package xzw.shuai.student.flink.sink.hive;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author xiaoyu
 * @Create 2021/11/9 17:02
 * @Description
 */
public class SinkHiveApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setRestartStrategy(RestartStrategies.failureRateRestart(
                3,
                Time.of(6, TimeUnit.MINUTES),
                Time.of(10, TimeUnit.SECONDS)
        ));

        //配置kafka数据源
        Properties prop = new Properties();
        prop.setProperty("bootstrap.servers", "172.16.100.109:9092");
        prop.setProperty("group.id", "dtstack");

        //{"id":1,"name":"zhnag","age":11}
        env.addSource(new FlinkKafkaConsumer<>("kafka", new SimpleStringSchema(), prop))
                .map((MapFunction<String, Row>) value -> {
                    JSONObject jsonObject = JSON.parseObject(value);
                    return Row.of(jsonObject.getInteger("id"), jsonObject
                            .getString("name"), jsonObject.getInteger("age"));
                }, new RowTypeInfo(
                        new TypeInformation[]{Types.INT, Types.STRING, Types.INT},
                        new String[]{"id", "name", "age"}
                ))
                .addSink(new HiveSink());
        env.execute("kafka->hive");

    }

    static class HiveSink extends RichSinkFunction<Row> {
        private static final Logger LOG = LoggerFactory.getLogger(HiveSink.class);
        private Connection connection;
        private Statement pstmt;
        private transient ScheduledExecutorService scheduler;
        private transient List<Row> rows;

        @Override
        public void open(Configuration parameters) {
            rows = new ArrayList<>();
            try {
                Class.forName("org.apache.hive.jdbc.HiveDriver");
                connection = DriverManager.getConnection("jdbc:hive2://172.16.101.161:10000", "admin", "");
                pstmt = connection.createStatement();
                LOG.info("connection = " + connection);
                this.scheduler = new ScheduledThreadPoolExecutor(1);
                scheduler.scheduleWithFixedDelay(() -> {
                    if (!rows.isEmpty()) {
                        flush();
                    }
                }, 3, 3, TimeUnit.SECONDS);
            } catch (Exception e) {
                LOG.error("error", e);
            }
        }

        @Override
        public synchronized void invoke(Row value, Context context) {
            rows.add(value);
            if (rows.size() > 100) {
                flush();
            }
        }

        public void flush() {
            try {
                StringBuilder sql = new StringBuilder("insert into bigdatadev_kafka_hive values");
                for (Row row : rows) {
                    sql.append("(");
                    sql.append(row.getField(0)).append(", \"").append(row.getField(0))
                            .append("\",").append(row.getField(2)).append(")");
                }
                pstmt.execute(sql.toString());
            } catch (Exception e) {
                LOG.error(e.toString());
            } finally {
                rows.clear();
            }
        }

        @Override
        public void close() throws Exception {
            if (pstmt != null) {
                pstmt.close();
            }
            if (connection != null) {
                connection.close();
            }
            if (scheduler == null) {
                scheduler.shutdown();
            }
        }
    }
}