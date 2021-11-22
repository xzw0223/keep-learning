package xzw.shuai.flink.sink.clickhouse.conf;

import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.api.TableSchema;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static xzw.shuai.flink.sink.clickhouse.options.SinkOptions.*;

/**
 * @author xuzhiwen
 */
public class ClickhouseConf implements Serializable {
    protected List<String> table;
    /**
     * 连接url
     */
    private String jdbcUrl;
    private String username;
    private String password;
    private String schema;
    private int parallelism;
    private List<FieldConf> column;

    private ClickhouseConf() {
    }

    /**
     * 生成conf
     */
    public static ClickhouseConf generate(ReadableConfig readableConfig, TableSchema schema) {

        ClickhouseConf conf = new ClickhouseConf();

        conf.setJdbcUrl(readableConfig.get(URL));
        conf.setTable(Collections.singletonList(readableConfig.get(TABLE_NAME)));
        conf.setSchema(readableConfig.get(SCHEMA));
        conf.setUsername(readableConfig.get(USERNAME));
        conf.setPassword(readableConfig.get(PASSWORD));
        conf.setParallelism(readableConfig.get(SINK_PARALLELISM));

        return conf;
    }

    public List<FieldConf> getColumn() {
        return column;
    }

    public void setColumn(List<FieldConf> column) {
        this.column = column;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getTable() {
        return table;
    }

    public void setTable(List<String> table) {
        this.table = table;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

}
