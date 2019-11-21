package com.xycode.ThreadWork;
/*
 * 所谓守护线程必须有被守护的线程才会运行,当没有被守护线程(非守护线程即可)时,守护线程便会结束.
 */
public class DaemonDemo implements Runnable{
	public static volatile int i=0;
	@Override
	public void run() {
		while(true) {
			System.out.println("I am alive.");
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
//		for(i=0;i<10e7;++i);
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		Thread t=new Thread(new DaemonDemo());
		//此处main线程为用户进程,若t不是守护进程,t将永远运行下去
		t.setDaemon(true);//setDaemon必须在start之前设置,t是守护线程,此处就是守护main线程
		//所以当main线程2s后结束了,所有守护线程也随之结束,否则t非守护线程的话,main线程即使退出了,t线程也永远都在执行
		t.start();
		Thread.sleep(2000);
		
//		Thread t=new Thread(new DaemonDemo());
//		
//		t.start();
//		t.join();//join需在start之后设置,t.join表示main线程愿意等待线程t执行完毕,否则i将会很小,来不及增加到阈值main线程就结束了
//		System.out.println(i);
		
	}
	
}