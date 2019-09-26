package com.xycode.Executor;
/**
 * 实现任务的创建(调用)与任务的执行分离
 * @author xycode
 *
 */
public class Executor_demo_1 {

	public static void main(String[] args) {
		MyChannel channel=new MyChannel(10);
		channel.start();
		new MyClient("Alice",channel).start();
		new MyClient("Bob",channel).start();
		new MyClient("Charis",channel).start();
		
	}

}
