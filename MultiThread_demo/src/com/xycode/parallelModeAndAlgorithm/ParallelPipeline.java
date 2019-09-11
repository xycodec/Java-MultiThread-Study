package com.xycode.parallelModeAndAlgorithm;
/*
 * 并行流水线,将任务分解,交给不同的线程执行(线程之间有处理逻辑上的先后关系),提高性能
 */
import java.text.MessageFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ParallelPipeline {
	static class Msg{
		double i,j;
		String str;
		public Msg(double i, double j, String str) {
			super();
			this.i = i;
			this.j = j;
			this.str = str;
		}
		
	}
	
	static class Plus implements Runnable{
		static BlockingQueue<Msg> bq=new LinkedBlockingDeque<>();
		
		@Override
		public void run() {
			while(true) {
				try {
					Msg msg=bq.take();
					msg.j=msg.i+msg.j;
					Multiply.bq.add(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	static class Multiply implements Runnable{
		static BlockingQueue<Msg> bq=new LinkedBlockingDeque<>();
		
		@Override
		public void run() {
			while(true) {
				try {
					Msg msg=bq.take();
					msg.i=msg.i*msg.j;
					Div.bq.add(msg);					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	static class Div implements Runnable{
		static BlockingQueue<Msg> bq=new LinkedBlockingDeque<>();
		
		@Override
		public void run() {
			while(true) {
				try {
					Msg msg=bq.take();
					msg.i=msg.i/2;
					System.out.println(msg.str+" = "+msg.i);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	public static void main(String[] args) {
		new Thread(new Plus()).start();
		new Thread(new Multiply()).start();
		new Thread(new Div()).start();
		
		for(int i=0;i<1000;++i) {
			for(int j=0;j<1000;++j) {
				Msg msg=new Msg(i, j, MessageFormat.format("({0}+{1})*{2}/2",i,j,i));
				Plus.bq.add(msg);
			}
		}
	}

}
