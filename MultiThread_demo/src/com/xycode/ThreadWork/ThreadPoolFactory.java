package com.xycode.ThreadWork;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolFactory {
	static class MyTask implements Runnable{

		@Override
		public void run() {
			System.out.println(System.currentTimeMillis()/1000+" : Thread-"+Thread.currentThread().getId());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	public static void main(String[] args) throws InterruptedException {
		ExecutorService es=new ThreadPoolExecutor(5,//corePoolSize
				10,//maximumPoolSize
				0,//线程池中线程数量超过corePoolSize但小于maximumPoolSize,多余的线程保持存活的时间
				TimeUnit.SECONDS,//存活时间的时间单位
				new SynchronousQueue<>(),//任务队列,存放被提交但未执行的任务
				new ThreadFactory() {//创建线程的工厂
					
					@Override
					public Thread newThread(Runnable r) {
						Thread t=new Thread(r);
						t.setDaemon(true);
						System.out.println("create Thread-"+t.getId());
						return t;
					}
				},
			new RejectedExecutionHandler() {//自定义拒绝策略,当线程池中线程数量超过maximumPoolSize时执行的拒绝策略
				
				@Override
				public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
					System.out.println("Thread-"+new Thread(r).getId()+" is rejected.");
				}
			}
		);
		
		for(int i=0;i<20;++i) {
			es.submit(new MyTask());
		}
		//因为线程池中的线程都设置为守护现场,所以在非守护线程main没有结束之前,线程池也一直不会退出
		//线程池等待main线程休眠3s之后才退出
		Thread.sleep(3000);
	}

}
