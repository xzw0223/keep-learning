package xzw.shuai.hm.netty.protocol.message.impls;

import xzw.shuai.hm.netty.protocol.message.Message;

import java.util.Set;

public class GroupMembersResponseMessage extends Message {

    private Set<String> members;

    public GroupMembersResponseMessage(Set<String> members) {
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return GroupMembersResponseMessage;
    }
}
