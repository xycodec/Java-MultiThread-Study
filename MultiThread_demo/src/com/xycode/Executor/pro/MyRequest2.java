package com.xycode.Executor.pro;

public class MyRequest2 implements Request{
	private String name;
	private int id;
	public MyRequest2(String name, int id) {
		super();
		this.name = name;
		this.id = id;
	}

	
	@Override
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
		return "{ Request from "+name+ ", Task2Id="+id+"}";
	}

}
