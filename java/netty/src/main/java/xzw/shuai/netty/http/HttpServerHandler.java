package xzw.shuai.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author xuzhiwen
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    /**
     * 读取客户端数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 判断是否是httpRequest请求
        isRequest(ctx, msg);

        // else requests ....
    }

    private void isRequest(ChannelHandlerContext ctx, HttpObject msg) throws URISyntaxException {
        if (msg instanceof HttpRequest) {
            System.out.println("类型= " + msg.getClass() + "  客户端地址" + ctx.channel().remoteAddress());
            // 判断是否有要过滤的资源
            if (filterResources((HttpRequest) msg)) {
                return;
            }
            // 回复消息给我浏览器
            returnMsg(ctx);
        }
    }

    private void returnMsg(ChannelHandlerContext ctx) {
        ByteBuf serverMsg = Unpooled.copiedBuffer("服务器告诉你 说徐志文真帅", CharsetUtil.UTF_8);
        FullHttpResponse httpResponse =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK, serverMsg);

        httpResponse.headers()
                       .set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8")
                       .set(HttpHeaderNames.CONTENT_LENGTH, serverMsg.readableBytes());
        // 将构建好的response返回
        ctx.writeAndFlush(httpResponse);
    }

    private boolean filterResources(HttpRequest msg) throws URISyntaxException {
        URI uri = new URI(msg.uri());
        String res = "/favicon.ico";
        if (res.equals(uri.getPath())) {
            System.out.println(res + "该资源请求不做处理");
            return true;
        }
        return false;
    }
}
