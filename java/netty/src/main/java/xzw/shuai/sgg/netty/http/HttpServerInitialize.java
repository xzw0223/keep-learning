package xzw.shuai.sgg.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author xuzhiwen
 */
public class HttpServerInitialize extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        // 构建pipeline
        ChannelPipeline pipeline = ch.pipeline();
        // 加入http编解码器
        pipeline.addLast("myHttpServerCodec", new HttpServerCodec())
                .addLast("myHttpServerHandler", new HttpServerHandler());
    }
}
