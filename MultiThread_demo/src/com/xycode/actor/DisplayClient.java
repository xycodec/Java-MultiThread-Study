package com.xycode.actor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

public class DisplayClient extends Thread{
	private static final LinkedBlockingQueue<String> shareQueue=MakerClient.getSharequeue();
	private final ActiveObject activeObject;
	public DisplayClient(String name,ActiveObject activeObject) {
		super(name);
		this.activeObject=activeObject;
	}
	
	@Override
	public void run() {
		try {
			for(int i=0;true;++i) {
				String str=Thread.currentThread().getName()+"--"+(i+1);
				activeObject.displayStr(str+", displayStr = "+shareQueue.take());
				Thread.sleep(200);
			}
		}catch (RejectedExecutionException e) {
			System.out.println(Thread.currentThread().getName()+" displayTask : "+e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
