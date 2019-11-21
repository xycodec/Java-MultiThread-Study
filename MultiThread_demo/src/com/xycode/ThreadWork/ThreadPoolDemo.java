package com.xycode.ThreadWork;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * ScheduledThreadPool:针对计划任务
 */
public class ThreadPoolDemo {

	public static void main(String[] args) {
		ScheduledExecutorService ses=Executors.newScheduledThreadPool(10);//poolSize=10
		ses.scheduleAtFixedRate(new Runnable() {
			//scheduleAtFixedRate:
			//2s一个周期,初试延时为0,
			//若任务执行时间超过了周期,則上一个任务结束后会立即执行,不会再延时.
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					System.out.println(System.currentTimeMillis()/1000%1009);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}, 0, 2, TimeUnit.SECONDS);
		
//		ses.schedule(new Runnable() {
//			//schedule:
//			//指定延时,然后执行
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(3000);
//					System.out.println(System.currentTimeMillis()/1000%1009);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				
//			}
//		}, 3, TimeUnit.SECONDS);
		
//		ses.scheduleWithFixedDelay(new Runnable() {
//			//scheduleWithFixedDelay:
//			//2s一个周期,初试延时为0,不再是单纯的周期性,实质上是在每个任务结束后插入一个延时(period)
//			//若任务执行时间超过了周期,則上一个任务结束后仍会插入延时(period),不会再延时.
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(3000);
//					System.out.println(System.currentTimeMillis()/1000%1009);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				
//			}
//		}, 0, 2, TimeUnit.SECONDS);
		
		//线程池若不shutdown,将不会自行退出
		//ses.shutdown();//拒绝接受新的任务,并且等待线程池中的任务执行完毕后就退出.
		//ses.shutdownNow();//线程池拒绝接收新提交的任务，同时立马关闭线程池，线程池里的任务不再执行直接退出,慎用!!!
		
		
	}

}
