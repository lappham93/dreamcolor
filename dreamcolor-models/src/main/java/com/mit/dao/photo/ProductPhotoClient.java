package com.mit.dao.photo;

import org.slf4j.LoggerFactory;

public class ProductPhotoClient extends PhotoClient{

	private static final ProductPhotoClient instance = new ProductPhotoClient();
	
	private ProductPhotoClient() {
		super(LoggerFactory.getLogger(ProductPhotoClient.class), PhotoCommon.productPhotoName, "ProductPhotoClient");
	}
	
	public static ProductPhotoClient getInstance() {
		return instance;
	}
	
}
