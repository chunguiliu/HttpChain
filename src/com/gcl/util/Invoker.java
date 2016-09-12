package com.gcl.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.reflect.MethodUtils;

public class Invoker {

	private String className;
	private String methodName;
	private Pattern pattern = Pattern.compile("((\\w+\\.)+)(\\w+)\\((\\.*)\\)");

	public boolean compile(String classPath) {
		Matcher m = pattern.matcher(classPath);
		if (m.matches()) {
			className = m.group(1).replaceAll("\\.$", "");
			methodName = m.group(3);
			// hasArgs = (m.group(4).indexOf(".") != -1);
			return true;
		} else {
			return false;
		}
	}

	public boolean isStaticMethod(Method method) {
		return method.toGenericString().indexOf(" static ") != -1;
	}

	public <T> Object invoke() {
		Object res = null;
		try {
			Class<?> demo = getClass(className);
			Method method = getMethod(demo);
			Object invokeObj = isStaticMethod(method) ? demo : demo.newInstance();
			res = method.invoke(invokeObj);
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

	private Method getMethod(Class<?> clazz) throws SecurityException, NoSuchMethodException {
		return clazz.getMethod(methodName);
	}

	public Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Invoker invoker = new Invoker();
		 if(invoker.compile("com.gcl.util.Invoker.testInvoker()")){
			 System.out.println(invoker.invoke());
		 }
		System.out.println(MethodUtils.invokeMethod(invoker, "testInvoker", null));
	}

	public String testInvoker() {
		System.out.println("fuck");
		return "you";
	}

}
