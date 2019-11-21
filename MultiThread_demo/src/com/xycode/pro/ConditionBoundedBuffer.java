package com.xycode.pro;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
/**
 * 基于ReentrantLock与Condition的有界环形缓存(应对多线程环境)
 */
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionBoundedBuffer<T> {
	private final Lock lock=new ReentrantLock();
	private final Condition notFull=lock.newCondition();
	private final Condition notEmpty=lock.newCondition();
	private final T[] items;
	private int head,tail,count;
	
	public ConditionBoundedBuffer(int buf_size) {
		items=(T[])new Object[buf_size];
		head=0;
		tail=0;
		count=0;
	}
	
	public void put(T item) throws InterruptedException {
		lock.lock();//lock保证某一时刻只能有一个线程修改缓存
		try {
			while(count==items.length) {//缓存已满
				notFull.await();//阻塞notFull(主要针对put方法),此时不能再添加了,会一直阻塞在这儿
			}
			items[tail++]=item;
			if(tail==items.length) {//环形缓存
				tail=0;
			}
			++count;
			notEmpty.signal();//对外通告notEmpy(主要针对take方法),此时缓存中有数据,可以take
		}finally {
			lock.unlock();
		}
	}
	
	
	public T take() throws InterruptedException {
		lock.lock();//lock保证某一时刻只能有一个线程修改缓存
		try {
			while(count==0) {
				notEmpty.await();//阻塞notEmpty,因为缓存已经空了,不能再take
			}
			T item=items[head];
			items[head]=null;
			if(++head==items.length) {//环形缓存
				head=0;
			}
			--count;
			notFull.signal();//对外通告notFull(主要针对put方法),此时可以put
			return item;
		}finally {
			lock.unlock();
		}
	}
	
	static final Random r=new Random();
	static class Task<T> implements Runnable{
		ConditionBoundedBuffer<T> cb;
		int mode;
		public Task(ConditionBoundedBuffer<T> cb,int mode) {
			super();
			this.cb = cb;
			this.mode=mode;
		}
		
		@Override
		public void run() {
			if(mode==1) {//mode==1,take...
				for(int i=0;i<1000;++i) {
					try {
						T item=cb.take();
//						System.out.println("take "+item);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}else {//else,put...
				for(int i=0;i<1000;++i) {
					Object o=r.nextInt(1000);
					//java的泛型是基于类型擦除的(先是变成Object类型)
					//所以泛型类型只能强制转换Object类型
					try {
						cb.put((T)o);
//						System.out.println("put "+o);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("Thread-"+Thread.currentThread().getId()+", has finished Task!");
			
		}
	}
	
	public static void main(String[] args) {
		ConditionBoundedBuffer<Integer> cb=new ConditionBoundedBuffer<>(1000);
		ExecutorService es=new TimingThreadPool
				(10, 20, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
		for(int i=0;i<1000;++i) {
			es.submit(new Task(cb, 1));
			es.submit(new Task(cb, 0));
		}
		
		es.shutdown();
		
	}

}
