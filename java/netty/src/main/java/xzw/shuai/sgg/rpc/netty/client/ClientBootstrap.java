package xzw.shuai.sgg.rpc.netty.client;

import xzw.shuai.sgg.rpc.RpcService;

/**
 * @author xuzhiwen
 */
public class ClientBootstrap {

    public static String providerName ="Service#show#";
    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        RpcService bean = (RpcService) client.getBean(RpcService.class, providerName);

        // 调用服务者对象
        System.out.println("我调用show后返回的结果"+bean.show("徐志文帅"));

    }
}
