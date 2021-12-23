package xzw.shuai.hm.netty.protocol.message.impls;

import lombok.EqualsAndHashCode;
import xzw.shuai.hm.netty.protocol.message.Message;
import lombok.Data;
import lombok.ToString;
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class LoginRequestMessage extends Message {
    private String username;
    private String password;

    public LoginRequestMessage() {
    }

    public LoginRequestMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }


    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }
}