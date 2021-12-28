package xzw.shuai.hm.nio.netty.test.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import xzw.shuai.hm.utils.ByteBufUtil;

/**
 * slice :
 *      对byteBuf进行拆分成多个byteBuf,
 *      看上有拆分成多个,实际是逻辑上的拆分
 * @author xuzhiwen
 */
public class SliceTest {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeBytes(new byte[]{'a','a','a','a','a','b','b','b','b','b'});
        ByteBufUtil.log(buffer);

        ByteBuf buf1 = buffer.slice(0, 5);
        ByteBuf buf2 = buffer.slice(5, 5);

        ByteBufUtil.log(buf1);
        System.out.println();
        System.out.println();
        ByteBufUtil.log(buf2);
        buf1.setByte(0,'c');

        System.out.println();
        System.out.println();
        ByteBufUtil.log(buffer);
    }
}
