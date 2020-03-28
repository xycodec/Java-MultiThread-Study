package com.xycode.executor.myThreadPool;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * ClassName: MyThreadPool
 *
 * @Author: xycode
 * @Date: 2020/2/26
 * @Description: this is description of the MyThreadPool class
 **/
public class MyThreadPool {
    private static final Logger logger=Logger.getLogger("MyLogger");
    //任务队列
    private MyBlockingQueue<Runnable> taskQueue;

    //存放任务执行单元的容器
    private final Set<Worker> workers=new HashSet<>();

    //核心线程数
    private int coreSize;

    //任务等待的超时时间
    private long timeout;
    private TimeUnit timeUnit;

    //拒绝策略
    private MyRejectStrategy rejectStrategy;

    public MyThreadPool(int coreSize, long timeout, TimeUnit timeUnit,int queueCapacity) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;

        this.taskQueue=new MyBlockingQueue<>(queueCapacity);
        //默认拒绝策略,死等(实际上没有拒绝)
        this.rejectStrategy= (queue, element) -> queue.put(element);
    }

    public MyThreadPool(int coreSize, long timeout, TimeUnit timeUnit,int queueCapacity,MyRejectStrategy rejectStrategy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;

        this.taskQueue=new MyBlockingQueue<>(queueCapacity);

        this.rejectStrategy=rejectStrategy;
    }

    public void execute(Runnable task){
        synchronized (workers){//这里针对workers的操作非线程安全,所以加上同步
            if(workers.size()<coreSize){
                Worker worker=new Worker(task);
                System.out.println("create Worker for task");
                workers.add(worker);
                worker.start();
            }else{
                System.out.println("add task into taskQueue");
//                taskQueue.put(task);//taskQueue已经是线程安全的了
                //执行自定义的拒绝策略
                taskQueue.putWithReject(rejectStrategy,task);
            }
        }
    }

    //test
    public static void main(String[] args) {
        MyThreadPool pool=new MyThreadPool(3, 1, TimeUnit.SECONDS, 4, new MyRejectStrategy() {
            //自定义拒绝策略,直接抛出异常,并拒绝任务
            @Override
            public void reject(MyBlockingQueue queue, Object element) throws Exception {
                throw new Exception("task is rejected");
            }
        });
        for(int i=0;i<10;++i){
            pool.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("-->"+Thread.currentThread().getName()+" execute task");
            });
        }

    }


    //执行单元,一个对Thread的封装
    class Worker extends Thread{
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            if(task!=null) task.run();
            //执行完自己的task后,再从任务队列中取task来执行,使用take的话,实际上这里会一直阻塞住
            while ((task=taskQueue.poll(timeout,timeUnit))!=null){
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            synchronized (workers){
                System.out.println("remove worker");
                workers.remove(this);
            }
        }
    }

}

