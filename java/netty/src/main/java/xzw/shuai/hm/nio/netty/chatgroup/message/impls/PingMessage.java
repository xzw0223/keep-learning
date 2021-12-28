package xzw.shuai.hm.nio.netty.chatgroup.message.impls;

import lombok.EqualsAndHashCode;
import xzw.shuai.hm.nio.netty.chatgroup.message.Message;
import lombok.Data;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PingMessage;
    }
}