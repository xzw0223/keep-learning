package xzw.shuai.hm.netty.protocol.message.impls;

import xzw.shuai.hm.netty.protocol.message.Message;

public class GroupMembersRequestMessage extends Message {
    private String groupName;

    public GroupMembersRequestMessage(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int getMessageType() {
        return GroupMembersRequestMessage;
    }
}