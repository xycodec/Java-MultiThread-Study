package com.xycode.synchronizedDemo;

import java.util.concurrent.TimeUnit;

public class LockObject {
    public static int cnt=0;
    public static int cnt2=0;
    public static int cnt3=0;
    public static int cnt4=0;
    //在这里synchronized(this){}与synchronized修饰方法的效果是一样的

    Object lock=new Object();
    public void inc(){
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (this){
            ++cnt;
        }
    }

    public synchronized void inc2(){
        ++cnt2;
    }

    //在这里,每调用inc3()一次,就会新建一个Object实例,对比以上两种使用方式,这种方式的锁粒度更细
    //但是,这种加锁方式是不安全的,因为方法内的Object实例只约束到了当前调用线程,
    //当有多个线程同时调用时,线程彼此之间起不到互斥作用!
//    public void inc3(){
//        Object o=new Object();
//        synchronized (o){
//            ++cnt3;
//        }
//    }

    //上面的inc3()改成这样就线程安全了,因为lock是LockObject的成员变量,和this是一一对应的,
    // 效果也和synchronized(this){}一样
    public void inc3(){
        synchronized (lock){
            ++cnt3;
        }
    }

    //下面这个实际上和synchronized(class){]效果一样了,锁的限制更强了,会约束全体LockObject实例
    //详细的验证见LockStatic.java
    private static Object staticLock=new Object();
    public void inc4(){
        synchronized (staticLock){
            ++cnt4;
        }
    }

    public static void main(String[] args) {
        Thread[] t=new Thread[200];
        //因为只有一个LockObject实例,所以synchronized起到了同步的作用
        LockObject lockObject=new LockObject();
        for(int i=0;i<t.length;++i){
            t[i]=new Thread(){
                @Override
                public void run() {
                    lockObject.inc();
                    lockObject.inc2();
                    lockObject.inc3();
                    lockObject.inc4();
                    System.out.println("cnt="+cnt+", cnt2="+cnt2+", cnt3="+cnt3+", cnt4="+cnt4);
                }
            };
        }
        for(int i=0;i<t.length;++i) {
            t[i].start();

//            //只有线程启动了,才能执行join()或其它的对线程的操作
//            //加了join之后,可以看到是cnt顺序输出了,说明起到了join的作用(插入一段执行过程)
//            //去掉join,cnt输出是乱序的
//            try {
//                t[i].join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
        //休眠一会儿,否则main线程可能会先于其它线程 退出
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(cnt);
        System.out.println(cnt2);
        System.out.println(cnt3);
        System.out.println(cnt4);
    }
}


