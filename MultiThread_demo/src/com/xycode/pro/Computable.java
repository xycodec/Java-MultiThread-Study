package com.xycode.pro;

public interface Computable<R, A> {
	public R compute(A arg);
	public R get(A arg);
}
