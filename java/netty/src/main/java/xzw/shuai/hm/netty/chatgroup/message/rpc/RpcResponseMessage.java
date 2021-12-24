package xzw.shuai.hm.netty.chatgroup.message.rpc;

import xzw.shuai.hm.netty.chatgroup.message.Message;

public class RpcResponseMessage extends Message {
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}