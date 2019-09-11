package com.xycode.ThreadWork;
/*
 * Fork/Join框架
 */
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ForkAndJoin extends RecursiveTask<Long>{
	
	static final int THRESHOLD=10000;
	long start,end;
	
	public ForkAndJoin(long start, long end) {
		super();
		this.start = start;
		this.end = end;
	}
	
	@Override
	protected Long compute() {
		long sum=0;
		if(end-start<THRESHOLD) {
			for(long i=start;i<=end;++i) {
				sum+=i;
			}
		}else {
			long step=(end-start)/100;
			ArrayList<ForkAndJoin> subTasks=new ArrayList<>();
			long pos=start;
			for(int i=0;i<100;++i) {//分割成100个子任务
				long lastOne=pos+start;
				if(lastOne>end) lastOne=end;
				ForkAndJoin subtask=new ForkAndJoin(pos, lastOne);
				pos+=step+1;
				subTasks.add(subtask);
				subtask.fork();
			}
			for(ForkAndJoin t:subTasks) {
				sum+=t.join();//Returns:the computed result
			}
		}
		return sum;
	}
	
	public static void main(String[] args) {
		ForkJoinPool pool=new ForkJoinPool();
		ForkAndJoin task=new ForkAndJoin(0, 200000);
		ForkJoinTask<Long> result=pool.submit(task);//future模式
		try {
			System.out.println("sum = "+result.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
	}

}
