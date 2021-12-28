package xzw.shuai.hm.nio.netty.test;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * @author Administrator
 */
public class EmbeddedChannelTest {
    public static void main(String[] args) {

        ChannelInboundHandlerAdapter h1 =null;
        ChannelInboundHandlerAdapter h2 =null;
        ChannelOutboundHandlerAdapter h3 =null;
        ChannelOutboundHandlerAdapter h4 =null;


    // 可以通过这个channel来测试我们入栈和出栈
        EmbeddedChannel embeddedChannel =
                new EmbeddedChannel(h1,h2,h3,h4);

        // 模拟入栈
        embeddedChannel.writeInbound("");

        // 模拟出栈
        embeddedChannel.writeOneInbound("");

    }
}
