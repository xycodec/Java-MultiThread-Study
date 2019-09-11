package com.xycode.parallelModeAndAlgorithm;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


public class FutureDemo2 {
	static class RealData implements Callable<String>{//jdk支持的future模式
		String para;
		public RealData(String para) {
			super();
			this.para = para;
		}

		@Override
		public String call() throws Exception {
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<10;++i) {
				sb.append(para);
				Thread.sleep(100);
			}
			return sb.toString();
		}
		
	}
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		FutureTask<String> future1=new FutureTask<>(new RealData("xycode "));//jdk支持的future模式
		FutureTask<String> future2=new FutureTask<>(new RealData("xycodec "));
		ExecutorService es=Executors.newCachedThreadPool();
		es.submit(future1);
		es.submit(future2);
		System.out.println("Request Finished!");
		
		//这里可以处理其它业务逻辑
		System.out.println("Handle Others...");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Handle Others Finished!");
		
		//jdk提供的future编程模式简便多了,FutureTask<V> ,Callable<V>
		System.out.println("Data = "+future1.get());//若数据还没算好,会在这儿一直等着,直到数据准备完毕
		System.out.println("Data = "+future2.get());

	}

}
