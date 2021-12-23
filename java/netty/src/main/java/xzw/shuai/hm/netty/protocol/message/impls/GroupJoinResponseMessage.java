package xzw.shuai.hm.netty.protocol.message.impls;

import xzw.shuai.hm.netty.protocol.message.AbstractResponseMessage;

public class GroupJoinResponseMessage extends AbstractResponseMessage {

    public GroupJoinResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GroupJoinResponseMessage;
    }
}