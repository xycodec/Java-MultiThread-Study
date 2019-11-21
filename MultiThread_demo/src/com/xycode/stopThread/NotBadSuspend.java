package com.xycode.stopThread;
/*
 * 通过标志变量来退出
 * 缺点:当线程阻塞时,可能永远都无法执行到判断标志变量的那行代码,也就无法退出了
 * (例如:LinkedBlockingQueue<V>当队列满了时,再往里面添加数据,此时线程就会阻塞)
 */
public class NotBadSuspend {
	public static Object o=new Object();
	public static class WriteThread extends Thread{
		volatile boolean suspendme=false;
		public volatile boolean flag=true;
		public void suspendMe() {
			suspendme=true;
		}
		
		public void resumeMe() {
			suspendme=false;
			synchronized (o) {
				o.notify();//notify后,线程不会立即释放锁
				try {
					System.out.println("WriteThread sleep 2s.(maintaining o's lock)");
					this.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}


		}
		@Override
		public void run() {
			while(true) {
				synchronized(o){
					while(suspendme) {
						try {
							o.wait();//wait,并释放了o的锁,suspendme==true时,线程就停在这儿了
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(flag) {//suspendme==false时,线程就正常执行,若之前wait了,就需要notify来唤醒线程
						System.out.println("in WriteThread.");
						//flag=false;
					}
				}

			}

		}
	}
	
	public static class ReadThread extends Thread{
		public volatile boolean flag=true;
		@Override
		public void run() {
			while(true) {
				synchronized(o){
					if(flag) {
						System.out.println("in ReadThread.");
						flag=false;
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		WriteThread t1=new WriteThread();
		ReadThread t2=new ReadThread();
		t1.start();
		t2.start();
		Thread.sleep(1000);
		t1.suspendMe();
		System.out.println("WriteThread suspend 2s.");
		Thread.sleep(2000);
		System.out.println("WriteThread resume.");
		t1.resumeMe();//resume完全执行完毕后才会释放o的锁
		
	}

}
