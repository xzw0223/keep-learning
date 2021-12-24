package xzw.shuai.hm.netty.chatgroup.message.impls;

import lombok.EqualsAndHashCode;
import xzw.shuai.hm.netty.chatgroup.message.Message;
import lombok.Data;
import lombok.ToString;
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
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