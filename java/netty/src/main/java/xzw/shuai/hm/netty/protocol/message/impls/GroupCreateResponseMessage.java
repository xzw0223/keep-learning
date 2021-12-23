package xzw.shuai.hm.netty.protocol.message.impls;

import xzw.shuai.hm.netty.protocol.message.AbstractResponseMessage;

public class GroupCreateResponseMessage extends AbstractResponseMessage {

    public GroupCreateResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GroupCreateResponseMessage;
    }
}