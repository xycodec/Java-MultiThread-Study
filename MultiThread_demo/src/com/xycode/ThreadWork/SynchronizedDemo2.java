package com.xycode.ThreadWork;

public class SynchronizedDemo2 implements Runnable{

	public static Integer i=0;
	public static SynchronizedDemo2 o=new SynchronizedDemo2();
	@Override
	public void run() {
		for(int j=0;j<10e6;++j) {
			synchronized (o) {
				++i;
			}
			
//			synchronized (i) {//错误的做法,因为Integer本身是不变的,++i导致i的指向不断改变,因此会产生不一致的问题,可能加锁加到了不一样的对象实例上
//				++i;
//			}
			
		}
	
	}
	public static void main(String[] args) throws InterruptedException {
		Thread t1=new Thread(o);
		Thread t2=new Thread(o);
		t1.start();t2.start();
		t1.join();t2.join();//join须在start之后
		
		System.out.println(i);
	}

}
