package xzw.shuai.hm.netty.chatgroup.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import xzw.shuai.hm.netty.chatgroup.message.Message;
import xzw.shuai.hm.utils.ConfigUtil;

import java.io.IOException;
import java.util.List;

/**
 * 可共享的
 * 必须保证没有粘包拆包问题
 *
 * @author xuzhiwen
 */
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    final int currentVersion = 2;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message msg, List<Object> out) throws Exception {
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

        ByteBuf outBuf = channelHandlerContext.alloc().buffer();
        byte[] messageBytes = serializeMessage(msg);
        // 魔数    4b
        outBuf.writeBytes(new byte[]{'x', 'z', 'w', 's'});
        //         1b
        outBuf.writeByte(currentVersion);
        // 序列化方式 1b
        outBuf.writeByte(ConfigUtil.getSerializerAlgorithm().ordinal());
        // 指令类型    1b
        outBuf.writeByte(msg.getMessageType());
        // 请求序号    4b
        outBuf.writeInt(msg.getSequenceId());

        // TODO 对齐填充2的倍数
        outBuf.writeByte(0xff);

        // 长度       4b
        outBuf.writeInt(messageBytes.length);
        // 获取内容的字节数组  jdk方式     nb
        outBuf.writeBytes(messageBytes);

        out.add(outBuf);
    }

    private byte[] serializeMessage(Message msg) throws IOException {
        return ConfigUtil.getSerializerAlgorithm().serialize(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

        // 解码

        // 获取魔数
        int magicNum = in.readInt();
        // 获取版本
        byte version = in.readByte();
        // 序列化类型
        byte serializeAlgorithm = in.readByte();

        byte messageType = in.readByte();
        int sequenceId = in.readInt();

        // 读取填充字节
        in.readByte();

        int length = in.readInt();
        byte[] messageByte = new byte[length];
        in.readBytes(messageByte);



        // 判断序列化类型,根据不同的类型进行序列化
        Object message = Serializer.Algorithm
                .values()[serializeAlgorithm]
                .deserialize(Message.getMessageClass(messageType), messageByte);

        System.out.println(magicNum + " " + version + " " + serializeAlgorithm + " " + messageType + " " +
                sequenceId + " " + length + " " + message);


        out.add(message);
    }
}
