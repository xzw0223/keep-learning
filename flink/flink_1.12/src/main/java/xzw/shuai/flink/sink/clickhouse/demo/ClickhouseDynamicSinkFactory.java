package xzw.shuai.flink.sink.clickhouse.demo;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.factories.DynamicTableSinkFactory;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.utils.TableSchemaUtils;
import xzw.shuai.flink.sink.clickhouse.conf.ClickhouseConf;

import java.util.HashSet;
import java.util.Set;

import static xzw.shuai.flink.sink.clickhouse.options.SinkOptions.*;

/**
 * @author xuzhiwen
 */
public class ClickhouseDynamicSinkFactory implements DynamicTableSourceFactory, DynamicTableSinkFactory {

    private static final String IDENTIFIER = "xzw_ck";


    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {
        FactoryUtil.TableFactoryHelper factoryHelper = FactoryUtil
                .createTableFactoryHelper(this, context);

        ReadableConfig options = factoryHelper.getOptions();

        // 参数校验
        factoryHelper.validate();

        // 获取 table schema
        TableSchema tableSchema = TableSchemaUtils
                .getPhysicalSchema(context.getCatalogTable().getSchema());

        return new ClickhouseTableSink(
                ClickhouseConf.generate(options, tableSchema),
                tableSchema
        );
    }

    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        return new ClickhouseTableSouce();
    }

    /**
     * 连接器的标识
     */
    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    /**
     * 设置必须的option
     */
    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        Set<ConfigOption<?>> requiredOptions = new HashSet<>();
        requiredOptions.add(URL);
        requiredOptions.add(TABLE_NAME);
        return requiredOptions;
    }

    /**
     * 设置自己的option 用于解析sql参数
     */
    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        Set<ConfigOption<?>> optionalOptions = new HashSet<>();
        optionalOptions.add(USERNAME);
        optionalOptions.add(PASSWORD);
        optionalOptions.add(SCHEMA);
        optionalOptions.add(SINK_PARALLELISM);
        return optionalOptions;
    }
}
