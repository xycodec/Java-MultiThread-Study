package com.xycode.threadlocal;

public class Main {

	public static void main(String[] args) {
		//三个Client对应三个线程,也就对于三个TSLog
		new Client("Alice").start();
		new Client("Bob").start();
		new Client("Charis").start();
	}

}
