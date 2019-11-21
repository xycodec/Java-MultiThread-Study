package com.xycode.actor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

public class MakerClient extends Thread{
	private static final LinkedBlockingQueue<String> shareQueue=new LinkedBlockingQueue<>();
	private final ActiveObject activeObject;
	private final char fillChar;
	
	
	public static LinkedBlockingQueue<String> getSharequeue() {
		return shareQueue;
	}
	
	public MakerClient(String name,ActiveObject activeObject) {
		super(name);
		this.activeObject=activeObject;
		this.fillChar=name.charAt(0);
	}
	
	@Override
	public void run() {
		try {
			for(int i=0;true;++i) {
				Future<String> future=activeObject.makeStr(i+1, fillChar);
				Thread.sleep(10);
				String val=future.get();
				System.out.println(Thread.currentThread().getName()+"--"+(i+1)+", makeStr = "+val);
				shareQueue.add(val);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RejectedExecutionException e) {
			System.out.println(Thread.currentThread().getName()+" makeTask : "+e);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
