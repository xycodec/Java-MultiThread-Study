package com.xycode.parallelModeAndAlgorithm;
/*
 * 单例模式
 * 
 */


public class Singleton {
	private Singleton() {//private的构造方法,避免外部调用多次创建
		System.out.println("Singleton is created.");
	}
	/**
	 * 若instance非static,getInstance也非static,
	 * 那么执行getInstance的前提是必须有一个Singleton的对象引用,这无疑产生了悖论,
	 * 导致的后果是我们永远也无法获得这个对象实例
	 * 
	 * 实际上instance何时创建是不一定的,可能是第一次GetInstance()时,或者第一次应用static对象时(如这里的STATUS)
	 */
	public static int STATUS=1;
	private static Singleton instance=new Singleton();
	public static Singleton GetInstance() {
		return instance;
	}
	
	public static void main(String[] args) {
//		System.out.println(Singleton.STATUS);//可以看出,先created,然后打印出了STATUS
//		System.out.println(Singleton.GetInstance());//这里就只返回instance,不创建了
		
		//验证单例模式确实有效
		Thread[] t=new Thread[100];
		for(int i=0;i<100;++i) {
			t[i]=new Thread() {
				public void run() {
					try {
						System.out.println(Singleton.GetInstance());
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			};
			t[i].start();
		}
		for(int i=0;i<100;++i) {
			try {
				t[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("end!");
	}
}
