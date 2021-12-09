package xzw.shuai.sgg.rpc.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import xzw.shuai.sgg.rpc.impl.ServiceImpl;

public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server rpc  msg = " + msg);

        // 定义消息协议,确定是否调用服务
        String prefix = "Service#show#";
        String msgStr = msg.toString();
        if (msgStr.startsWith(prefix)) {
            // 获取传入参数
            String args = msgStr.substring(prefix.length());
            ctx.writeAndFlush(new ServiceImpl().show(args));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
