package com.xycode.Executor.pro;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MyChannel {
	private static final int MAX_REQUEST=100;
	private final LinkedBlockingQueue<Request> requestQueue;
	
	ExecutorService workerPool;
	
	public MyChannel() {
		this.requestQueue=new LinkedBlockingQueue<>(MAX_REQUEST);
		this.workerPool=Executors.newCachedThreadPool();
	}
	
	public void start(int workerNum) {
		for(int i=0;i<workerNum;++i) {
			//线程池貌似会把提交的线程的name覆盖掉
			workerPool.submit(new MyWorker("worker-"+(i+1),this));
		}
	}
	
	public void shutdown() {
		workerPool.shutdown();
	}
	
	public synchronized void putRequest(Request request) {
		while(requestQueue.remainingCapacity()<=0) {
			try {
				wait();
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		requestQueue.add(request);
		notifyAll();
	}
	
	public Request takeRequest() {
		Request request=null;
		try {
			request=requestQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return request;
	}
	
}
