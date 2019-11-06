package com.xycode.spinlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ClassName: MySpinLock
 *
 * @Author: xycode
 * @Date: 2019/10/28
 **/
public class MySpinLock {
    private AtomicReference<Thread> atomicReference=new AtomicReference<>(null);
    public void lock(){//实现自旋锁,CPU占用较高
        Thread thread=Thread.currentThread();
        while(!atomicReference.compareAndSet(null,thread)){
//            System.err.println("Thread-"+thread.getId()+" fail to acquire lock");
        }
    }

    public void unlock(){
        Thread thread=Thread.currentThread();
        atomicReference.compareAndSet(thread,null);
    }

    public static void main(String[] args) {
        Thread[] t=new Thread[5];
        MySpinLock spinLock=new MySpinLock();
        for(int i=0;i<t.length;++i){
            t[i]=new Thread(()->{
                try {
                    spinLock.lock();
                    System.out.println("Thread-"+Thread.currentThread().getId()+" acquire lock");
                    System.out.println("Thread-"+Thread.currentThread().getId()+" working...");
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Thread-"+Thread.currentThread().getId()+" release lock");
                    System.out.println();
                }finally {
                    spinLock.unlock();
                }
            });
        }
        for(Thread thread:t) thread.start();
    }
}
