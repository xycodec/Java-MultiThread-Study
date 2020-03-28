package com.xycode.countEx;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: ConditionCount
 *
 * @Author: xycode
 * @Date: 2020/3/28
 * @Description: 使用ReentrantLock + Condition实现3个线程交替计数
 **/
public class ConditionCount {
    private static final int threshold=100;
    private AtomicInteger cnt=new AtomicInteger(1);
    ReentrantLock lock=new ReentrantLock();
    Condition ab=lock.newCondition(),bc=lock.newCondition(),ca=lock.newCondition();
    public void countA(){
        while(true){
            lock.lock();
            try {
                if(cnt.intValue()>threshold) {
                    ab.signal();
                    return;
                }
                ca.await();
                System.out.println(Thread.currentThread().getId() + ": "+cnt.getAndIncrement());
                ab.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }

        }
    }

    public void countB(){
        while(true){
            lock.lock();
            try {
                if(cnt.intValue()>threshold) {
                    bc.signal();
                    return;
                }
                ab.await();
                System.out.println(Thread.currentThread().getId() + ": "+cnt.getAndIncrement());
                bc.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

    public void countC(){
        while(true){
            lock.lock();
            try {
                if(cnt.intValue()>threshold){
                    ca.signal();
                    return;
                }
                bc.await();
                System.out.println(Thread.currentThread().getId() + ": "+cnt.getAndIncrement());
                ca.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ConditionCount syncCount=new ConditionCount();
        new Thread(syncCount::countA).start();
        new Thread(syncCount::countB).start();
        new Thread(syncCount::countC).start();

        syncCount.lock.lock();
        System.out.println("start...");
        syncCount.ca.signal();
        syncCount.lock.unlock();
    }
}
