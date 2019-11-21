package com.xycode.parallelModeAndAlgorithm;

public class LazySingleton {

	private LazySingleton() {//private的构造方法,避免外部调用多次创建
		System.out.println("LazySingleton is created.");
	}
	/**
	 * 若instance非static,getInstance也非static,
	 * 那么执行getInstance的前提是必须有一个Singleton的对象引用,这无疑产生了悖论,
	 * 导致的后果是我们永远也无法获得这个对象实例
	 * 
	 * 下面的方式是保证是第一次GetInstance()时创建对象,synchronized防止同时创建多次,但是也是性能下降了.
	 */
	private static LazySingleton instance=null;
	/**
	 * 实验证明,若去掉synchronized,当线程比较密集时,会多次创建
	 * 
	 */
	public synchronized static LazySingleton GetInstance() {
		if(instance==null) return (instance=new LazySingleton());
		else return instance;
	}
	
	public static void main(String[] args) {
//		System.out.println(LazySingleton.GetInstance());
		
		//验证单例模式确实有效
		Thread[] t=new Thread[100];
		for(int i=0;i<100;++i) {
			t[i]=new Thread() {
				public void run() {
					try {
						System.out.println(LazySingleton.GetInstance());
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
