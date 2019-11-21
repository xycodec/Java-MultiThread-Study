package com.xycode.Executor;

public class MyRequest {
	private String name;
	private int id;
	public MyRequest(String name, int id) {
		super();
		this.name = name;
		this.id = id;
	}

	
	public void task() {
		System.out.println(Thread.currentThread().getName()+" executes "+this);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return "{ Request from "+name+ ", TaskId="+id+"}";
	}

}
