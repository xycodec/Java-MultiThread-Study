package com.xycode.exchanger;

import java.sql.Time;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: Demo_1
 *
 * @Author: xycode
 * @Date: 2019/10/31
 * @Description: this is description of the Demo_1 class
 **/
public class ExchangerDemo_1 {
    public static void main(String[] args) {
        Exchanger<Long> exchanger=new Exchanger<>();
        final CountDownLatch countDownLatch=new CountDownLatch(2);
        new Thread(){
            @Override
            public void run() {
                Thread thread=Thread.currentThread();
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("Thread-"+thread.getId()+" arrive exchange point...");
                    System.out.println("Thread-"+thread.getId()+" get peer's ID = "+exchanger.exchange(thread.getId()));//交换点
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                Thread thread=Thread.currentThread();
                try {
                    TimeUnit.SECONDS.sleep(3);//这里要慢一点,实际上另一端即使早到达交换点,也会阻塞在那儿,直到这段也到达交换点
                    System.out.println("Thread-"+thread.getId()+" arrive exchange point...");
                    System.out.println("Thread-"+thread.getId()+" get peer's ID = "+exchanger.exchange(thread.getId()));//交换点
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
