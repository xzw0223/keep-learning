package xzw.shuai.hm.netty.protocol.message.impls;

import lombok.EqualsAndHashCode;
import xzw.shuai.hm.netty.protocol.message.Message;
import lombok.Data;
import lombok.ToString;
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
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