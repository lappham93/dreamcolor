package com.mit.entities.banner;

import com.mit.entities.photo.PhotoType;
import com.mit.entities.photo.PhotoView;

public class WelcomeBanner extends Banner {
	public static final int TYPE = BannerType.WELCOME.getValue();

	private String msg;
	private long thumb;

	public WelcomeBanner() {
	}

	public WelcomeBanner(long uId, String msg, long thumb, int status) {
		super(uId, TYPE, status);
		this.msg = msg;
		this.thumb = thumb;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public long getThumb() {
		return thumb;
	}

	public void setThumb(long thumb) {
		this.thumb = thumb;
	}

	public UserView buildUserView() {
		return new UserView(this);
	}

	public static class UserView extends Banner.UserView {
		private String msg;
		private PhotoView thumb;

		private UserView(WelcomeBanner welcomeBanner) {
			super(welcomeBanner);
			this.msg = welcomeBanner.getMsg();
			this.thumb = new PhotoView(welcomeBanner.getThumb(), PhotoType.BANNER.getValue());
		}

		public String getMsg() {
			return msg;
		}

		public PhotoView getThumb() {
			return thumb;
		}
	}
}
