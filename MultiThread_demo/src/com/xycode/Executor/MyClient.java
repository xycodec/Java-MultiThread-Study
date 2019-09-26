package com.xycode.Executor;

public class MyClient extends Thread{
	private final MyChannel channel;


	public MyClient(String name,MyChannel channel) {
		super(name);
		this.channel = channel;
	}

	@Override
	public void run() {
		int cnt=0;
		while(true) {
			MyRequest request=new MyRequest(super.getName(),++cnt);
			channel.putRequest(request);
			try {
				Thread.sleep(450);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
