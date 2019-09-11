package com.xycode.LockWork;
/**
 * AtomicData类,底层原理是CAS,在竞争不是太激烈的情况下,性能优于加锁的方式
 * 更高级的还有AtomicReference<Class>,针对一般的类进行封装,不过要手动的进行CAS操作,CompareAndSet
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


public class AtomicDemo implements Runnable{
	static AtomicInteger i=new AtomicInteger(0);//原子Integer类,初值赋为0,底层原理是CAS
	static int k=0;
	int mode;
	public static ReentrantLock lock=new ReentrantLock(false);
	
	public AtomicDemo(int mode) {
		super();
		this.mode = mode;
	}

	@Override
	public void run() {
		if(mode==0) {
			//1900
			for(int j=0;j<10000000;++j) {
				i.incrementAndGet();//相当于++i,AtomicInteger不支持形如++i,i++的操作,必须通过api
			}
		}else {
			//10ms
			lock.lock();
			for(int j=0;j<10000000;++j) {
				++k;
				
			}
			lock.unlock();
			
			//2000ms
			//AtomicInteger的方法性能比下面这个稍好一些,远不如上面那种更优的加锁方式(锁粗化)
//			for(int j=0;j<10000000;++j) {
//				lock.lock();
//				++k;
//				lock.unlock();
//			}
		}

	}

	public static void main(String[] args) {
//		long b=System.currentTimeMillis();
//		ExecutorService es=new ThreadPoolExecutor(10,10,0,TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>()) {
//			@Override
//			protected void beforeExecute(Thread t, Runnable r) {
//				super.beforeExecute(t, r);//默认是空的方法
//				
//			}
//			@Override
//			protected void afterExecute(Runnable r, Throwable t) {
//				super.afterExecute(r, t);
//
//			}
//			@Override
//			protected void terminated() {
//				super.terminated();//默认是空的方法
//				System.out.println(i);
//				System.out.println("ThreadPool exit!");
//				System.out.println("AtomicInteger total cost time: "+(System.currentTimeMillis()-b)+"ms.");
//			}
//		};
//		
//		for(int i=0;i<10;++i) {
//			es.submit(new AtomicDemo(0));
//		}
//		es.shutdown();
		
		
		long b=System.currentTimeMillis();
		ExecutorService es=new ThreadPoolExecutor(10,10,0,TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>()) {
			@Override
			protected void beforeExecute(Thread t, Runnable r) {
				super.beforeExecute(t, r);//默认是空的方法
				
			}
			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);

			}
			@Override
			protected void terminated() {
				super.terminated();//默认是空的方法
				System.out.println(k);
				System.out.println("ThreadPool exit!");
				System.out.println("ReentrantLock total cost time: "+(System.currentTimeMillis()-b)+"ms.");
			}
		};
		
		for(int i=0;i<10;++i) {
			es.submit(new AtomicDemo(1));
		}
		es.shutdown();
		
		
	}
}
