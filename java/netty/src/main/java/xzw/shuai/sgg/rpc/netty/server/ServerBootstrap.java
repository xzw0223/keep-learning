package xzw.shuai.sgg.rpc.netty.server;

/**
 * 服务的启动者
 * @author xuzhiwen
 */
public class ServerBootstrap {
    public static void main(String[] args) {
        NettyServer.startServer0("127.0.0.1",6666);
    }
}
