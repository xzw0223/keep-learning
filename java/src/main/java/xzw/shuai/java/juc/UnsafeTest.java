package xzw.shuai.java.juc;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeTest {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        final Unsafe unsafe = getUnsafe();

        // test1(unsafe);

        final MyAtomicInteger integer = new MyAtomicInteger(10000);
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                for (int j = 0; j < 100; j++) {
                    integer.withdraw(10);
                }
            }).start();
        }
        System.out.println(integer.getBalance());
        for (;;) {

        }
    }


    private static void test1(Unsafe unsafe) throws NoSuchFieldException {
        final long id = unsafe.objectFieldOffset(T.class.getDeclaredField("id"));
        final long name = unsafe.objectFieldOffset(T.class.getDeclaredField("name"));
        final long age = unsafe.objectFieldOffset(T.class.getDeclaredField("age"));
        System.out.println(id + " " + name + " " + age);
    }

    private static Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        final Field field = Unsafe.class.getDeclaredField("theUnsafe");

        field.setAccessible(true);

        // get方法表示从哪个对象获取这个field,但为什么传递为null呢,
        // 由于theUnsafe是静态的,静态的数据class 并不属于某一个对象,所以传递null即可
        return (Unsafe) field.get(null);
    }

    static class T {
        int id;
        String name;
        int age;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "T{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

class MyAtomicInteger {
    private static final Unsafe unsafe;
    private final static long valueOffset;

    static {
        try {
            final Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            valueOffset = unsafe.objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new Error();
        }
    }

    public MyAtomicInteger(int value) {
        this.value = value;
    }

    private volatile int value;

    public int getValue() {
        return value;
    }

    public void decrement(int target) {
        while (true) {
            final int prev = this.value;
            int next = prev - target;
            if (unsafe.compareAndSwapInt(this, valueOffset, prev, next)) {
                break;
            }
        }
    }

    public int getBalance() {
        return getValue();
    }

    public void withdraw(int target) {
        decrement(target);
    }
}