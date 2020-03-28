package com.xycode.executor.myThreadPool;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * ClassName: MyBlockingQueue
 *
 * @Author: xycode
 * @Date: 2020/2/26
 * @Description: this is description of the MyBlockingQueue class
 **/
public class MyBlockingQueue<T> {
    private static final Logger logger=Logger.getLogger("MyLogger");
    private Deque<T> queue;
    private int capacity;

    private ReentrantLock lock=new ReentrantLock();
    private Condition notEmpty=lock.newCondition();
    private Condition notFull=lock.newCondition();

    private MyRejectStrategy rejectStrategy;

    public MyBlockingQueue(int capacity) {
        this.capacity = capacity;
        queue=new ArrayDeque<>(capacity);
    }

    public MyBlockingQueue(Deque<T> queue, MyRejectStrategy rejectStrategy) {
        this.queue = queue;
        this.rejectStrategy = rejectStrategy;
    }

    //带超时参数的取元素
    public T poll(long timeout, TimeUnit timeUnit){
        //时间单位统一成纳秒
        long nanos=timeUnit.toNanos(timeout);
        lock.lock();
        try {
            while(queue.isEmpty()){
                try {
                    if(nanos<=0) return null;
                    System.out.println("queue is empty");
                    nanos=notEmpty.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T e=queue.removeFirst();
            notFull.signal();
            return e;
        } finally {
            lock.unlock();
        }
    }

    public T take(){
        lock.lock();
        try {
            while(queue.isEmpty()){
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //取出元素了,那肯定就不满了
            T e=queue.removeFirst();
            notFull.signal();
            return e;
        } finally {
            lock.unlock();
        }
    }

    public void put(T element){
        lock.lock();
        try {
            while(queue.size()==capacity){//满的
                try {
                    System.out.println("waiting for add element into queue");
                    notFull.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(element);
            //添加元素了,那肯定就不为空了
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    //带超时参数的添加元素
    public boolean offer(T element,long timeout,TimeUnit timeUnit){
        //时间单位统一成纳秒
        long nanos=timeUnit.toNanos(timeout);
        lock.lock();
        try {
            while(queue.size()==capacity){//满的
                try {
                    if(nanos<=0) return false;
                    nanos=notEmpty.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(element);
            //添加元素了,那肯定就不为空了
            notEmpty.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public int size(){
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public void putWithReject(MyRejectStrategy<T> rejectStrategy,T element){
        lock.lock();
        try {
            if(queue.size()==capacity){//满的
                rejectStrategy.reject(this,element);
            }else{
                queue.addLast(element);
                //添加元素了,那肯定就不为空了
                notEmpty.signal();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
