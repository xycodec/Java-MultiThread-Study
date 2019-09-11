package com.xycode.DH;

import java.util.Random;

public class Constant {
	public final static int a=5;
	public final static int p=23;
	public static Random r=new Random();
	public static int get(int private_key) {//获得密钥计算过程中的中间结果
		return ((int)Math.pow(a,private_key))%p;
	}

	public static int get(int f, int s) {//生成最终的密钥
		return ((int)Math.pow(f,s))%p;
	}
}
