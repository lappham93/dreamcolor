package com.mit.dao.photo;

import org.slf4j.LoggerFactory;

public class ColorPhotoClient extends PhotoClient{

	private static final ColorPhotoClient instance = new ColorPhotoClient();
	
	private ColorPhotoClient() {
		super(LoggerFactory.getLogger(ColorPhotoClient.class), PhotoCommon.colorPhotoName, "ColorPhotoClient");
	}
	
	public static ColorPhotoClient getInstance() {
		return instance;
	}
	
}
