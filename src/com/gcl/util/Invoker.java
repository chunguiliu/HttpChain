package com.gcl.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Invoker {

	private String className;
	private String methodName;
	private boolean hasArgs;
	private Pattern pattern = Pattern.compile("((\\w+\\.)+)(\\w+)\\((\\.*)\\)");
	
	public boolean compile(String classPath){
		Matcher m = pattern.matcher(classPath);
		if(m.matches()){
			className = m.group(1).replaceAll("\\.$", "");
			methodName = m.group(3);
			hasArgs = (m.group(4).indexOf(".") != -1);
			return true;
		}else{
			return false;
		}
	}
	public boolean isStaticMethod(Method method){
		return method.toGenericString().indexOf(" static ") != -1;
	}
	public <T> Object invoke(Class<T>[] cls, T[] obj){
		Object res = null;
		try {
			Class<?> demo = getClass(className);
			Method method = getMethod(demo, cls);
			Object invokeObj = isStaticMethod(method) ? demo : demo.newInstance();
			if(hasArgs){
				res = method.invoke(invokeObj, obj);
			}else{
				res = method.invoke(invokeObj);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return res;
	}
	private Method getMethod(Class<?> clazz, Class<?>... parameterTypes) throws SecurityException, NoSuchMethodException{
		if(hasArgs){
			return clazz.getMethod(methodName, parameterTypes);
		}else{
			return clazz.getMethod(methodName);
		}
	}
	public Class<?> getClass(String className){
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		Invoker invoker = new Invoker();
		if(invoker.compile("com.gcl.util.Invoker.testInvoker(.)")){
			System.out.println(invoker.invoke(new Class[]{String.class}, new String[]{"test"}));
		}
	}
	public void testInvoker(String str){
		System.out.println( "fuck"+str);
	}
	public boolean hasArgs() {
		return hasArgs;
	}
	public void setHasArgs(boolean hasArgs) {
		this.hasArgs = hasArgs;
	}

}
