package xzw.shuai.hm.netty.chatgroup.message.impls;

import lombok.EqualsAndHashCode;
import xzw.shuai.hm.netty.chatgroup.message.AbstractResponseMessage;
import lombok.Data;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class LoginResponseMessage extends AbstractResponseMessage {

    public LoginResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }
}