package com.xycode.parallelModeAndAlgorithm;

public class StaticSingleton {
	private StaticSingleton() {//private的构造方法,避免外部调用多次创建
		System.out.println("StaticSingleton is created.");
	}
	/**
	 * 若instance非static,getInstance也非static,
	 * 那么执行getInstance的前提是必须有一个Singleton的对象引用,这无疑产生了悖论,
	 * 导致的后果是我们永远也无法获得这个对象实例
	 * 
	 * 实际上instance何时创建是不一定的,可能是第一次GetInstance()时,或者第一次应用static对象时,
	 * 下面这种方式反过来利用这种缺点来实现只在第一次GetInstance()时创建
	 */
	public static int STATUS=1;
	private static class SingletonHolder{//private,使外部无法访问,单例对象隐藏在这个内部类中
		private static StaticSingleton instance=new StaticSingleton();
	}
	
	public static StaticSingleton GetInstance() {
		return SingletonHolder.instance;
	}
	public static void main(String[] args) {
		System.out.println(StaticSingleton.STATUS);//只打印了STATUS,证明此时没有创建对象
//		System.out.println(StaticSingleton.GetInstance());//这时候就创建对象了
		
		//验证单例模式确实有效
		Thread[] t=new Thread[100];
		for(int i=0;i<100;++i) {
			t[i]=new Thread() {
				public void run() {
					try {
						System.out.println(StaticSingleton.GetInstance());
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
