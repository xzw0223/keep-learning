package xzw.shuai.hm.netty.protocol.message.impls;

import xzw.shuai.hm.netty.protocol.message.AbstractResponseMessage;

public class GroupQuitResponseMessage extends AbstractResponseMessage {
    public GroupQuitResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GroupQuitResponseMessage;
    }
}