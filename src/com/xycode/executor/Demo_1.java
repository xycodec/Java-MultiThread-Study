package com.xycode.executor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: Demo_1
 *
 * @Author: xycode
 * @Date: 2019/11/29
 * @Description: this is description of the Demo_1 class
 **/
public class Demo_1 {
    public static void main(String[] args) {
        ExecutorService es= Executors.newSingleThreadExecutor();
        es.submit(()->{
            int count=0;
            while(true){
                if(count>=2) break;
                System.out.println(Thread.currentThread().getName()+" started");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+" done");
                ++count;
            }

        });

        es.shutdown();
//        try {
//            es.awaitTermination(100,TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        es.shutdownNow();//对于while(true)的任务,除非自行停止,否则是停止不了的
        while(true){
            System.out.println(es.isTerminated());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
