package com.mit.dao.photo;

import org.slf4j.LoggerFactory;

import com.mit.entities.photo.PhotoType;

public class BannerPhotoClient extends PhotoClient{

	private static final BannerPhotoClient instance = new BannerPhotoClient();
	
	private BannerPhotoClient() {
		super(LoggerFactory.getLogger(BannerPhotoClient.class), PhotoCommon.photoName.get(PhotoType.BANNER.getValue()), "BannerPhotoClient");
	}
	
	public static BannerPhotoClient getInstance() {
		return instance;
	}
	
}
