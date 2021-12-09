package xzw.shuai.sgg.rpc.impl;

import xzw.shuai.sgg.rpc.RpcService;

public class ServiceImpl implements RpcService {
    @Override
    public String show(String msg) {
        System.out.println("收到 msg = " + msg);

        if (msg == null || "".equals(msg)) {
            return "我什么也没收到,你确定发送了吗";
        }

        return "     我收到了你的消息  你发送的消息是:[" + msg + "]是这个吗";
    }
}
