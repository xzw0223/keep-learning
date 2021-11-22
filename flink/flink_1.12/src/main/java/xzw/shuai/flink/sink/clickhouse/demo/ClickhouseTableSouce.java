package xzw.shuai.flink.sink.clickhouse.demo;

import org.apache.flink.table.connector.source.DynamicTableSource;

/**
 * 未实现的source
 */
public class ClickhouseTableSouce implements DynamicTableSource {

    @Override
    public DynamicTableSource copy() {
        return null;
    }

    @Override
    public String asSummaryString() {
        return null;
    }
}
