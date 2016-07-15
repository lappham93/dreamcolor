package com.mit.entities.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class News {
	public static final int EVENT_NONE = 0;
	public static final int EVENT_REGISTER = 1;

	public static final int ACTIVE = 1;
	
	private long uId;
	private int type;
	private int event;
	private int status;
	
	public News() {}

	public News(long uId, int type, int event, int status) {
		super();
		this.uId = uId;
		this.type = type;
		this.event = event;
		this.status = status;
	}

	@JsonIgnore
	public long getuId() {
		return uId;
	}

	public void setuId(long uId) {
		this.uId = uId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getEvent() {
		return event;
	}

	public void setEvent(int event) {
		this.event = event;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public abstract UserView buildUserView();
	
	public abstract NotificationItem buildNotificationItem(int userId);
	
	public static class UserView {
		private int type;
		
		protected UserView(News newsItem) {
			this.type = newsItem.getType();
		}

		@JsonIgnore
		public int getType() {
			return type;
		}
	}
}
