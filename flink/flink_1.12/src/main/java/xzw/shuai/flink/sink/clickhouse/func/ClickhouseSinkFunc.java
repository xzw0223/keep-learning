package xzw.shuai.flink.sink.clickhouse.func;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.data.RowData;
import xzw.shuai.flink.sink.clickhouse.conf.ClickhouseConf;
import xzw.shuai.flink.sink.clickhouse.util.ClickhouseUtil;
import xzw.shuai.flink.sink.clickhouse.util.SqlUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * 用于写入clickhouse的sink
 */
public class ClickhouseSinkFunc extends RichSinkFunction<RowData> implements Connectable {
    private final ClickhouseConf conf;
    private PreparedStatement preparedStatement;
    private List<RowData> rowDataList;
    private long batchSize = 1;
    private Connection connection;

    public ClickhouseSinkFunc(ClickhouseConf conf) {
        this.conf = conf;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        connection = getConnection();
        String sql = SqlUtil.getSqlTest();
        preparedStatement = connection.prepareStatement(
                sql
//                SqlUtil.getSqlTest()
        );
    }

    @Override
    public void close() throws Exception {
        super.close();
    }


    @Override
    public void invoke(RowData value, Context context) throws Exception {

        // TODO 一个简单的批量插入
        if (batchSize <= 1) {
            // 写入一条
            setFiled(value);
            preparedStatement.execute();
        } else {
            // 写入多条
            rowDataList.add(value);
            if (rowDataList.size() > batchSize) {
                // 批量写入
                for (RowData rowData : rowDataList) {
                    setFiled(rowData);
                }
            }
        }


    }

    private void setFiled(RowData value) throws SQLException {
        for (int i = 0; i < value.getArity(); i++) {
            set(value, i);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return ClickhouseUtil.getConnection(conf.getJdbcUrl(), conf.getUsername(), conf.getPassword());

    }

    public void set(RowData value, int index) throws SQLException {

        String type = conf.getColumn().get(index).getType();
        if ("INT".equals(type)) {
            int anInt = value.getInt(index);
            System.out.println(anInt);
            System.out.println("-------------" + index);
            int parameterIndex = index + 1;
            System.out.println(parameterIndex);
            preparedStatement.setInt(parameterIndex, anInt);
        } else if ("STRING".equals(type)) {
            preparedStatement.setString(index + 1, value.getString(index).toString());
        }


    }

}
