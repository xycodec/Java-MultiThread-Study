package com.xycode.ParallelJava8;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.StampedLock;

public class StampedLockDemo {
	static class Point implements Runnable{
		static final StampedLock s1=new StampedLock();
		static Random r=new Random();
		double x,y;
		
		public Point(double x, double y) {
			super();
			this.x = x;
			this.y = y;
		}

		void move(double deltaX,double deltaY) {
			long stamp=s1.writeLock();//悲观锁,Exclusively acquires the lock, blocking if necessaryuntil available.
			x+=deltaX;
			y+=deltaY;
			s1.unlock(stamp);//If the lock state matches the given stamp, releases the corresponding mode of the lock.
		}
		
		double distanceFromOrigin() {
			long stamp=s1.tryOptimisticRead();//乐观锁(类似CAS的操作,循环尝试)
			double currentX=x,currentY=y;
			if(s1.validate(stamp)==false) {
				System.out.println("conflict!");
				stamp=s1.readLock();//若中途被抢占过,則转悲观锁
				currentX=x;
				currentY=y;
				s1.unlock(stamp);
			}
			return Math.sqrt(currentX*currentX+currentY*currentY);
		}

		@Override
		public void run() {
			for(int i=0;i<10;++i) {
				move(r.nextDouble()*10,r.nextDouble()*10);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(distanceFromOrigin());
			}
		}
		
	}
	
	public static void main(String[] args) {
		ExecutorService es=Executors.newCachedThreadPool();
		for(int i=0;i<256;++i) {
			es.execute(new Point(0,0));
		}
	}

}
