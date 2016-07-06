package com.mit.models;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.mit.dao.mid.MIdGenLongDAO;
import com.mit.dao.photo.PhotoCommon;
import com.mit.dao.photo.VideoThumbnailClient;
import com.mit.midutil.MIdNoise;
import com.mit.mphoto.thrift.TMPhoto;
import com.mit.utils.ImageInfoUtils;
import com.mit.utils.LinkBuilder;

public class UploadPhotoModel {
	
	private static final int VIDEO_PHOTO = 1;
	public static final UploadPhotoModel Instance = new UploadPhotoModel();
	
	private UploadPhotoModel(){}
	
	public Map<String, Object> uploadPhoto(byte[] data, String fileName, int type) throws IOException {
		Map<String, Object> rs = new HashMap<String, Object>();
		int err = ModelError.SUCCESS;
		long id = 0;
		String link = "";
		if (data != null) {
			TMPhoto tmp = new TMPhoto();
			id = MIdGenLongDAO.getInstance(PhotoCommon.videoPhotoIdGen).getNext();
			ImageInfoUtils img = new ImageInfoUtils(data);
			String contentType = img.getMimeType();
			tmp.setId(id);
			tmp.setFilename(fileName);
			tmp.setData(data);
			tmp.setContentType(contentType);
			
			if (type == VIDEO_PHOTO) {
				err = VideoThumbnailClient.getInstance().putMPhoto(tmp);
				link = LinkBuilder.buildVideoThumbLink(id);
			}
			
			err = ModelError.PHOTO_DATA_NULL;
		}
		
		if (err >= 0) {
			rs.put("id", MIdNoise.enNoiseLId(id));
			rs.put("link", link);
		}
		rs.put("err", err);
		
		return rs;
	}

}
