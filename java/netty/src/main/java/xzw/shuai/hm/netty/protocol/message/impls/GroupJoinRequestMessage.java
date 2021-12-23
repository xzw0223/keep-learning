package xzw.shuai.hm.netty.protocol.message.impls;

import xzw.shuai.hm.netty.protocol.message.Message;

public class GroupJoinRequestMessage extends Message {
    private String groupName;

    private String username;

    public GroupJoinRequestMessage(String username, String groupName) {
        this.groupName = groupName;
        this.username = username;
    }

    @Override
    public int getMessageType() {
        return GroupJoinRequestMessage;
    }
}