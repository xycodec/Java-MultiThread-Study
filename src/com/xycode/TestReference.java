package com.xycode;

import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;
import org.testng.Assert;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: Test
 *
 * @Author: xycode
 * @Date: 2019/11/9
 * @Description: this is description of the Test class
 **/
public class TestReference {
    static class test{
        int i=0;
        byte[] array=new byte[100];
    }

    @Test
    public void test_1(){
        Map<String,String> mp1=new ConcurrentHashMap<String, String>(){
            {
                put("1","one");
                put("2","two");
                put("3","three");
            }
        };
        Map<String,String> mp2=new ConcurrentHashMap<String, String>(){
            {
                put("one","1");
                put("two","2");
                put("three","3");
            }
        };
        Assert.assertEquals(mp1.get("xxx"),null);
        Assert.assertEquals(mp2.get("xxx"),null);
        System.out.println(mp1);
        System.out.println(mp2);
        Assert.assertEquals(mp1.containsKey("1"),true);
        Assert.assertEquals(mp2.containsKey("one"),true);
        for(String key:mp1.keySet()){
            Assert.assertNotEquals(mp2.remove(mp1.remove(key)),null);
        }
        Assert.assertEquals(mp1.isEmpty(),true);
        Assert.assertEquals(mp2.isEmpty(),true);

    }

    public static void main(String[] args) {

//        Semaphore semaphore=new Semaphore(1);
//        try {
//            semaphore.acquire();
//            semaphore.acquire();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }finally {
//            semaphore.release();
//            semaphore.release();
//        }
//
//        ReentrantLock lock=new ReentrantLock();
//        lock.lock();
//        Condition condition=lock.newCondition();
//        try {
//            condition.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        lock.unlock();

        //幽灵引用示例
        Object o=new Object();
        ReferenceQueue<Object> referenceQueue=new ReferenceQueue<>();
        PhantomReference phantomReference=new PhantomReference(o,referenceQueue);

        System.out.println(o);
        System.out.println(phantomReference);
        System.out.println(phantomReference.get());
        System.out.println(referenceQueue.poll());

        System.out.println("-------------------------");

        o=null;
        System.gc();
        System.out.println(o);
        System.out.println(phantomReference);
        System.out.println(phantomReference.get());
        System.out.println(referenceQueue.poll());
    }
}
