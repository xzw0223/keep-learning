package xzw.shuai.hm.utils;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class SequenceIdGenerator {

    private static final AtomicInteger id = new AtomicInteger();

    // 递增 和 获取
    public static int nextId() {
        return id.incrementAndGet();
    }

}