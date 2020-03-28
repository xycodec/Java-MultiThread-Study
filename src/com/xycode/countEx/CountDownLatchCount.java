package com.xycode.countEx;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName: SyncCount
 *
 * @Author: xycode
 * @Date: 2020/3/28
 * @Description: 使用CountDownLatch实现3个线程交替计数
 **/
public class CountDownLatchCount {
    private static final int threshold=100;
    private AtomicInteger cnt=new AtomicInteger(1);
    private CountDownLatch ab=new CountDownLatch(1);
    private CountDownLatch bc=new CountDownLatch(1);
    private CountDownLatch ca=new CountDownLatch(1);
    public void countA(){
        while(true){
            try {
                ca.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(cnt.intValue()>threshold) {
                ab.countDown();
                return;
            }
            ca=new CountDownLatch(1);
            System.out.println(Thread.currentThread().getId() + ": "+cnt.getAndIncrement());
            ab.countDown();
        }
    }

    public void countB(){
        while(true){
            try {
                ab.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(cnt.intValue()>threshold) {
                bc.countDown();
                return;
            }
            ab=new CountDownLatch(1);
            System.out.println(Thread.currentThread().getId() + ": "+cnt.getAndIncrement());
            bc.countDown();
        }
    }

    public void countC(){
        while(true){
            try {
                bc.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(cnt.intValue()>threshold){
                ca.countDown();
                return;
            }
            bc=new CountDownLatch(1);
            System.out.println(Thread.currentThread().getId() + ": "+cnt.getAndIncrement());
            ca.countDown();
        }
    }

    public static void main(String[] args) {
        CountDownLatchCount syncCount=new CountDownLatchCount();
        syncCount.ca.countDown();
        new Thread(syncCount::countA).start();
        new Thread(syncCount::countB).start();
        new Thread(syncCount::countC).start();

    }
}
