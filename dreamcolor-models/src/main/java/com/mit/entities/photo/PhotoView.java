package com.mit.entities.photo;

import com.mit.dao.photo.PhotoInfoDAO;
import com.mit.midutil.MIdNoise;
import com.mit.utils.LinkBuilder;

public class PhotoView {
	
	private String id;
	private String link;
	private int height;
	private int width;
	
	public PhotoView(long id, int type) {
		this.id = MIdNoise.enNoiseLId(id);
		
		if (type == PhotoType.COLOR.getValue()) {
			link = LinkBuilder.buildColorPhotoLink(id);
		} else if (type == PhotoType.BANNER.getValue()) {
			link = LinkBuilder.buildBannerThumbLink(id);
		} else if (type == PhotoType.PRODUCT.getValue()) {
			link = LinkBuilder.buildProductPhotoLink(id);
		} else if (type == PhotoType.VIDEO_THUMBNAIL.getValue()) {
			link = LinkBuilder.buildVideoThumbLink(id);
		}
		
		PhotoInfo info = PhotoInfoDAO.getInstance().getByRefIdAndType(id, type);
		if (info != null) {
			height = info.getHeight();
			width = info.getWidth();
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	
}	
