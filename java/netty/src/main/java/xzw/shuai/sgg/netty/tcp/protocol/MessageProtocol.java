package xzw.shuai.sgg.netty.tcp.protocol;

/**
 * 协议包
 * @author xuzhiwen
 */
public class MessageProtocol {
    private int len;
    private byte[] data;

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
