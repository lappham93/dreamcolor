package com.mit.dao.photo;

import org.slf4j.LoggerFactory;

import com.mit.entities.photo.PhotoType;

public class DistributorPhotoClient extends PhotoClient{

	private static final DistributorPhotoClient instance = new DistributorPhotoClient();
	
	private DistributorPhotoClient() {
		super(LoggerFactory.getLogger(DistributorPhotoClient.class), PhotoCommon.photoName.get(PhotoType.DISTRIBUTOR.getValue()), "DistributorPhotoClient");
	}
	
	public static DistributorPhotoClient getInstance() {
		return instance;
	}
	
}
