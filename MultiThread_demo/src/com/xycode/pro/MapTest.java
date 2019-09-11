package com.xycode.pro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 用读写锁包装原生的Map
 * @author xycode
 *
 */

public class MapTest{
	
	static class readWriteMap<K,V> implements MyMap<K, V>{
		private final Map<K,V> map;
		private final ReadWriteLock lock=new ReentrantReadWriteLock();
		private final Lock readLock=lock.readLock();
		private final Lock writeLock=lock.writeLock();
		
		public readWriteMap(Map<K, V> map) {
			this.map = map;
		}
		
		@Override
		public V put(K key,V value) {
			writeLock.lock();
			try {
				return map.put(key, value);
			}finally {
				writeLock.unlock();
			}
		}
		
		@Override
		public V get(K key) {
			readLock.lock();
			try {
				return map.get(key);
			}finally {
				readLock.unlock();
			}
		}
		
		@Override
		public V remove(K key) {
			writeLock.lock();
			try {
				return map.remove(key);
			}finally {
				writeLock.unlock();
			}
		}
		
		@Override
		public void clear() {
			writeLock.lock();
			try {
				 map.clear();
			}finally {
				writeLock.unlock();
			}
		}

	}
	
	static class lockMap<K,V> implements MyMap<K, V>{
		private final Map<K,V> map;
		private final ReentrantLock lock=new ReentrantLock();
		
		public lockMap(Map<K, V> map) {
			super();
			this.map = map;
		}

		@Override
		public V put(K key,V value) {
			lock.lock();
			try {
				return map.put(key, value);
			}finally {
				lock.unlock();
			}
		}
		
		@Override
		public V get(K key) {
			lock.lock();
			try {
				return map.get(key);
			}finally {
				lock.unlock();
			}
		}
		
		@Override
		public V remove(K key) {
			lock.lock();
			try {
				return map.remove(key);
			}finally {
				lock.unlock();
			}
		}
		
		@Override
		public void clear() {
			lock.lock();
			try {
				 map.clear();
			}finally {
				lock.unlock();
			}
		}

	}
	
	static final Random r=new Random();
	static CountDownLatch gate;
	static class Task<K,V> implements Runnable{
		MyMap<K,V> map;
		List<K> key_list;

		public Task(MyMap<K,V> map,List<K> key_list) {
			this.map=map;
			for(K key: key_list) {
				map.put(key, (V)String.valueOf(key));
			}
			this.key_list=key_list;
		}

		@Override
		public void run() {
			try {
				Collections.shuffle(key_list);
				for(K key: key_list) {
					if(r.nextDouble()<0.1) {//10%的概率write
						map.remove(key);
					}else {
						map.get(key);
					}
				}
			}finally {
				gate.countDown();//计数减一
			}
		}
		
	}
	
	
	public static void test(ExecutorService es,MyMap<Integer, String> m,int task_num) {
		gate=new CountDownLatch(task_num);//使用CountDownLatch来辅助计时
		Random r=new Random();
		long begin=System.currentTimeMillis();
		for(int i=0;i<task_num;++i) {
			List<Integer> key_list=new ArrayList<>();
			for(int j=0;j<5000;++j) {
				key_list.add(r.nextInt(5000));
			}
			es.submit(new Task(m,key_list));
		}
		try {
			gate.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long end=System.currentTimeMillis();
		if(m instanceof readWriteMap)
			System.out.println("readWriteMap's total time: "+(end-begin)+" ms.");
		else if(m instanceof lockMap)
			System.out.println("lockMap's total time: "+(end-begin)+" ms.");
		else
			System.out.println("illegal class!");
		es.shutdown();
	}
	
	public static void main(String[] args) {
		ExecutorService es_1=new TimingThreadPool
				(10, 20, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
		ExecutorService es_2=new TimingThreadPool
				(10, 20, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
		test(es_1,new readWriteMap<Integer,String>(new HashMap<>()),1000);
		test(es_2,new lockMap<Integer,String>(new HashMap<>()),1000);
	}

}
