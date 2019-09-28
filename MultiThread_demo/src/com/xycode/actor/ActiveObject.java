package com.xycode.actor;

import java.util.concurrent.Future;

public interface ActiveObject {
	//接口里面定义的方法默认是abstract的
	public Future<String> makeStr(int cnt,char fillChar);
	public void displayStr(String str);
	public void shutdown();
	
}
