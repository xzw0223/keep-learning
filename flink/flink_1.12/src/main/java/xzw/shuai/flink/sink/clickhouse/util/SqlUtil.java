package xzw.shuai.flink.sink.clickhouse.util;

import xzw.shuai.flink.sink.clickhouse.conf.ClickhouseConf;
import xzw.shuai.flink.sink.clickhouse.conf.FieldConf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 解析和拼接sql
 *
 * @author xuzhiwen
 */
public class SqlUtil {

    public static String getSql(ClickhouseConf conf) {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        List<String> fieldNames = conf.getColumn().stream()
                .map(FieldConf::getName).collect(Collectors.toList());


        sb.append(conf.getTable().get(0)).append("  (")
                .append(
                        String.join(", ", fieldNames)
                )
                .append(" )").append(" values(");

        for (int i = 0; i < fieldNames.size(); i++) {
            sb.append("?");
            if (i < fieldNames.size() - 1) {
                sb.append(",");
            }
        }

        return sb.append(")").toString();
    }

    public static String getSqlTest() {
        return "insert into test(id,name) values(?,?) ";
    }

    public static void main(String[] args) throws Exception {
        Connection connection = ClickhouseUtil.getConnection("jdbc:clickhouse://localhost:8123/default", "default", "123456");

        PreparedStatement preparedStatement = connection.prepareStatement(getSqlTest());
        preparedStatement.setLong(1, 111212);
        preparedStatement.setString(2, "aaa");
        boolean execute = preparedStatement.execute();
        System.out.println(execute);
    }
}
