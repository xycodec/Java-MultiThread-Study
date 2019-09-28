package com.xycode.threadlocal;

public class Client extends Thread{
	
	
	public Client(String name) {
		super(name);
	}

	@Override
	public void run() {
		System.out.println(getName()+" begin");
		for(int i=0;i<10;++i) {
			LogProxy.println("i = "+i);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LogProxy.close();
		System.out.println(getName()+" end");
	}
	
	
}
