package com.mit.entities.notification;

public enum NewsType {
	COLOR(1);
	
	private int value;
	
	private NewsType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
