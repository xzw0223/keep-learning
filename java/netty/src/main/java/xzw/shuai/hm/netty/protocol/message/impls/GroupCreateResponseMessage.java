package xzw.shuai.hm.netty.protocol.message.impls;

import lombok.EqualsAndHashCode;
import xzw.shuai.hm.netty.protocol.message.AbstractResponseMessage;
import lombok.Data;
import lombok.ToString;
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class GroupCreateResponseMessage extends AbstractResponseMessage {

    public GroupCreateResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GroupCreateResponseMessage;
    }
}