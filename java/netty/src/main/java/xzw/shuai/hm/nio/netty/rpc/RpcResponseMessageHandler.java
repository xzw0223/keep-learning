package xzw.shuai.hm.nio.netty.rpc;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import xzw.shuai.hm.nio.netty.chatgroup.message.rpc.RpcResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("msg{}" ,msg);
        System.out.println("SAdsadasd");
        Promise<Object> promise = PROMISES.remove(msg.getSequenceId());
        if(promise!=null){
            Exception exceptionValue = msg.getExceptionValue();
            if (exceptionValue!=null){
                 promise.setFailure(exceptionValue);
            }else {
                promise.setSuccess(msg.getReturnValue());
            }
        }

    }
}
