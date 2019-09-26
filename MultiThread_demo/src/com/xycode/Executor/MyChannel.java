package com.xycode.Executor;

import java.util.concurrent.LinkedBlockingQueue;

public class MyChannel {
	private static final int MAX_REQUEST=100;
	private final LinkedBlockingQueue<MyRequest> requestQueue;
	
	private final MyWorker[] workerPool;

	public MyChannel(int workerNum) {
		this.requestQueue=new LinkedBlockingQueue<>(MAX_REQUEST);
		this.workerPool=new MyWorker[workerNum];
		
		for(int i=0;i<workerNum;++i) {
			workerPool[i]=new MyWorker("worker-"+(i+1),this);
		}
	}
	
	public void start() {
		for(int i=0;i<workerPool.length;++i) {
			workerPool[i].start();
		}
	}
	
	public synchronized void putRequest(MyRequest request) {
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
	
	public MyRequest takeRequest() {
		MyRequest request=null;
		try {
			request=requestQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return request;
	}
	
}
