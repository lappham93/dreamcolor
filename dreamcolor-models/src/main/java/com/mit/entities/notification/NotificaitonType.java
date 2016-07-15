package com.mit.entities.notification;

public enum NotificaitonType {
	APPROVE(1), REJECTED(2), NEWS(3);

	private int val;

	private NotificaitonType(int val) {
		this.val = val;
	}

	public int getValue() {
		return val;
	}

}
