package com.mit.dao.photo;

import org.slf4j.LoggerFactory;

import com.mit.entities.photo.PhotoType;

public class ColorPhotoClient extends PhotoClient{

	private static final ColorPhotoClient instance = new ColorPhotoClient();
	
	private ColorPhotoClient() {
		super(LoggerFactory.getLogger(ColorPhotoClient.class), PhotoCommon.photoName.get(PhotoType.COLOR.getValue()), "ColorPhotoClient");
	}
	
	public static ColorPhotoClient getInstance() {
		return instance;
	}
	
}
