package com.xycode.countEx;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName: SynchronizedCount
 *
 * @Author: xycode
 * @Date: 2020/3/28
 * @Description: 使用synchronized + wait/notify实现3个线程交替计数
 **/
public class SynchronizedCount {
    private static final int threshold=123;
    private volatile int state=1;
    private AtomicInteger cnt=new AtomicInteger(1);
    public void countA(){
        while(cnt.intValue()<threshold-1){
            synchronized (this) {
                while (state != 1) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getId() + ": "+cnt.getAndIncrement());
                state = 2;
                this.notifyAll();
            }
        }
    }

    public void countB(){
        while (cnt.intValue()<threshold-1){
            synchronized (this) {
                while (state != 2) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getId() + ": "+cnt.getAndIncrement());
                state = 3;
                this.notifyAll();
            }
        }

    }

    public void countC(){
        while (cnt.intValue()<threshold-1){
            synchronized (this) {
                while (state != 3) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getId() + ": "+cnt.getAndIncrement());
                state = 1;
                this.notifyAll();
            }
        }
    }

    public static void main(String[] args) {
        SynchronizedCount synchronizedCount =new SynchronizedCount();
        new Thread(synchronizedCount::countA).start();
        new Thread(synchronizedCount::countB).start();
        new Thread(synchronizedCount::countC).start();
    }
}
