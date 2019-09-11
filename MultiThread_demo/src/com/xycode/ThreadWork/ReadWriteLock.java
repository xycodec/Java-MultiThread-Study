package com.xycode.ThreadWork;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/*
 * 1.ReentrantReadWriteLock(读写(重入)锁)能使读操作与读操作之间并行,因为读操作与读操作之间没有数据一致性的问题,
 * 读操作与写操作,写操作与写操作之间能必须同步,此时读写锁就相当于普通的锁,
 * 2.在读操作的次数远大于写操作是,读写锁能带来极大的性能提升.
 * 3.实践证明,join()会使这玩意失灵....
 */
public class ReadWriteLock {
	static Lock lock=new ReentrantLock();
	static ReentrantReadWriteLock readWriteLock=new ReentrantReadWriteLock();
	static Lock readLock=readWriteLock.readLock();
	static Lock writeLock=readWriteLock.writeLock();
	int val;
	public Object handleRead(Lock lock) {
		try {
			lock.lock();
			Thread.sleep(1000);//模拟一个耗时的读操作
			System.out.println(val);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
		return val;
	}
	
	public void handleWrite(Lock lock,int data) {
		
		try {
			lock.lock();
			Thread.sleep(1000);//模拟一个耗时的写操作
			val=data;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		final ReadWriteLock demo=new ReadWriteLock();
		Runnable readRunnable=new Runnable() {
			@Override
			public void run() {
				demo.handleRead(readLock);
//				demo.handleRead(lock);
			}
		};
		Runnable writeRunnable=new Runnable() {
			@Override
			public void run() {
				demo.handleWrite(writeLock,new Random().nextInt(100));
//				demo.handleRead(lock,new Random().nextInt(100));
			}
		};
//		long begin=System.currentTimeMillis();
//		for(int i=0;i<10;++i) {
//			if(i<8) {
//				Thread t=new Thread(readRunnable);
//				t.start();
//				t.join();//main线程要等待t线程执行结束
//			}
//			else {
//				Thread t=new Thread(writeRunnable);
//				t.start();
//				t.join();
//			}
//		}
//		System.out.println("consume time: "+(System.currentTimeMillis()-begin)/1000+"s");
		
		for(int i=0;i<2;++i) {
			Thread t=new Thread(writeRunnable);
			t.start();
		}
		
		for(int i=0;i<18;++i) {
			Thread t=new Thread(readRunnable);
			t.start();
		}

		
	}

}
