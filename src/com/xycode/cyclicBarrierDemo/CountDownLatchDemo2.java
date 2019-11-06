package com.xycode.cyclicBarrierDemo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: test
 *
 * @Author: xycode
 * @Date: 2019/10/25
 **/
public class CountDownLatchDemo2 {
    private static CountDownLatch countDownLatch=new CountDownLatch(5);

    public static void main(String[] args) {
        Thread[] t=new Thread[5];
        for(int i=0;i<t.length;++i){
            t[i]=new Thread(){
                @Override
                public void run() {
                    try {
                        System.out.println(System.currentTimeMillis()/1000%1000+": Thread-"+Thread.currentThread().getId()+" start work");
                        TimeUnit.SECONDS.sleep(2);
                        countDownLatch.countDown();

                        System.out.println(System.currentTimeMillis()/1000%1000+": Thread-"+Thread.currentThread().getId()+" finish work");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
        }

        for (Thread value : t) value.start();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        countDownLatch=new CountDownLatch(t.length);

        for(int i=0;i<t.length;++i){
            t[i]=new Thread(){
                @Override
                public void run() {
                    try {
                        System.out.println(System.currentTimeMillis()/1000%1000+": Thread-"+Thread.currentThread().getId()+" start work");
                        TimeUnit.SECONDS.sleep(2);
                        countDownLatch.countDown();

                        System.out.println(System.currentTimeMillis()/1000%1000+": Thread-"+Thread.currentThread().getId()+" finish work");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        for (Thread thread : t) thread.start();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
