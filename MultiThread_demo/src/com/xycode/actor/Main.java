package com.xycode.actor;

public class Main {

	public static void main(String[] args) {
		ActiveObject activeObject=ActiveObjectFactory.createActiveObject();
		new MakerClient("Alice", activeObject).start();
		new MakerClient("Bob", activeObject).start();
		new DisplayClient("Charis", activeObject).start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			activeObject.shutdown();//shutdown后再submit/execute任务就会抛出RejectedExecutionException
		}
	}

}
