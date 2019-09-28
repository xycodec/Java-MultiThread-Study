package com.xycode.threadlocal;
/**
 * ThreadLocal模式,为每个线程分配TSLog,并调用
 * @author xycode
 *
 */
public class LogProxy {
	private static final ThreadLocal<TSLog> tslogCollection=new ThreadLocal<>();
	
	private static TSLog getTSLog() {
		TSLog tsLog=tslogCollection.get();//会根据当前线程上下文信息来获取数据对象
		if(tsLog==null) {//之前没有分配TSLog的话就分配一个,否则就直接返回结果
			tsLog=new TSLog(Thread.currentThread().getName()+"-log.txt");
			tslogCollection.set(tsLog);
		}
		return tsLog;
	}
	
	public static void println(String s) {
		getTSLog().println(s);
	}
	
	public static void close() {
		getTSLog().close();
	}
}
