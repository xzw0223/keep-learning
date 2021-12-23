package xzw.shuai.hm.netty.protocol.message;

public abstract class AbstractResponseMessage extends Message {
    private boolean success;
    private String reason;

    public AbstractResponseMessage() {
    }

    public AbstractResponseMessage(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }
}
