package com.xycode.pro;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CacheTest {
	/*
	 * 使用线程安全的ConcurrentHashMap来进行缓存,效果比HashMap+synchronized好一些,
	 * 然而,当单个线程计算所需时间较久时,很有可能造成重复计算
	 */
	static class Memorizer_1<R,A> implements Computable<R,A>{
		final Map<A,R> cache=new ConcurrentHashMap<>();
		
		@Override
		public R compute(final A arg) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return (R)arg;
		}
		
		@Override
		public R get(final A arg) {
			R result=cache.get(arg);
			if(result==null) {
				result=compute(arg);
				cache.put(arg, result);
			}
			return result;
		}
		
	}
	/*
	 * 
	 * 使用参数到Future的ConcurrentHashMap,
	 * 因为future是立马获得的,所以不会因为计算时间过长而造成重复计算
	 * 并且使用ConcurrentHashMap::putIfAbsent(K,V),因为该方法是原子性的,故杜绝了重复添加future的可能性
	 */
	static class Memorizer_2<R,A> implements Computable<R,A>{
		final Map<A,Future<R>> cache=new ConcurrentHashMap<>();
		
		@Override
		public R compute(final A arg) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return (R)arg;
		}
		
		@Override
		public R get(final A arg) {
			while(true) {
				Future<R> f=cache.get(arg);
				if(f==null) {
					FutureTask<R> ft=new FutureTask<>(new Callable<R>() {

						@Override
						public R call() throws Exception {
							return compute(arg);
						}
						
					});
					
					f=cache.putIfAbsent(arg, ft);
					if(f==null) {//putIfAbsent返回null,说明先前值是null(在这里说明先前不存在这个映射,现在是第一次添加)
						f=ft;
						ft.run();//会调用call(),详见jdk源码
					}
				}
				
				try {
					return f.get();//阻塞等待(还没算好的话),否则立即返回结果
				}catch(CancellationException e) {
					e.printStackTrace();
					cache.remove(arg,f);//防止缓存污染
				}catch(RuntimeException e){
					e.printStackTrace();
					cache.remove(arg,f);//防止缓存污染
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	static CountDownLatch gate;
	static class Task<R,A> implements Runnable{
		Computable<R,A> m;
		A arg;
		public Task(Computable<R, A> m, A arg) {
			super();
			this.m = m;
			this.arg = arg;
		}
		
		@Override
		public void run()  {
			try {
				m.get(arg);
			}finally {
				gate.countDown();//计数减一
			}
			//System.out.println(m.get(arg));
		}
		
	}
	
	
	public static void test(ExecutorService es,Computable<Integer, Integer> m) {
		gate=new CountDownLatch(1000);//使用CountDownLatch来辅助计时
		Random r=new Random();
		long begin=System.currentTimeMillis();
		for(int i=0;i<1000;++i) {
			es.submit(new Task<Integer, Integer>(m,r.nextInt(50)));//计算结果重复率越高,缓存的效果越好
		}
		try {
			gate.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long end=System.currentTimeMillis();
		if(m instanceof Memorizer_1)//instanceof识别是哪个类
			System.out.println("Memorizer_1's total time: "+(end-begin)/1000+" s.");
		else
			System.out.println("Memorizer_2's total time: "+(end-begin)/1000+" s.");
		es.shutdown();
	}
	
	public static void main(String[] args) {
		
		//第一种方式大概需要6s ~ 8s
//		ExecutorService es_1=new ThreadPoolExecutor(10,20,0,TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>()) {
//			@Override
//			protected void terminated() {
//				System.out.println(System.currentTimeMillis()/1000%1000);
//				System.out.println("ThreadPool exit!");
//				System.out.println();
//			}
//		};
//		
//		test(es_1,new Memorizer_1<Integer,Integer>());
//		System.out.println(System.currentTimeMillis()/1000%1000);
		
		
		//第二种方式大概需要3s ~ 4s,性能是第一种的两倍多
//		ExecutorService es_2=new ThreadPoolExecutor(10,20,0,TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>()) {
//			@Override
//			protected void terminated() {
//				System.out.println(System.currentTimeMillis()/1000%1000);
//				System.out.println("ThreadPool exit!");
//			}
//		};
//		test(es_2,new Memorizer_2<Integer,Integer>());
//		System.out.println(System.currentTimeMillis()/1000%1000);
		
		
		
//		ExecutorService es_1=Executors.newFixedThreadPool(20);
//		test(es_1,new Memorizer_1<Integer,Integer>());//5s
//		
//		ExecutorService es_2=Executors.newFixedThreadPool(20);
//		test(es_2,new Memorizer_2<Integer,Integer>());//2s,性能仍是2倍+的差距
		
		
		ExecutorService es_1=new TimingThreadPool
				(10, 20, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
		ExecutorService es_2=new TimingThreadPool
				(10, 20, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
		test(es_1,new Memorizer_1<Integer,Integer>());//更精确的,从任务的平均运行时间也可看出性能的差距
		test(es_2,new Memorizer_2<Integer,Integer>());

	}

}
