package com.xycode.parallelModeAndAlgorithm;

import java.text.MessageFormat;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ProducerAndConsumer {
	static class PCData{
		int data;
		public PCData(int data) {
			super();
			this.data = data;
		}

		public int getData() {
			return data;
		}

		public void setData(int data) {
			this.data = data;
		}
		
		@Override
		public String toString() {
			return "[data = "+data+"]";
		}
	}
	static class Producer implements Runnable{
		volatile boolean isRunning=true;
		BlockingQueue<PCData> queue;//这里只是一个引用,因为是要和其它 线程/类 共享的
		static AtomicInteger count=new AtomicInteger(0);
		static final int sleep_time=1000;
		
		
		public Producer(BlockingQueue<PCData> queue) {
			super();
			this.queue = queue;
		}


		@Override
		public void run() {
			PCData data=null;
			Random r=new Random();
			System.out.println("start,ProducerID = "+Thread.currentThread().getId());
			while(isRunning) {
				try {
					Thread.sleep(r.nextInt(sleep_time));
					data=new PCData(count.incrementAndGet());//相当于++count
					System.out.print("Producer: ");
					System.out.println(data+ "is put into queue.");
					if(queue.offer(data, 2, TimeUnit.SECONDS)==false) {
						System.out.println("failed to put "+data+" into queue.");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
//					Thread.currentThread().interrupt();
				}
			}
		}
		
		public void stop() {
			isRunning=false;
		}
		
	}
	
	static class Consumer implements Runnable{
		BlockingQueue<PCData> queue;//这里只是一个引用,因为是要和其它 线程/类 共享的
		static final int sleep_time=1000;
		
		
		public Consumer(BlockingQueue<PCData> queue) {
			super();
			this.queue = queue;
		}

		@Override
		public void run() {
			System.out.println("start,ConsumerID = "+Thread.currentThread().getId());
			Random r=new Random();
			while(true) {
				try {
					PCData data=queue.take();
					if(data!=null) {
						int result=data.getData()*data.getData();
						System.out.print("Consumer: compute ");
						System.out.println(MessageFormat.format("{0}*{1}={2}", 
								data.getData(),data.getData(),result));
						Thread.sleep(r.nextInt(sleep_time));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
//					Thread.currentThread().interrupt();
				}
				
			}
		}
	}
	public static void main(String[] args) throws InterruptedException {
		BlockingQueue<PCData> queue=new LinkedBlockingDeque<>(10);
		/*
		 * 共享一个队列,这个队列就是联系Producer和Consumer的纽带
		 * 并且由于有这个queue充当缓冲区,允许Producer和Consumer线程之间性能存在差异,起到一个调和的作用.
		 */
		Producer p1=new Producer(queue);
		Producer p2=new Producer(queue);
		Producer p3=new Producer(queue);
		
		Consumer c1=new Consumer(queue);
		Consumer c2=new Consumer(queue);
		Consumer c3=new Consumer(queue);
		
		ExecutorService es=Executors.newCachedThreadPool();
		es.execute(p1);
		es.execute(p2);
		es.execute(p3);
		
		es.execute(c1);
		es.execute(c2);
		es.execute(c3);
		Thread.sleep(10000);//计算时间10s
		
		p1.stop();//Producer线程停止生产数据
		p2.stop();
		p3.stop();
		
		Thread.sleep(3000);
//		es.shutdown();//这里的shutdown其实没用,因为Consumer线程一直在运行,所以线程池只是停止接受任务,而不会退出
		
	}

}
