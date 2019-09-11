package com.xycode.pro;

public interface MyMap<K,V> {
	public V put(K key,V value);
	public V remove(K key);
	public V get(K key);
	public void clear();
}
