package com.xycode.ThreadWork;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTrace{
	static class DivTask implements Runnable{
		int a,b;
		
		public DivTask(int a, int b) {
			super();
			this.a = a;
			this.b = b;
		}

		@Override
		public void run() {
			System.out.println(a/b);
		}
		
	}
	public static void main(String[] args) {
		//ThreadPoolExecutor(C) extends AbstractExecutorService(C)
		//AbstractExecutorService(C) implements ExecutorService(I)
		//interface(I) ExecutorService extends Executor(I)
		//public interface Executor { void execute(Runnable command); }
		
//		ExecutorService es=new ThreadPoolExecutor(5, 20, 
//				0, TimeUnit.SECONDS, 
//				new LinkedBlockingDeque<Runnable>());
		
		ThreadPoolExecutor es=new ThreadPoolExecutor(5, 20, 
				0, TimeUnit.SECONDS, 
				new LinkedBlockingDeque<Runnable>());
		for(int i=0;i<5;++i) {
			try {
				es.submit(new DivTask(100, i)).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
	}

}
