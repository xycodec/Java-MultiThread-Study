package com.xycode.pro;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyRandom {
	static class lockRandom extends Random{
		private final Lock lock=new ReentrantLock();
		private int seed;
		public lockRandom(int seed) {
			super();
			this.seed = seed;
		}
		
		@Override
		public int nextInt(int bound) {
			lock.lock();
			try {
				int s=seed;
				seed=(int) nextLong();
				int remainder=s%bound;
				return remainder>0?remainder:remainder+bound;
			}finally {
				lock.unlock();
			}
		}
	}
	
	static class atomicRandom extends Random{
		private AtomicInteger seed;
		public atomicRandom(int seed) {
			super();
			this.seed = new AtomicInteger(seed);
		}
		
		@Override
		public int nextInt(int bound) {
			int s=seed.get();
			int nextSeed=(int) nextLong();
			if(seed.compareAndSet(s, nextSeed)) {//ÀÖ¹ÛËø,CAS
				int remainder=s%bound;
				return remainder>0?remainder:remainder+bound;
			}
			return bound;
		}
	}
	
	public static void main(String[] args) {
		lockRandom lr=new lockRandom(1000);
		for(int i=0;i<100;++i) {
			System.out.println(lr.nextInt(1000));
		}
		atomicRandom ar=new atomicRandom(1000);
		for(int i=0;i<100;++i) {
			System.out.println(ar.nextInt(1000));
		}
		
	}

}
