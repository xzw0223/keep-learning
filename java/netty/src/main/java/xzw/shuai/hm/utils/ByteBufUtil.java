package xzw.shuai.hm.utils;

import io.netty.buffer.ByteBuf;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class ByteBufUtil {

    public static void log(ByteBuf buf){
        int length = buf.readableBytes();
        int rows = length/16 +(length%15 ==0?0:1)+4;
        StringBuilder sb = new StringBuilder();
        sb.append("read index : ").append(buf.readerIndex())
                .append(" write index : ") .append(buf.writerIndex())
                .append(" capacity : ").append(buf.capacity())
                .append(NEWLINE)
        ;
        appendPrettyHexDump(sb,buf);
        System.out.println(sb.toString());

    }
}
