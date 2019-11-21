package com.xycode.stopThread;
/* Thread.stop()是不安全的,可能会引起数据不一致的情况
 * extends Thread与implements Runnable的主要区别是：
 * 1.Thread是类，Runnable是接口，若一个线程类采用extends Thread的方式来实现，由于java不支持多继承，这个线程类将无法在继承其他类。
 * 而implements Runnable就没有这种限制
 * 2.extends Thread的方式可以使得线程类直接使用Thread的一些方法，this.xxx即可
 */
public class StopThreadUnsafe{
	public static class User{
		private int id;
		private String name;
		public User() {
			id=0;
			name="0";
		}
		
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "id="+id+", name="+name;
		}
	
	}
	public static User u=new User();
	
	public static class WriteThread extends Thread{
		@Override
		public void run() {
			while(true) {
				synchronized(u){
					int v=(int)(System.currentTimeMillis()/1000);
					u.setId(v);
					try {
						this.sleep(100);
					}catch (Exception e) {
						e.printStackTrace();
					}
					u.setName(String.valueOf(v));
				}
				this.yield();//线程间的一种谦让机制
			}

		}
	}
		
		
	public static class ReadThread implements Runnable{
		@Override
		public void run() {
			while(true) {
				synchronized(u){
					if(u.getId()!=Integer.parseInt(u.getName())) {
						System.out.println(u);
					}
				}
				Thread.yield();//线程间的一种谦让机制
			}

		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		(new Thread(new ReadThread())).start();
		while(true) {
			Thread t=new WriteThread();
			t.start();
			Thread.sleep(200);
			t.stop();//一种废弃的方法,因为退出的机制不安全,可能出现数据不一致.
		}

	}

}
