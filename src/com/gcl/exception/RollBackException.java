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
	 * �ع�����rollTo�������±��1��ʼ
	 * @param rollTo �±��1��ʼ
	 */
	public RollBackException(Integer rollTo) {
		super();
		this.rollTo = rollTo;
	}

	public RollBackException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * �ع�����rollTo������
	 * @param message
	 * @param rollTo  �±��1��ʼ
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
