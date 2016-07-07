package com.mit.dao.photo;

import org.slf4j.LoggerFactory;

public class VideoThumbnailClient extends PhotoClient{

	private static final VideoThumbnailClient instance = new VideoThumbnailClient();
	
	private VideoThumbnailClient() {
		super(LoggerFactory.getLogger(VideoThumbnailClient.class), PhotoCommon.videoPhotoName, "VideoThumbnailClient");
	}
	
	public static VideoThumbnailClient getInstance() {
		return instance;
	}
	
}
