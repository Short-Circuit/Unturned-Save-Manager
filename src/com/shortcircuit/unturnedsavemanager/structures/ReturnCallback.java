package com.shortcircuit.unturnedsavemanager.structures;

/**
 * @author ShortCircuit908
 */
public class ReturnCallback<T> {
	private T value;

	public ReturnCallback() {
		this(null);
	}

	public ReturnCallback(T value) {
		this.value = value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	@Override
	public String toString(){
		return value + "";
	}
}
