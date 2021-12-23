package xzw.shuai.hm.netty.protocol.message.impls;

import xzw.shuai.hm.netty.protocol.message.Message;

public class GroupQuitRequestMessage extends Message {
    private String groupName;

    private String username;

    public GroupQuitRequestMessage(String username, String groupName) {
        this.groupName = groupName;
        this.username = username;
    }

    @Override
    public int getMessageType() {
        return GroupQuitRequestMessage;
    }
}