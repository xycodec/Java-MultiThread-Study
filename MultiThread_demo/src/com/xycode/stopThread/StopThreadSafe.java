package com.xycode.stopThread;

/*
 * 采用中断机制来安全的退出线程,需要注意的是当一个线程收到一个中断信号时,线程并不会立即退出,只是表明希望这个线程退出了,
 * 到底退出与否,则由该线程自行决定
 */
public class StopThreadSafe {
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
				if(this.isInterrupted()) {//判断当前线程是否被中断,interrupted()不光判断是否被中断,并且清除当前终端状态
					System.out.println("Thread-"+this.currentThread().getId()+" interruted!");
					break;
				}
				synchronized(u){
					int v=(int)(System.currentTimeMillis()/1000);
					u.setId(v);
//					try {
//						Thread.sleep(100);
//					}catch (Exception e) {
//						e.printStackTrace();
//					}
					for(int i=0;i<10e8;++i);
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
			t.interrupt();//main线程给线程t发中断信号,希望它停止运行
		}

	}
	
}
