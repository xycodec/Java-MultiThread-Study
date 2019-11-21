package com.xycode.actor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

public class ActiveObjectImpl implements ActiveObject{
	private final ExecutorService es=Executors.newCachedThreadPool();
	
	@Override
	public Future<String> makeStr(int cnt, char fillChar) throws RejectedExecutionException {
		
		//Future模式,submit()先返回一个凭证
		return es.submit(new Callable<String>() {
			
			@Override
			public String call() {
				char[] buf=new char[cnt];
				for(int i=0;i<cnt;++i) {
					buf[i]=fillChar;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return new String(buf);
			}
			
		});
	}

	@Override
	public void displayStr(String str) throws RejectedExecutionException{
		//execute()没有返回值,只是去执行,是Executor接口的方法
		es.execute(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("display: "+str);
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	@Override
	public void shutdown() {
		System.out.println(es+" shutdown!");
		es.shutdown();
	}

}
