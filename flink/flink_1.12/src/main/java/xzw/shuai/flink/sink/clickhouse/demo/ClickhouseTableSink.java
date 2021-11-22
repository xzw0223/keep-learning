package xzw.shuai.flink.sink.clickhouse.demo;

import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.types.RowKind;
import xzw.shuai.flink.sink.clickhouse.conf.ClickhouseConf;
import xzw.shuai.flink.sink.clickhouse.conf.FieldConf;
import xzw.shuai.flink.sink.clickhouse.func.ClickhouseSinkFunc;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于生成sink的
 */
public class ClickhouseTableSink implements DynamicTableSink {

    private ClickhouseConf conf;
    private TableSchema tableSchema;

    public ClickhouseTableSink(ClickhouseConf generate, TableSchema tableSchema) {
        this.conf = generate;
        this.tableSchema = tableSchema;
    }

    @Override
    public ChangelogMode getChangelogMode(ChangelogMode requestedMode) {
        return ChangelogMode.newBuilder()
                .addContainedKind(RowKind.INSERT)
                .addContainedKind(RowKind.DELETE)
                .addContainedKind(RowKind.UPDATE_AFTER)
                .build();
    }

    @Override
    public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {
        RowType rowType = (RowType) tableSchema.toRowDataType().getLogicalType();

        String[] fieldNames = tableSchema.getFieldNames();

        int length = fieldNames.length;
        List<FieldConf> columnList = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            FieldConf field = new FieldConf();
            field.setName(fieldNames[i]);
            field.setType(rowType.getTypeAt(i).asSummaryString());
            field.setIndex(i);
            columnList.add(field);
        }

        conf.setColumn(columnList);

        return SinkFunctionProvider.of(new ClickhouseSinkFunc(conf)
                //, conf.getParallelism()
        );
    }

    @Override
    public DynamicTableSink copy() {
        return null;
    }

    @Override
    public String asSummaryString() {
        return null;
    }
}
