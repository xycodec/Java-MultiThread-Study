package com.xycode.pro;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MyLogger {
	
	public static Logger logger=Logger.getLogger("MyLogger");
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss(SSS");
	//public static java.lang.System.Logger log=System.getLogger("log");
	
	/*
	 * 日志级别:
	 *  OFF
	 *  SEVERE
		WARNING
		INFO
		CONFIG
		FINE
		FINER
		FINEST
		ALL
		
		
		Logger默认的级别是INFO，比INFO更低的日志将不显示
	 */
	
	static {
		ConsoleHandler handler=null;
		try {
			handler=new ConsoleHandler();
			handler.setFormatter(new Formatter() {
				
				@Override
				public String format(LogRecord record) {
			        StringBuilder builder = new StringBuilder(1000);
			        builder.append(df.format(new Date(record.getMillis()))).append("ms)");
			        builder.append("[").append(record.getLevel()).append("]: ");
			        builder.append(formatMessage(record));
			        //builder.append("\n\t\t\tat [").append(record.getSourceClassName()).append(".");
			        //builder.append(record.getSourceMethodName()).append("]");
			        builder.append("\n");
			        return builder.toString();
				}
			});
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
	}
	
	public static void main(String[] args) {
		ConsoleHandler handler=null;
		try {
			handler=new ConsoleHandler();
			handler.setFormatter(new Formatter() {
				
				@Override
				public String format(LogRecord record) {
			        StringBuilder builder = new StringBuilder(1000);
			        builder.append(df.format(new Date(record.getMillis()))).append("ms)");
			        builder.append("[").append(record.getLevel()).append("]: ");
			        builder.append(formatMessage(record));
			        builder.append("\n\t\t\tat [").append(record.getSourceClassName()).append(".");
			        builder.append(record.getSourceMethodName()).append("]");
			        builder.append("\n");
			        return builder.toString();
				}
			});
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		logger.info("sssssssssss");
	}
	
}
