package xzw.shuai.hm.netty.protocol.message.impls;

import xzw.shuai.hm.netty.protocol.message.AbstractResponseMessage;

public class LoginResponseMessage extends AbstractResponseMessage {

    public LoginResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }
}