package com.xycode.ThreadWork;
/*
 * synchronized(object):给指定对象加锁
 * synchronized returnType function(argv):给实例方法加锁,相当于给实例对象加锁(synchronized(this){}单个实例对象互斥)
 * synchronized static returnType function(argv):给静态方法加锁,相当于给类加锁(synchronized(object.class){})(相同类不同的实例对象也是互斥的)
 */
public class SynchronizedDemo implements Runnable{
	public static int i=0;
	static SynchronizedDemo instance=new SynchronizedDemo();
	public static synchronized void inc() {//给实例方法加锁
		++i;
	}
//	public synchronized void inc() {//给静态实例方法加锁
//		++i;
//	}
	@Override
	public void run() {
		for(int j=0;j<10e6;++j) {
//			synchronized (instance) {
//				++i;
//			}
			
//			synchronized (this) {
//				++i;
//			}
			
			//static synchronized method实际上与synchronized(object.class)等价
			inc();
//			synchronized (SynchronizedDemo.class) {
//				++i;
//			}
			
		}
	
	}
	public static void main(String[] args) throws InterruptedException {
		SynchronizedDemo s=new SynchronizedDemo();
		Thread t1=new Thread(s);
		Thread t2=new Thread(s);
		t1.start();t2.start();
		t1.join();t2.join();//join须在start之后
		
		System.out.println(i);
	}

}
