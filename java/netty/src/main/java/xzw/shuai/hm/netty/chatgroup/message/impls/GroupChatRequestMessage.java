package xzw.shuai.hm.netty.chatgroup.message.impls;

import lombok.EqualsAndHashCode;
import xzw.shuai.hm.netty.chatgroup.message.Message;
import lombok.Data;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GroupChatRequestMessage extends Message {
    private String content;
    private String groupName;
    private String from;

    public GroupChatRequestMessage(String from, String groupName, String content) {
        this.content = content;
        this.groupName = groupName;
        this.from = from;
    }

    @Override
    public int getMessageType() {
        return GroupChatRequestMessage;
    }
}