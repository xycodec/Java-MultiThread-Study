package com.xycode.ParallelJava8;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAccumulator;

public class LongAccumulatorDemo {

	public static void main(String[] args) {
		LongAccumulator accumulator=new LongAccumulator(Long::max, Long.MIN_VALUE);//指定操作的函数和初始值
		ExecutorService es=Executors.newCachedThreadPool();
		for(int i=0;i<10000;++i) {
			es.submit(new Thread(
					()->{
						Random r=new Random();
						accumulator.accumulate(r.nextLong());
					}
					));
		}
		es.shutdown();
		System.out.println(accumulator.get());
		
	}

}
