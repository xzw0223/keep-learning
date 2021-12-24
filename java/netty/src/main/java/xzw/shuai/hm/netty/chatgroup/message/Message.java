package xzw.shuai.hm.netty.chatgroup.message;


import xzw.shuai.hm.netty.chatgroup.message.impls.*;
import xzw.shuai.hm.netty.chatgroup.message.rpc.RpcRequestMessage;
import xzw.shuai.hm.netty.chatgroup.message.rpc.RpcResponseMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public abstract class Message implements Serializable {
    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public static Class<?> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    private int sequenceId;


    private int messageType;

    public abstract int getMessageType();

    public static final int LoginRequestMessage = 0;
    public static final int LoginResponseMessage = 1;
    public static final int ChatRequestMessage = 2;
    public static final int ChatResponseMessage = 3;
    public static final int GroupCreateRequestMessage = 4;
    public static final int GroupCreateResponseMessage = 5;
    public static final int GroupJoinRequestMessage = 6;
    public static final int GroupJoinResponseMessage = 7;
    public static final int GroupQuitRequestMessage = 8;
    public static final int GroupQuitResponseMessage = 9;
    public static final int GroupChatRequestMessage = 10;
    public static final int GroupChatResponseMessage = 11;
    public static final int GroupMembersRequestMessage = 12;
    public static final int GroupMembersResponseMessage = 13;

    public static final int PingMessage = 14;
    public static final int PongMessage = 15;

    public static final int RPC_MESSAGE_TYPE_REQUEST = 101;
    public static final int  RPC_MESSAGE_TYPE_RESPONSE = 102;

    private static final Map<Integer, Class<?>> messageClasses = new HashMap<>();
    static {
        messageClasses.put(LoginRequestMessage, LoginRequestMessage.class);
        messageClasses.put(LoginResponseMessage, LoginResponseMessage.class);
        messageClasses.put(ChatRequestMessage, xzw.shuai.hm.netty.chatgroup.message.impls.ChatRequestMessage.class);
        messageClasses.put(ChatResponseMessage, xzw.shuai.hm.netty.chatgroup.message.impls.ChatResponseMessage.class);
        messageClasses.put(GroupCreateRequestMessage, xzw.shuai.hm.netty.chatgroup.message.impls.GroupCreateRequestMessage.class);
        messageClasses.put(GroupCreateResponseMessage, GroupCreateResponseMessage.class);
        messageClasses.put(GroupJoinRequestMessage, GroupJoinRequestMessage.class);
        messageClasses.put(GroupJoinResponseMessage, GroupJoinResponseMessage.class);
        messageClasses.put(GroupQuitRequestMessage, GroupQuitRequestMessage.class);
        messageClasses.put(GroupQuitResponseMessage, GroupQuitResponseMessage.class);
        messageClasses.put(GroupChatRequestMessage, xzw.shuai.hm.netty.chatgroup.message.impls.GroupChatRequestMessage.class);
        messageClasses.put(GroupChatResponseMessage, xzw.shuai.hm.netty.chatgroup.message.impls.GroupChatResponseMessage.class);
        messageClasses.put(GroupMembersRequestMessage, GroupMembersRequestMessage.class);
        messageClasses.put(GroupMembersResponseMessage, GroupMembersResponseMessage.class);

        messageClasses.put(RPC_MESSAGE_TYPE_REQUEST, RpcRequestMessage.class);
        messageClasses.put(RPC_MESSAGE_TYPE_RESPONSE, RpcResponseMessage.class);
    }
}

