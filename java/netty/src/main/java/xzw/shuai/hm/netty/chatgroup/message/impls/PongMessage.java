package xzw.shuai.hm.netty.chatgroup.message.impls;

import lombok.EqualsAndHashCode;
import xzw.shuai.hm.netty.chatgroup.message.Message;
import lombok.Data;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return PongMessage;
    }
}