package xzw.shuai.hm.netty.test;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;

/**
 * @author Administrator
 */
public class PipelineTest {


    public static void main(String[] args) {

        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(ServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        // head  1 - 2 -3 -4 -5 -6 -tail

                        // 入栈  1 - 2 - 3
                        addIn(pipeline,"h1",socketChannel);
                        addIn(pipeline,"h2",socketChannel);
                        addIn(pipeline,"h3",socketChannel);

                        // 出栈  6 - 5 - 4
                        // 向channel写入数据得时候才会触发
                        addOut(pipeline,"h4",socketChannel);
                        addOut(pipeline,"h5",socketChannel);
                        addOut(pipeline,"h6",socketChannel);
                    }
                })
                .bind(8080);


    }

    public static void addIn(ChannelPipeline pipeline, String str, SocketChannel socketChannel){
         pipeline.addLast(str, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                System.out.println(str);

                // 会从当前channel 向前找出栈处理器
                // 假如该处理器是 3  则 3 - 2- 1
                //ctx.writeAndFlush();

                // 会从后向前走 即 6-5-4
               // socketChannel.writeAndFlush();

                // 调用此方法会像下面走
                super.channelRead(ctx, msg);
            }
        });
    }   public static void addOut(ChannelPipeline pipeline, String str, SocketChannel socketChannel){
         pipeline.addLast(str, new ChannelOutboundHandlerAdapter() {
             @Override
             public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                 System.out.println(str);
                 super.write(ctx, msg, promise);
             }
         });
    }
}
