package xzw.shuai.hm.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import xzw.shuai.hm.utils.ByteBufUtil;

public class ByByteBuf {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT
//                .directBuffer()
//                .heapBuffer()
                .buffer(10);


        System.out.println(buf.getClass());

        System.out.println(buf);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            sb.append("a");
        }
        buf.writeBytes(sb.toString().getBytes());
        ByteBufUtil.log(buf);
    }
}
