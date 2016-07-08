package com.mit.entities.banner;


public abstract class Banner {
	private long uId;
	private int type;
	private int status;
	
	public Banner() {}

	public Banner(long uId, int type, int status) {
		super();
		this.uId = uId;
		this.type = type;
		this.status = status;
	}

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
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public abstract UserView buildUserView();
	
	public static class UserView {
		private int type;
		
		protected UserView(Banner newsItem) {
			this.type = newsItem.getType();
		}

		public int getType() {
			return type;
		}
	}
}
