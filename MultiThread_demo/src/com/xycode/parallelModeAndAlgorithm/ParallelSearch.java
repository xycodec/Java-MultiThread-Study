package com.xycode.parallelModeAndAlgorithm;
/**
 * 并行搜索,先将序列分段,每段分配一个线程去搜索
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;


public class ParallelSearch {
	static AtomicInteger result=new AtomicInteger(-1);//原子类
	static final int Thread_num=10;
	static ExecutorService es=Executors.newCachedThreadPool();
	static int[] arr;
	static class SearchTask implements Callable<Integer>{
		int begin,end,searchValue;

		public SearchTask(int searchValue,int begin, int end) {
			super();
			this.begin = begin;
			this.end = end;
			this.searchValue = searchValue;
		}

		@Override
		public Integer call() throws Exception {
			return search(searchValue, begin, end);
		}
		
	}
	public static int search(int searchValue,int begin,int end) {//返回值所在的索引
		for(int i=begin;i<end;++i) {
			if(result.get()>=0) return result.get();
			if(arr[i]==searchValue) {
				if(result.compareAndSet(-1, i)==false) {//CAS模式操作
					return result.get();
				}else {
					return i;
				}
			}
		}
		return -1;//没找到,就返回-1
	}
	
	public static int pSearch(int searchValue) throws InterruptedException, ExecutionException {
		int subSize=arr.length/Thread_num+1;
		List<Future<Integer>> re=new ArrayList<>();//子任务的返回值的凭证,Future模式
		for(int i=0;i<arr.length;i+=subSize) {
			int end=i+subSize;
			if(end>=arr.length) end=arr.length;
			//submit有返回值,为Future<V>,提交到线程池中去计算
			re.add(es.submit(new SearchTask(searchValue,i, end)));
		}
		for(Future<Integer> fu:re) {
			if(fu.get()>=0) return fu.get();//get(),Future模式,根据先前的凭证去请求计算的结果,若没计算好,就会一直等待
		}
		return -1;
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Random r=new Random();
		arr=new int[2000000];
		for(int i=0;i<2000000;++i) {
			arr[i]=r.nextInt(2000000);
		}
		for(int i=0;i<100;++i) {
			int searchValue=r.nextInt(2000000);
			System.out.println("searchValue = "+searchValue+", pos = "+pSearch(searchValue));
			result=new AtomicInteger(-1);//搜索一次后要及时清除状态
		}
	}

}
