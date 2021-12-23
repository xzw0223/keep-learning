package xzw.shuai.hm.netty.protocol.message.impls;

import xzw.shuai.hm.netty.protocol.message.Message;

public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return PongMessage;
    }
}