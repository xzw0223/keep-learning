package xzw.shuai.hm.netty.chatgroup.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xzw.shuai.hm.netty.chatgroup.message.impls.LoginRequestMessage;
import xzw.shuai.hm.netty.chatgroup.message.impls.LoginResponseMessage;
import xzw.shuai.hm.netty.chatgroup.server.service.UserServiceFactory;
import xzw.shuai.hm.netty.chatgroup.server.session.SessionFactory;

@ChannelHandler.Sharable
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestMessage msg) throws Exception {
        boolean login = UserServiceFactory.getUserService()
                .login(msg.getUsername(), msg.getPassword());
        LoginResponseMessage responseMessage;
        if (login) {
            SessionFactory.getSession().bind(channelHandlerContext.channel(), msg.getUsername());
            responseMessage = new LoginResponseMessage(login, "登陆成功");
        } else {
            responseMessage = new LoginResponseMessage(login, "登陆失败,用户名/密码错误");
        }
        channelHandlerContext.writeAndFlush(responseMessage);
    }
}
