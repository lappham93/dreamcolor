package com.mit.entities.banner;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.mit.entities.photo.PhotoType;
import com.mit.entities.photo.PhotoView;


public class WebBanner extends Banner {	
	public static final int TYPE = BannerType.WEB.getValue();
	
	private String id;
	private String msg;
	private long thumb;
	
	public WebBanner() {}

	public WebBanner(long uId, String id, String msg, long thumb, int status) {
		super(uId, TYPE, status);
		this.id = id;
		this.msg = msg;
		this.thumb = thumb;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		private String id;
		private String msg;
		private PhotoView thumb;

		private UserView(WebBanner webBanner) {
			super(webBanner);
			try {
				this.id = URLDecoder.decode(webBanner.getId(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				this.id = webBanner.getId();
			}
			this.msg = webBanner.getMsg();
			this.thumb = new PhotoView(webBanner.getThumb(), PhotoType.BANNER.getValue());
		}
		
		public String getId() {
			return id;
		}

		public String getMsg() {
			return msg;
		}

		public PhotoView getThumb() {
			return thumb;
		}	
	}

    @Override
    public String toString() {
        return "WebBanner{" + "id=" + id + ", msg=" + msg + ", thumb=" + thumb + ", status" + this.getStatus() + ", " + '}';
    }
    
    
}
