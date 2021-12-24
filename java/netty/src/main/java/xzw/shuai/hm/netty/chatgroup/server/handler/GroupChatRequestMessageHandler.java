package xzw.shuai.hm.netty.chatgroup.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xzw.shuai.hm.netty.chatgroup.message.impls.GroupChatRequestMessage;
import xzw.shuai.hm.netty.chatgroup.message.impls.GroupChatResponseMessage;
import xzw.shuai.hm.netty.chatgroup.server.session.GroupSession;
import xzw.shuai.hm.netty.chatgroup.server.session.GroupSessionFactory;

import java.util.List;
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupChatRequestMessage msg) throws Exception {
        final GroupSession groupSession = GroupSessionFactory.getGroupSession();
        final List<Channel> channelList = groupSession.getMembersChannel(msg.getGroupName());

        for (Channel  channel : channelList){

            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(),msg.getContent()));

        }

    }
}
