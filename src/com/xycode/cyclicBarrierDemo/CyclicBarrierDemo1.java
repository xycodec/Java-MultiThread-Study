package com.xycode.cyclicBarrierDemo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierDemo1 {
    /**
     * 相比CountDownLatch,CyclicBarrier的本质区别是等待的主体不一样,CountDownLatch.await()阻塞的是主线程的代码,
     * 而CyclicBarrier.await()阻塞的是任务线程的代码.这样就导致CountDownLatch是在主线程中await任务线程执行完毕
     * 而CyclicBarrier是在任务线程中await其它(所有)任务线程执行完毕
     * 至于像CyclicBarrier可以重用,CountDownLatch不可重用这样的区别,只是细枝末节,不是本质区别.
     */
    private static CyclicBarrier cyclicBarrier=new CyclicBarrier(5);

    public static void main(String[] args) {
        Thread[] t=new Thread[5];
        for(int i=0;i<t.length;++i){
            t[i]=new Thread(){
                @Override
                public void run() {
                    try {
                        System.out.println(System.currentTimeMillis()/1000%1000+": Thread-"+Thread.currentThread().getId()+" start work");
                        TimeUnit.SECONDS.sleep(2);
                        cyclicBarrier.await();
                        System.out.println(System.currentTimeMillis()/1000%1000+": Thread-"+Thread.currentThread().getId()+" finish work");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        for (Thread value : t) value.start();

//        cyclicBarrier.reset();//显式重置,以便CyclicBarrier重用,不过实际上会自动重置...

        for(int i=0;i<t.length;++i){
            t[i]=new Thread(){
                @Override
                public void run() {
                    try {
                        System.out.println(System.currentTimeMillis()/1000%1000+": Thread-"+Thread.currentThread().getId()+" start work");
                        TimeUnit.SECONDS.sleep(2);
                        cyclicBarrier.await();
                        System.out.println(System.currentTimeMillis()/1000%1000+": Thread-"+Thread.currentThread().getId()+" finish work");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        for (Thread thread : t) thread.start();
    }
}
