package com.xycode.aqsLock;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: MyLock
 *
 * @Author: xycode
 * @Date: 2019/10/30
 * @Description: this is description of the MyLock class
 **/
public class MyLock implements Lock {
    private MySync sync;

    public MyLock() {
        this.sync = new MySync();
    }

    @Override
    public void lock() {
        sync.acquire(1);//锁计数加一
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1,unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);//锁计数减一
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }


    private static class MySync extends AbstractQueuedSynchronizer {
        @Override
        protected boolean tryAcquire(int arg) {//arg是锁计数增量
            if(getState()==0){//state==0说明没有其它线程占用锁
                if(compareAndSetState(0,arg)){//CAS操作,将state设置为arg
                    setExclusiveOwnerThread(Thread.currentThread());//设置为当前线程独占这个锁
                    return true;//成功获得锁
                }
            }else if(Thread.currentThread()==getExclusiveOwnerThread()){//同一个线程多次lock,即重入锁
                setState(getState()+1);//当前线程的锁计数加一
                return true;
            }
            return false;//获锁失败
        }

        @Override
        protected boolean tryRelease(int arg) {
            //lock与unlock是一一对应的,即必须是同一个线程执行
            if(Thread.currentThread()!=getExclusiveOwnerThread()){
                throw new RuntimeException("unlock error");
            }
            int state=getState()-arg;
            boolean released=false;
            if(state==0){//锁计数为0,释放成功
                setExclusiveOwnerThread(null);
                released=true;
            }
            setState(state);
            return released;
        }

        final ConditionObject newCondition(){
            return new ConditionObject();
        }
    }


    @Test
    public void testWithoutMyLock(){
        final int[] cnt={0};
        Thread[] t=new Thread[10];
        final CountDownLatch countDownLatch=new CountDownLatch(t.length);
        for(int i=0;i<t.length;++i){
            t[i]= new Thread(() -> {
                for(int i1 = 0; i1 <10000; ++i1){
                    ++cnt[0];
                }
                countDownLatch.countDown();
            });
        }
        for (Thread thread : t) thread.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(cnt[0],10*10000);
    }

    @Test
    public void testWithMyLock(){
        final int[] cnt = {0};
        Thread[] t=new Thread[10];
        final CyclicBarrier cyclicBarrier=new CyclicBarrier(t.length+1);
        MyLock lock=new MyLock();
        for(int i=0;i<t.length;++i){
            t[i]= new Thread(() -> {
                try {
                    lock.lock();//可重入锁
                    lock.lock();
                    for(int i1 = 0; i1 <10000; ++i1){
                        ++cnt[0];
                    }
                }finally {
                    lock.unlock();
                    lock.unlock();
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        for (Thread thread : t) thread.start();
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(cnt[0],10*10000);
    }

    public static void main(String[] args) {
        ReentrantLock lock=new ReentrantLock();
        lock.newCondition();
    }

}
