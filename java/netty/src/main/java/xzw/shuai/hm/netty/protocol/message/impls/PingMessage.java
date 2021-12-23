package xzw.shuai.hm.netty.protocol.message.impls;

import xzw.shuai.hm.netty.protocol.message.Message;

public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PingMessage;
    }
}