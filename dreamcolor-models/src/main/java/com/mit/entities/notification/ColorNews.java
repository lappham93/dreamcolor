package com.mit.entities.notification;

import java.util.List;

import com.mit.entities.app.AppKey;
import com.mit.utils.LinkBuilder;



public class ColorNews extends News {
	public static final int TYPE = NewsType.COLOR.getValue();
	
	private List<Long> colorIds;
	private String msg;
	private long thumb;
	
	public ColorNews() {}

	public ColorNews(long uId, List<Long> colorIds, String msg, long thumb, int event, int status) {
		super(uId, TYPE, event, status);
		this.colorIds = colorIds;
		this.msg = msg;
		this.thumb = thumb;
	}

	public List<Long> getColorIds() {
		return colorIds;
	}

	public void setColorIds(List<Long> colorIds) {
		this.colorIds = colorIds;
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
	
	public NotificationItem buildNotificationItem(int userId) {
		return new NotificationItem(colorIds, 0, userId, AppKey.CYOGEL, getType(), getMsg());
	}
	
	public MultiDestNotificationItem buildMultiDestNotificationItem(List<Integer> userIds) {
		return new MultiDestNotificationItem(colorIds, 0, userIds, AppKey.CYOGEL, getType(), getMsg());
	}
	
	public static class UserView extends News.UserView {		
		private List<Long> colorIds;
		private String msg;
		private String thumb;

		private UserView(ColorNews colorNews) {
			super(colorNews);
			this.colorIds = colorNews.getColorIds();
			this.msg = colorNews.getMsg();
			this.thumb = LinkBuilder.buildColorPhotoLink(colorNews.getThumb());
		}

		public List<Long> getColorIds() {
			return colorIds;
		}
		
		public String getMsg() {
			return msg;
		}

		public String getThumb() {
			return thumb;
		}	
	}
}
