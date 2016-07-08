package com.mit.dao.photo;

import org.slf4j.LoggerFactory;

import com.mit.entities.photo.PhotoType;

public class VideoThumbnailClient extends PhotoClient{

	private static final VideoThumbnailClient instance = new VideoThumbnailClient();
	
	private VideoThumbnailClient() {
		super(LoggerFactory.getLogger(VideoThumbnailClient.class), PhotoCommon.photoName.get(PhotoType.VIDEO_THUMBNAIL.getValue()), "VideoThumbnailClient");
	}
	
	public static VideoThumbnailClient getInstance() {
		return instance;
	}
	
}
