package com.xycode.stopThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/*
 * 信号量Semaphore,允许多个线程同时访问
 */
public class SemaphoreDemo implements Runnable{
	final Semaphore semp=new Semaphore(5);//允许最多5个线程同时访问
	@Override
	public void run() {
		try {
			semp.acquire();
			Thread.sleep(1000);//获得一个许可
			System.out.println("Thread-"+Thread.currentThread().getId()+" done!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			semp.release();//释放一个许可
		}
	}
	public static void main(String[] args) throws InterruptedException {
		ExecutorService exec=Executors.newFixedThreadPool(10);
//		ExecutorService exec=Executors.newCachedThreadPool();
		final SemaphoreDemo demo=new SemaphoreDemo();
		for(int i=0;i<20;++i) {
			exec.submit(new Thread(demo));
		}
		exec.awaitTermination(10, TimeUnit.SECONDS);//线程池等待指定时间(等待时间最好超过线程池中任务执行的时间)
		System.out.println("Aloha!");
		exec.shutdown();//拒绝接受新的任务,并且等待线程池中的任务执行完毕后就退出.
		//exec.shutdownNow();//线程池拒绝接收新提交的任务，同时立马关闭线程池，线程池里的任务不再执行直接退出,慎用!!!
	}

}
