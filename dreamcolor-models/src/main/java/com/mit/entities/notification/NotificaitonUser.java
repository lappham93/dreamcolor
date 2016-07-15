package com.mit.entities.notification;

import java.util.List;

public class NotificaitonUser {
	private int uid;
	private List<Long> nId;

	public NotificaitonUser() {
		super();
	}

	public NotificaitonUser(int uid, List<Long> nId) {
		super();
		this.uid = uid;
		this.nId = nId;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public List<Long> getNotificationId() {
		return nId;
	}

	public void setNotificationId(List<Long> notificationId) {
		this.nId = notificationId;
	}

}
