package com.mit.dao.photo;

import org.slf4j.LoggerFactory;

public class BannerPhotoClient extends PhotoClient{

	private static final BannerPhotoClient instance = new BannerPhotoClient();
	
	private BannerPhotoClient() {
		super(LoggerFactory.getLogger(BannerPhotoClient.class), PhotoCommon.bannerPhotoName, "BannerPhotoClient");
	}
	
	public static BannerPhotoClient getInstance() {
		return instance;
	}
	
}
