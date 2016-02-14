package com.gcl.exception;

public class RollBackException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4779596403038474592L;

	private Integer rollTo = null;
	public RollBackException() {
		super();
	}

	/**
	 * 回滚至第rollTo个请求，下标从1开始
	 * @param rollTo 下标从1开始
	 */
	public RollBackException(Integer rollTo) {
		super();
		this.rollTo = rollTo;
	}

	public RollBackException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 回滚至第rollTo个请求，
	 * @param message
	 * @param rollTo  下标从1开始
	 */
	public RollBackException(String message, Integer rollTo) {
		super(message);
		this.rollTo = rollTo;
	}
	public RollBackException(String message) {
		super(message);
	}

	public RollBackException(Throwable cause) {
		super(cause);
	}

	public Integer getRollTo() {
		return rollTo;
	}

	public void setRollTo(Integer rollTo) {
		this.rollTo = rollTo;
	}

	
}
