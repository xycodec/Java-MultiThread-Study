package com.xycode.ThreadWork;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo2 implements Runnable{
	static ReentrantLock lock1=new ReentrantLock();
	static ReentrantLock lock2=new ReentrantLock();
	int lock_flag;
	
	public ReentrantLockDemo2(int lock_flag) {
		super();
		this.lock_flag = lock_flag;
	}

	@Override
	public void run() {
		if(lock_flag==1) {
			try {
				lock1.lockInterruptibly();//lock,优先响应中断,可以通过发送中断信号来解除死锁
				Thread.sleep(500);//这里sleep,使线程更容易产生死锁
				lock2.lockInterruptibly();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else {
			try {
				lock2.lockInterruptibly();
				Thread.sleep(500);
				lock1.lockInterruptibly();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(lock1.isHeldByCurrentThread()) lock1.unlock();
		if(lock2.isHeldByCurrentThread()) lock2.unlock();
		System.out.println("Thread-"+Thread.currentThread().getId()+" exit!");
	}
	
	public static void main(String[] args) throws InterruptedException {
		Thread t1=new Thread(new ReentrantLockDemo2(1));
		Thread t2=new Thread(new ReentrantLockDemo2(2));
		t1.start();t2.start();
		Thread.sleep(1000);
		t2.interrupt();//t2线程被中断,t2将会放弃对锁的申请,并且释放已持有的锁
	}

}
