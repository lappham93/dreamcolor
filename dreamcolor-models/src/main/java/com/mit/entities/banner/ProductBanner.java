package com.mit.entities.banner;

import com.mit.entities.photo.PhotoType;
import com.mit.entities.photo.PhotoView;



public class ProductBanner extends Banner {
	public static final int TYPE = BannerType.PRODUCT.getValue();
	
	private long productId;
	private String msg;
	private long thumb;
	
	public ProductBanner() {}

	public ProductBanner(long uId, long productId, String msg, long thumb, int status) {
		super(uId, TYPE, status);
		this.productId = productId;
		this.msg = msg;
		this.thumb = thumb;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
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
		private long productId;
		private String msg;
		private PhotoView thumb;

		private UserView(ProductBanner productBanner) {
			super(productBanner);
			this.productId = productBanner.getProductId();
			this.msg = productBanner.getMsg();
			this.thumb = new PhotoView(productBanner.getThumb(), PhotoType.BANNER.getValue());
		}

		public long getProductId() {
			return productId;
		}

		public String getMsg() {
			return msg;
		}

		public PhotoView getThumb() {
			return thumb;
		}	
	}
}
