package com.mit.entities.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserNews {
	private long uId;
	private boolean viewed;
	private long createTime;
	
	public UserNews() {}
	
	public UserNews(long uId, boolean viewed, long createTime) {
		this.uId = uId;
		this.viewed = viewed;
		this.createTime = createTime;
	}

	@JsonIgnore
	public long getuId() {
		return uId;
	}

	public void setuId(long uId) {
		this.uId = uId;
	}

	public boolean isViewed() {
		return viewed;
	}

	public void setViewed(boolean viewed) {
		this.viewed = viewed;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}	
	
	public UserView buildUserView() {
		return new UserView(this);
	}
	
	public static class UserView {
		private long id;
		private int type;
		private News.UserView content;
		private boolean viewed;
		private long createTime;
		
		protected UserView(UserNews newsItem) {
			this.id = newsItem.getuId();
			this.viewed = newsItem.isViewed();
			this.createTime = newsItem.getCreateTime();
		}
		
		public long getId() {
			return id;
		}

		public int getType() {
			return type;
		}

		public News.UserView getContent() {
			return content;
		}

		public UserView setContent(News.UserView content) {
			this.type = content.getType();
			this.content = content;
			return this;
		}

		public boolean isViewed() {
			return viewed;
		}

		public long getCreateTime() {
			return createTime;
		}
	}
}
