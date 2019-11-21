package com.xycode.Executor.pro;

import java.util.Random;

public class MyClient extends Thread{
	private final MyChannel channel;
	private static final Random r=new Random();
	public MyClient(String name,MyChannel channel) {
		super(name);
		this.channel = channel;
	}

	@Override
	public void run() {
		int cnt=0;
		while(true) {
			Request request;
			if(r.nextDouble()>0.5)
				request=new MyRequest(super.getName(),++cnt);
			else
				request=new MyRequest2(super.getName(), ++cnt);
			channel.putRequest(request);
			try {
				Thread.sleep(450);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
