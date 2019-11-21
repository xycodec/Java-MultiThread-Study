package com.xycode.Executor;

public class MyWorker extends Thread{
	private final MyChannel channel;

	public MyWorker(String name,MyChannel channel) {
		super(name);
		this.channel = channel;
	}

	@Override
	public void run() {
		while(true) {
			MyRequest request=channel.takeRequest();
			request.task();
		}
	}
	
}
