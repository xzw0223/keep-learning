package xzw.shuai.hm.nio.netty.rpc;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import xzw.shuai.hm.nio.netty.chatgroup.message.rpc.RpcRequestMessage;
import xzw.shuai.hm.nio.netty.chatgroup.message.rpc.RpcResponseMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) {
        System.out.println(1111111111);
        RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
        rpcResponseMessage.setSequenceId(msg.getSequenceId());
        try {
            HelloService service = (HelloService) ServiceFactory.getService(Class.forName(msg.getInterfaceName()));

            Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object invoke = method.invoke(service, msg.getParameterValue());
            System.out.println(invoke);
            rpcResponseMessage.setReturnValue(invoke);
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            rpcResponseMessage.setExceptionValue(new Exception("远程调用出错 cause : " + e.getCause() .getMessage()));
        }
        ctx.writeAndFlush(rpcResponseMessage);
    }
}
