package xzw.shuai.hm.nio.netty.rpc.impl;

import xzw.shuai.hm.nio.netty.rpc.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String msg) {
        System.out.println("被调用你了");
        return "收到 msg = " + msg;
    }
}
