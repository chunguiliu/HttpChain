package com.gcl.util;

import org.apache.log4j.Logger;

public class LogUtil {

	public static boolean hasLog4jJar = false;
	static{
		try {
			hasLog4jJar = Logger.getLogger(LogUtil.class) != null;
		} catch (NoClassDefFoundError e) {
			hasLog4jJar = false;
		}
	}
	public static String getClassName(){
		return Thread.currentThread().getStackTrace()[4].getClassName();
	}
	public static void log(Object obj, Logs logs){
		if(hasLog4jJar)
			logs.log(Logger.getLogger(getClassName()), obj);
		
	}
	public static void trace(Object obj){
		log(obj, new Logs(){
			@Override
			public void log(Logger logger, Object obj) {
				logger.trace(obj);
			}
		});
	}
	public static void debug(Object obj){
		log(obj, new Logs(){
			@Override
			public void log(Logger logger, Object obj) {
				logger.debug(obj);
			}
		});
	}
	public static void info(Object obj){
		log(obj, new Logs(){
			@Override
			public void log(Logger logger, Object obj) {
				logger.info(obj);
			}
		});
	}
	public static void error(Object obj){
		log(obj, new Logs(){
			@Override
			public void log(Logger logger, Object obj) {
				logger.error(obj);
			}
		});
	}
	public static void error(Object obj, Throwable e){
		if(hasLog4jJar)
			Logger.getLogger(getClassName()).error(obj, e);
	}
	public static void fatal(Object obj){
		log(obj, new Logs(){
			@Override
			public void log(Logger logger, Object obj) {
				logger.fatal(obj);
			}
		});
	}
}
interface Logs{
	public void log(Logger logger, Object obj);
}