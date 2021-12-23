package xzw.shuai.hm.netty.protocol.message.impls;

import xzw.shuai.hm.netty.protocol.message.Message;

import java.util.Set;

public class GroupCreateRequestMessage extends Message {
    private String groupName;
    private Set<String> members;

    public GroupCreateRequestMessage(String groupName, Set<String> members) {
        this.groupName = groupName;
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return GroupCreateRequestMessage;
    }
}