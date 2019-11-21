package com.xycode.ParallelJava8;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class CompletableFutureDemo1 {
	static class AskThread implements Runnable{
		CompletableFuture<Integer> re=null;
		
		public AskThread(CompletableFuture<Integer> re) {
			super();
			this.re = re;
		}

		@Override
		public void run() {
			int result=0;
			try {
				result=re.get()*re.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			System.out.println(result);
		}
	}
	
	public static int calc(int para) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return para*para;
	}
	
	public static int div(int a,int b) {
		return a/b;
	}
	
	public static void f() {
		;
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
//		/*
//		 * 1.构造一个CompletableFuture实例
//		 * 2.新建一个线程,将CompletableFuture实例作为参数,然后执行(run()里面需要Future.get())
//		 */
//		final CompletableFuture<Integer> fu=new CompletableFuture<>();
//		new Thread(new AskThread(fu)).start();
//		System.out.println("sleep...");
//		Thread.sleep(2000);
//		fu.complete(100);//填充数据(对应get())
////		fu.complete(200);
		
		
//		//更简便的写法
//		final CompletableFuture<Integer> fu=CompletableFuture.supplyAsync(()->calc(100));
//		/*
//		 * CompletableFuture<U> supplyAsync(Supplier<U> supplier)
//		 * Supplier是一个函数接口
//		 * 
//		 * @FunctionalInterface
//			public interface Supplier<T> {
//    			T get();
//			}
//			Functional Interface: 这是一个功能界面，可以用作lambda表达式或方法引用的赋值对象。 
//		 */
//		System.out.println("calculating...");
//		System.out.println(fu.get());
		
		
		//CompletableFuture的异常处理,流式调用
		CompletableFuture<Void> fu=CompletableFuture//虽然div有返回值,但是经过一系列的流式调用后,就没有返回值了
				.supplyAsync(()->div(12,0))
				.exceptionally(exception->{
					exception.printStackTrace();
					return 0;
				})
				.thenApply(i->Integer.toString(i))
				.thenApply(str->("\""+str+"\""))
				.thenAccept(System.out::println);
		System.out.println(fu.get());//fu.get()返回null,实际上返回值为void(或者叫无返回值)是不能打印的
				
	}

}
