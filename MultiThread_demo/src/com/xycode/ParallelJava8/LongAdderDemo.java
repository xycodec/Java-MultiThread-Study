package com.xycode.ParallelJava8;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

public class LongAdderDemo {
	static final int MAX_THREAD=10;
	static final int TASK_COUNT=10;
	static final int TARGET_COUNT=10000000;
	ReentrantLock lock=new ReentrantLock();
	long count=0;//普通的加锁同步
	AtomicLong acount=new AtomicLong(0);//原子同步
	LongAdder lacount=new LongAdder();//LongAdder同步(原理:基于原子同步的热点分离)
	
	
	static CountDownLatch cdlsync=new CountDownLatch(TASK_COUNT);//等待指定的线程完成(倒计数器)
	static CountDownLatch cdlatomic=new CountDownLatch(TASK_COUNT);
	static CountDownLatch cdladder=new CountDownLatch(TASK_COUNT);
	
	static class SyncThread implements Runnable{
		String name;
		long start;
		LongAdderDemo out;
		
		public SyncThread(String name, long start, LongAdderDemo out) {
			super();
			this.name = name;
			this.start = start;
			this.out = out;
		}

		@Override
		public void run() {
			
			for(int i=0;i<TARGET_COUNT;++i) {
				out.lock.lock();//这里是为了比较性能,所以才去这种粒度很细的加锁方法,实际上在for循环外面解锁其性能将是最好的
				++out.count;
				out.lock.unlock();
			}
			
			cdlsync.countDown();//表明一个线程完成任务
			
		}
		
	}
	
	public void testSync() throws InterruptedException {
		ExecutorService es=Executors.newFixedThreadPool(MAX_THREAD);
		SyncThread sync=new SyncThread("SyncThread", System.currentTimeMillis(), this);
		for(int i=0;i<TASK_COUNT;++i) {
			es.submit(sync);
		}
		cdlsync.await();
		System.out.println(count);
		System.out.println(sync.name+" spend "+(System.currentTimeMillis()-sync.start)+" ms");
		es.shutdown();
	}
	
	static class AtomicThread implements Runnable{
		String name;
		long start;
		LongAdderDemo out;
		
		public AtomicThread(String name, long start, LongAdderDemo out) {
			super();
			this.name = name;
			this.start = start;
			this.out = out;
		}

		@Override
		public void run() {
			for(int i=0;i<TARGET_COUNT;++i) {
				out.acount.incrementAndGet();
			}
			cdlatomic.countDown();
		}
		
	}
	
	
	public void testAtomic() throws InterruptedException {
		ExecutorService es=Executors.newFixedThreadPool(MAX_THREAD);
		AtomicThread atom=new AtomicThread("AtomicThread", System.currentTimeMillis(), this);
		for(int i=0;i<TASK_COUNT;++i) {
			es.submit(atom);
		}
		cdlatomic.await();
		System.out.println(acount);
		System.out.println(atom.name+" spend "+(System.currentTimeMillis()-atom.start)+" ms");
		es.shutdown();
	}
	
	static class AdderThread implements Runnable{
		String name;
		long start;
		LongAdderDemo out;
		
		public AdderThread(String name, long start, LongAdderDemo out) {
			super();
			this.name = name;
			this.start = start;
			this.out = out;
		}

		@Override
		public void run() {
			for(int i=0;i<TARGET_COUNT;++i) {
				out.lacount.increment();//基于热点分离的加
			}
			cdladder.countDown();
		}
		
	}
	
	
	public void testAdder() throws InterruptedException {
		ExecutorService es=Executors.newFixedThreadPool(MAX_THREAD);
		AdderThread adder=new AdderThread("AdderThread", System.currentTimeMillis(), this);
		for(int i=0;i<TASK_COUNT;++i) {
			es.submit(adder);
		}
		cdladder.await();//等待任务全部完成
		System.out.println(lacount.sum());//因为是热点分离,所以要sum所有的cell,才能得到最终的值
		System.out.println(adder.name+" spend "+(System.currentTimeMillis()-adder.start)+" ms");
		es.shutdown();
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		LongAdderDemo demo=new LongAdderDemo();
		demo.testSync();
		demo.testAtomic();
		demo.testAdder();
	}

}
