package com.xycode.ThreadWork;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo1 implements Runnable{
	//ReentrantLock(boolean fair),默认非公平,fair==true时,代表公平锁,其性能相比非公平锁较差
	public static ReentrantLock lock=new ReentrantLock(false);//重入锁,unlock与lock的次数要一致
	public static int i;
	@Override
	public void run() {
		for(int j=0;j<1e6;++j) {
			try {
				lock.lock();
				lock.lock();//加锁
				++i;
			}finally {
				lock.unlock();//释放锁
				lock.unlock();
			}

		}
		
	}
	public static void main(String[] args) throws InterruptedException {
		Thread t1=new Thread(new ReentrantLockDemo1());
		Thread t2=new Thread(new ReentrantLockDemo1());
		t1.start();t2.start();
		t1.join();t2.join();//join须在start之后
		
		System.out.println(i);
		
	}


}
