package com.xycode.pro;
/**
 * 重新包装后的线程池，主要加了计时功能
 */
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TimingThreadPool extends ThreadPoolExecutor{
	
	
	public TimingThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	final ThreadLocal<Long> start_time=new ThreadLocal<>();
	final AtomicLong numTasks=new AtomicLong();
	final AtomicLong total_time=new AtomicLong();
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		MyLogger.logger.fine(String.format("Thread %s: start %s", t,r));
		start_time.set(System.currentTimeMillis());
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		long end_time=System.currentTimeMillis();
		long taskTime=end_time-start_time.get();
		numTasks.incrementAndGet();//++i
		total_time.addAndGet(taskTime);
		MyLogger.logger.fine(String.format("Thread %s end %s, time=%d ns", t,r,taskTime));
		//默认日志等级为info，故fine等级的消息默认不会显示
	}
	
	@Override
	protected void terminated() {
		super.terminated();
		MyLogger.logger.info(String.format("Terminated:total_time:%d ns,avg_time=%d ns", 
		total_time.get(),total_time.get()/numTasks.get()));
	}

	public static void main(String[] args) {
		
	}

}
