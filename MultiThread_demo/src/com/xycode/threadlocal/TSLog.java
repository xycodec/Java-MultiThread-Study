package com.xycode.threadlocal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TSLog {

	private PrintWriter writer;

	public TSLog(String filename) {
		super();
		try {
			this.writer = new PrintWriter(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void println(String s) {
		writer.println(s);
	}
	
	public void close() {
		writer.println("==== end of log ====");
		writer.close();
	}

}
