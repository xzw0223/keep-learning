package xzw.shuai.sgg.rpc.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;
    private String result;
    /**
     * 调用方法时候传递的参数
     */
    private String args;

    public void setArgs(String args) {
        this.args = args;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 其他方法需要使用此ctx
        context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result = msg.toString();
        // 唤醒等待的线程
        notify();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    public synchronized Object call() throws Exception {
        context.writeAndFlush(args);
        wait();
        return result;
    }

}
