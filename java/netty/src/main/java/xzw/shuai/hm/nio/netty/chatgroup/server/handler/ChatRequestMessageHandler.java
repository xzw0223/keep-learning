package xzw.shuai.hm.nio.netty.chatgroup.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xzw.shuai.hm.nio.netty.chatgroup.message.impls.ChatRequestMessage;
import xzw.shuai.hm.nio.netty.chatgroup.message.impls.ChatResponseMessage;
import xzw.shuai.hm.nio.netty.chatgroup.server.session.SessionFactory;

@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {

        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);

        //在线
        if(channel!=null){
            ctx.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        }else {
            ctx.writeAndFlush(new ChatResponseMessage(false,"对方不再先"));
        }
        System.out.println(msg);

    }
}
