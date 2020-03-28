package com.xycode.unsafe;

import org.testng.annotations.Test;
import sun.misc.Unsafe;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: MyAtomicInteger
 *
 * @Author: xycode
 * @Date: 2020/2/25
 * @Description: this is description of the MyAtomicInteger class
 **/
public class MyAtomicInteger {
    private static final Unsafe UNSAFE;

    private volatile int value;
    private static final long valueOffset;

    public MyAtomicInteger() {
    }

    public MyAtomicInteger(int value) {
        this.value = value;
    }

    static{
        UNSAFE=UnsafeUtils.getUnsafe();
        try {
            //设置value在类中的偏移量
            valueOffset=UNSAFE.objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public int getValue(){
        return value;
    }

    public void increment(int count){
        //while(true) + CAS实现原子操作
        while (true){
            int expectedValue=value;
            int newValue=count+value;
            if(UNSAFE.compareAndSwapInt(this,valueOffset,expectedValue,newValue)) break;
        }
    }

    public void decrement(int count){
        increment(-count);
    }

    @Test
    public void testMyAtomicInteger(){
        MyAtomicInteger myAtomicInteger=new MyAtomicInteger(100);
        for(int i=0;i<10;++i){
            new Thread(()-> myAtomicInteger.increment(5)).start();
            new Thread(()-> myAtomicInteger.decrement(5)).start();
        }
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //若线程安全的话应该会输出100
        System.out.println(myAtomicInteger.getValue());// -> 100
    }
}
