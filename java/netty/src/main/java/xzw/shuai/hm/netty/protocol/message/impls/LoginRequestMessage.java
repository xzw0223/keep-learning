package xzw.shuai.hm.netty.protocol.message.impls;

import xzw.shuai.hm.netty.protocol.message.Message;

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