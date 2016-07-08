package com.mit.dao.photo;

import org.slf4j.LoggerFactory;

import com.mit.entities.photo.PhotoType;

public class ProductPhotoClient extends PhotoClient{

	private static final ProductPhotoClient instance = new ProductPhotoClient();
	
	private ProductPhotoClient() {
		super(LoggerFactory.getLogger(ProductPhotoClient.class), PhotoCommon.photoName.get(PhotoType.PRODUCT.getValue()), "ProductPhotoClient");
	}
	
	public static ProductPhotoClient getInstance() {
		return instance;
	}
	
}
