package com.xycode.pro;
/*
 * 使用信号量(Semaphore)来限制任务提交的速率
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

public class BoundedExecutor {
	final ExecutorService exec;
	final Semaphore semp;
	
	public BoundedExecutor(ExecutorService exec, Semaphore semp) {
		super();
		this.exec = exec;
		this.semp = semp;
	}
	
	public void shutdown(){
		exec.shutdown();
	}

	public void submitTask(final Runnable task) throws InterruptedException {
		semp.acquire();
		try {
			exec.execute(new Runnable() {//对task重新包装,嵌入信号量计数
				@Override
				public void run() {
					try {
						task.run();
					}finally {
						semp.release();
					}
				}
			});
		}catch(RejectedExecutionException e) {
			semp.release();
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		BoundedExecutor tasks=new BoundedExecutor(Executors.newCachedThreadPool(), new Semaphore(10));
		for(int i=0;i<100;++i) {
			try {
				tasks.submitTask(new Runnable() {
					
					@Override
					public void run() {
						System.out.println("Task-"+Thread.currentThread().getId()+" is Running.");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//能发现明显的间隔式地运行一组任务(一组10个)
		
		tasks.shutdown();
		
	}
	
}
