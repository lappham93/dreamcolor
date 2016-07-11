package com.mit.models;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.mit.dao.mid.MIdGenLongDAO;
import com.mit.dao.photo.BannerPhotoClient;
import com.mit.dao.photo.ColorPhotoClient;
import com.mit.dao.photo.DistributorPhotoClient;
import com.mit.dao.photo.PhotoCommon;
import com.mit.dao.photo.PhotoInfoDAO;
import com.mit.dao.photo.ProductPhotoClient;
import com.mit.dao.photo.VideoThumbnailClient;
import com.mit.entities.photo.PhotoInfo;
import com.mit.entities.photo.PhotoType;
import com.mit.entities.photo.PhotoView;
import com.mit.midutil.MIdNoise;
import com.mit.mphoto.thrift.TMPhoto;
import com.mit.utils.ImageInfoUtils;

public class UploadPhotoModel {

	public static final UploadPhotoModel Instance = new UploadPhotoModel();

	private UploadPhotoModel() {
	}

	public Map<String, Object> uploadPhoto(byte[] data, String fileName, int type) throws IOException {
		Map<String, Object> rs = new HashMap<String, Object>();
		int err = ModelError.SERVER_ERROR;
		if (data != null) {
			TMPhoto tmp = new TMPhoto();

			ImageInfoUtils img = new ImageInfoUtils(data);
			String contentType = img.getMimeType();
			tmp.setFilename(fileName);
			tmp.setData(data);
			tmp.setContentType(contentType);
			long id = 0;
			if (type == PhotoType.COLOR.getValue()) {
				id = MIdGenLongDAO.getInstance(PhotoCommon.idGen.get(PhotoType.COLOR.getValue())).getNext();
				tmp.setId(id);
				err = ColorPhotoClient.getInstance().putMPhoto(tmp);
			} else if (type == PhotoType.PRODUCT.getValue()) {
				id = MIdGenLongDAO.getInstance(PhotoCommon.idGen.get(PhotoType.PRODUCT.getValue())).getNext();
				tmp.setId(id);
				err = ProductPhotoClient.getInstance().putMPhoto(tmp);
			} else if (type == PhotoType.BANNER.getValue()) {
				id = MIdGenLongDAO.getInstance(PhotoCommon.idGen.get(PhotoType.BANNER.getValue())).getNext();
				tmp.setId(id);
				err = BannerPhotoClient.getInstance().putMPhoto(tmp);
			} else if (type == PhotoType.VIDEO_THUMBNAIL.getValue()) {
				id = MIdGenLongDAO.getInstance(PhotoCommon.idGen.get(PhotoType.VIDEO_THUMBNAIL.getValue())).getNext();
				tmp.setId(id);
				err = VideoThumbnailClient.getInstance().putMPhoto(tmp);
			} else if (type == PhotoType.DISTRIBUTOR.getValue()) {
				id = MIdGenLongDAO.getInstance(PhotoCommon.idGen.get(PhotoType.DISTRIBUTOR.getValue())).getNext();
				tmp.setId(id);
				err = DistributorPhotoClient.getInstance().putMPhoto(tmp);
			}

			if (err >= 0) {
				// save photo info
				PhotoInfo info = new PhotoInfo(0, id, type, img.getHeight(), img.getWidth());
				PhotoInfoDAO.getInstance().insert(info);

				rs.put("id", MIdNoise.enNoiseLId(id));
				rs.put("link", new PhotoView(id, type));
			}
		} else {
			err = ModelError.PHOTO_DATA_NULL;
		}

		rs.put("err", err);

		return rs;
	}

}
