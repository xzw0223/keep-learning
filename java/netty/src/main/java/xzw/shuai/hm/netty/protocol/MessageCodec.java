package xzw.shuai.hm.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import xzw.shuai.hm.netty.protocol.message.Message;
import xzw.shuai.hm.netty.protocol.message.impls.LoginRequestMessage;

import java.io.*;
import java.util.List;

public class MessageCodec extends ByteToMessageCodec<Message> {
    public static void main(String[] args) {
        // TODO 测试未通过
        final EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LoggingHandler(LogLevel.DEBUG),
                new MessageCodec()
        );
        LoginRequestMessage message = new LoginRequestMessage("张三", "123456");
        embeddedChannel.writeOutbound(message);

    }

    final int currentVersion = 1;

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        /*
        自定义编码
        魔数 : 用于第一时间判断数据包是否有效
        版本号: 支持协议的升级
        序列化算法 : 消息的序列化方式 　: 如json prtobuf jdk等
        指令类型 : 登录,注册,群聊.. 业务相关
        请求序号 : 提供异步的能力,防止出现乱序处理
        消息长度
        消息
         */
        byte[] messageBytes = serializeMessage(msg);
                // 魔数    4b
        out.writeBytes(new byte[]{'x', 'z', 'w', 's'});
                //         1b
        out   .writeByte(currentVersion);
                // 序列化方式  0 jdk 1 json   1b
        out     .writeByte(0);
                // 指令类型    1b
        out     .writeByte(msg.getMessageType());
                // 请求序号    4b
        out   .writeInt(msg.getSequenceId());

                // TODO 对齐填充2的倍数
        out    .writeByte(0xff);

                // 长度       4b
        out       .writeInt(messageBytes.length);
                // 获取内容的字节数组  jdk方式     nb
        out     .writeBytes(messageBytes);
    }

    private byte[] serializeMessage(Message msg) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        return bos.toByteArray();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 解码

        // 获取魔数
        int magicNum = in.readInt();
        // 获取版本
        byte version = in.readByte();
        // 序列化类型
        byte serializeType = in.readByte();

        byte messageType = in.readByte();
        int sequenceId = in.readInt();

        // 读取填充字节
        in.readByte();

        int length = in.readInt();
        byte[] messageByte = new byte[length];
        in.readBytes(messageByte);

        // 判断序列化类型,根据不同的类型进行序列化
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(messageByte));
        Message message = (Message) ois.readObject();

        System.out.println(magicNum + " " + version + " " + serializeType + " " + messageType + " " +
                sequenceId + " " + length + " " + message);

        out.add(message);
    }
}
