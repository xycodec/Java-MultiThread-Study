package com.xycode.Executor.pro;

public class Executor_demo_1 {

	public static void main(String[] args) {
		MyChannel channel=new MyChannel();
		channel.start(10);
		new MyClient("Alice",channel).start();
		new MyClient("Bob",channel).start();
		new MyClient("Charis",channel).start();
		
	}

}
