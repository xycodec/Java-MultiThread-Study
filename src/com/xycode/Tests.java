package com.xycode;

import com.xycode.unsafe.UnsafeUtils;
import org.junit.Test;
import org.testng.Assert;

import javax.xml.ws.WebEndpoint;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ClassName: Test
 *
 * @Author: xycode
 * @Date: 2019/11/9
 * @Description: this is description of the Test class
 **/
public class Tests {
    public static class test{
//        int i=j;//static internal class can only access to external class's static variables
                  //non-static internal class can access to external class's non-static/static variables
        byte[] array=new byte[100];
        Tests tests =new Tests();
    }

//    int j=0;
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

    public static String transfer(String s){
        System.out.println("--> transfer...");
        return s.toUpperCase();
    }

    public static String func(){
        try {
            System.out.println("try...");
            String result="returnStr";
//            System.out.println(result);
            return transfer(result);
        }finally {
            System.out.println("finally...");
        }
    }

    public static void main(String[] args) throws InterruptedException {

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

//        //notice: 幽灵引用示例
//        Object o=new Object();
//        ReferenceQueue<Object> referenceQueue=new ReferenceQueue<>();
//        PhantomReference phantomReference=new PhantomReference(o,referenceQueue);
//
//        System.out.println(o);
//        System.out.println(phantomReference);
//        System.out.println(phantomReference.get());
//        System.out.println(referenceQueue.poll());
//
//        System.out.println("-------------------------");
//
//        o=null;
//        System.gc();
//        System.out.println(o);
//        System.out.println(phantomReference);
//        System.out.println(phantomReference.get());
//        System.out.println(referenceQueue.poll());



//        Thread t1=new Thread(()->{
//            System.out.println(Thread.currentThread().getName()+": "+Thread.currentThread().getState());
//            LockSupport.park();
//            System.out.println(Thread.currentThread().getName()+": "+Thread.currentThread().getState());
//        });
//
//        System.out.println(t1.getName()+": "+t1.getState());
//        t1.start();
//        TimeUnit.SECONDS.sleep(1);
//        System.out.println(t1.getName()+": "+t1.getState());
//        TimeUnit.SECONDS.sleep(1);
//        LockSupport.unpark(t1);

        //测试finally
//        System.out.println(func());

//        ExecutorService es=new ThreadPoolExecutor(2, 5,
//                1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(3), new ThreadFactory() {
//            private AtomicInteger atomicInteger=new AtomicInteger(0);
//            @Override
//            public Thread newThread(Runnable r) {
//                System.out.println(atomicInteger.incrementAndGet()+": thread created, ");
//                return new Thread(r,"thread-"+atomicInteger.get());
//            }
//        });
        //notice: 证明超过coreSize的任务会先被添加到任务队列里面
        //        最终线程池的容量: maximumPoolSize + taskQueue.capacity;
//        for(int i=0;i<8;++i){
//            es.submit(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        TimeUnit.SECONDS.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println(Thread.currentThread().getName()+"'s task, "+es.toString());
//                }
//            });
//        }
//
//        es.shutdown();
//
//        System.out.println("main end");

        //notice: 反射调用方法,实际上也能实现AOP,但是这种方式对调用者的侵入性太强,
        //        即必须提供方法名参数(类似这样,invoke(方法名参数)),而不是直接.method()来调用
//        A a=new A("xycode");
//        try {
//            a.getClass().getMethod("sayName").invoke(a);
//        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//            e.printStackTrace();
//        }

        Map<Integer,Integer> mp=new HashMap<>();
        mp.put(1,1234);
        for(Map.Entry entry:mp.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
        mp.forEach((key,value)->System.out.println(key+":"+value));

        Thread t=new Thread();

    }

    static class A{
        String name;
        public A(String name) {
            this.name = name;
        }
        public void sayName(){
            System.out.println(this.name);
        }
    }


}
