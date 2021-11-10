package xzw.shuai.student.util;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.java.StreamTableEnvironment;

public class FlinkEnvUtil {

    public static StreamTableEnvironment getTableEnv(StreamExecutionEnvironment env, boolean isBlink, boolean isStreaming) {
        EnvironmentSettings.Builder builder = EnvironmentSettings.newInstance();
        if (isBlink) builder.useBlinkPlanner();
        if (isStreaming) builder.inStreamingMode();
        return StreamTableEnvironment.create(
                env,
                builder.build()
        );
    }
}
