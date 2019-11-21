package com.xycode.ThreadWork;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExtend {
	static class MyTask implements Runnable{
		String name;
		
		public MyTask(String name) {
			super();
			this.name = name;
		}

		@Override
		public void run() {
			System.out.println("Excuting Thread-"+name+".");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	public static void main(String[] args) throws InterruptedException {
		ExecutorService es=new ThreadPoolExecutor(5,10,0,TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>()) {
			@Override
			protected void beforeExecute(Thread t, Runnable r) {
				super.beforeExecute(t, r);//默认是空的方法
				System.out.println("准备执行  "+((MyTask)r).name+".");
				
			}
			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);
				System.out.println(((MyTask)r).name+"执行完成.");
			}
			@Override
			protected void terminated() {
				super.terminated();
				System.out.println("ThreadPool exit!");
			}
		};
		for(int i=0;i<10;++i) {
			es.execute(new MyTask("Task-"+(i+1)));
			Thread.sleep(100);
		}
		es.shutdown();
		
	}

}
