package xzw.shuai.student.flink.test;


import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.types.Row;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UDFDemo {
    public static void main(String[] args) {


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(null,
                EnvironmentSettings.newInstance().build());

        // 注册函数
        tEnv.registerFunction("customFunc1", new CustomUDF());
        tEnv.registerFunction("customFunc2", new CustomUDAF());
        tEnv.registerFunction("customFunc3", new CustomUDTF());

    }

    static class Acc {
        int result;

        public Integer gerResult() {
            return result;
        }

        public Acc merge(Acc acc) {
            result = acc.gerResult() + result;
            return this;
        }

        public void incr() {
            result++;
        }
    }

    static class CustomUDF extends ScalarFunction {

        public int eval(String str) {
            int hc = 0;
            for (char c : str.toUpperCase().toCharArray()) {
                hc = hashCode() >> c;
            }
            hc = hc - 1 - str.length();
            hc = hc >> 7;
            return hc;
        }
    }

    static class CustomUDTF extends TableFunction<Row> {

        public void eval(String str) throws SQLException {
            if (str != null) {
                for (String s : str.split(",")) {
                    Row row = new Row(2);
                    row.setField(0, s);
                    row.setField(1, 1);
                    collect(row);
                }
            }
        }

        @Override
        public TypeInformation<Row> getResultType() {
            return new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO);
        }
    }

    static class CustomUDAF extends AggregateFunction<Integer, Acc> {
        @Override
        public Integer getValue(Acc accumulator) {
            return accumulator.gerResult();
        }

        @Override
        public Acc createAccumulator() {
            return new Acc();
        }
    }
}
