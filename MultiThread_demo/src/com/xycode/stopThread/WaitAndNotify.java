package com.xycode.stopThread;
/*
 * wait,notify一般是和synchronized搭配使用
 * 在线程T1中,object.wait(),T1不光会wait这个object,而且T1会释放object的锁
 * 而对于object.notify(),线程必须先获得object的锁才能notify
 * wait与notify都是由某个对象来调用的,并对调用的当前线程起作用
 */

public class WaitAndNotify {
	final static Object o=new Object();
	public static class T1 implements Runnable{

		@Override
		public void run() {
			synchronized (o) {
				System.out.println(System.currentTimeMillis()+": T1 start.");
				try {
					System.out.println(System.currentTimeMillis()+": T1 wait for o.");
					o.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(System.currentTimeMillis()+": T1 end.");
			}
		}
		
	}
	
	public static class T2 implements Runnable{

		@Override
		public void run() {
			synchronized (o) {
				System.out.println(System.currentTimeMillis()+": T2 start,notify T1.");
				o.notify();
				System.out.println(System.currentTimeMillis()+": T2 end.");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}
	public static void main(String[] args) {
		new Thread(new T1()).start();
		new Thread(new T2()).start();
		//T1会延迟2s才end,因为T2:o.notify()后,没有释放o的锁,并且sleep了2s,2s之后T1才得到o的锁,此时T1才能继续执行并end
	}

}
