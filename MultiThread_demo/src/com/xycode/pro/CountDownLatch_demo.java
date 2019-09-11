package com.xycode.pro;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class CountDownLatch_demo {
	Random r=new Random();
	public long Tasks(int nThread,final Runnable task) throws InterruptedException {
		final CountDownLatch startGate=new CountDownLatch(1);
		final CountDownLatch endGate=new CountDownLatch(nThread);
		for(int i=0;i<nThread;++i) {
			Thread t=new Thread() {
				public void run() {
					try {
						startGate.await();
						try {
							task.run();
						}finally {
							endGate.countDown();
						}
					}catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
		}
		
		long start=System.currentTimeMillis();
		startGate.countDown();
		endGate.await();
		long end=System.currentTimeMillis();
		return end-start;
	}
	

	
	public static void main(String[] args) throws InterruptedException {
		CountDownLatch_demo c=new CountDownLatch_demo();
		Runnable task=new Runnable() {
			
			@Override
			public void run() {
				for(int i=0;i<1000000;++i) {
					c.r.nextInt(1000000);
				}
			}
		};
		
		System.out.println("total time: "+c.Tasks(10,task)+" ms.");
		
		long start=System.currentTimeMillis();
		for(int i=0;i<1000000;++i) {
			c.r.nextInt(1000000);
		}
		long end=System.currentTimeMillis();
		System.out.println("single task consume: "+(end-start)+" ms.");
		
	}


}
