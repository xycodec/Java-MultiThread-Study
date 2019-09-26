package com.xycode.stopThread;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/*
 * 使用中断才是比较好的suspend方式
 */
public class GoodSuspend {
	static class PrimeProducer extends Thread{
		private final BlockingQueue<BigInteger> queue;
		public PrimeProducer(BlockingQueue<BigInteger> queue) {
			super();
			this.queue = queue;
		}
		
		@Override
		public void run() {
			BigInteger p=BigInteger.ONE;
			try {
				while(true) {
					if(!Thread.currentThread().isInterrupted())
						queue.put(p=p.nextProbablePrime());
					else {
						System.out.println("Thread-"+Thread.currentThread().getId()+" interrupted,exit!");
						break;
					}		
				}
			} catch (InterruptedException e) {
				System.err.println("Capacity is full!");
				e.printStackTrace();
			}
		}
		
		public void cancel() {
			interrupt();
		}
	}
	public static void main(String[] args) throws InterruptedException {
		BlockingQueue<BigInteger> queue=new LinkedBlockingDeque<>(100);
		PrimeProducer t=new PrimeProducer(queue);
		t.start();
		
		for(int i=0;i<200;++i) {
			System.out.println(queue.take());
			Thread.sleep(10);
		}
		
		t.cancel();
	}

}
