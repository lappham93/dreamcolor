package com.mit.dreamcolor.admin.utils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;

import com.mit.dao.mid.MIdGenLongDAO;
import com.mit.dao.photo.PhotoCommon;
import com.mit.entities.photo.PhotoType;
import com.mit.mphoto.thrift.TMPhoto;
import com.mit.utils.MIMETypeUtil;

public class UploadPhotoUtil {
	
	public static void uploadPhoto(FileItem file, PhotoType type) {
		long idThumb = MIdGenLongDAO.getInstance(PhotoCommon.bannerNewsPhotoIdGen).getNext();
        if(idThumb >= 0){
//            TMPhoto tmp = new TMPhoto();
//            tmp.setId(idThumb);
//            String filename = thumbphoto.getName();
//            tmp.setFilename(filename);
//            tmp.setData(thumbphoto.get());
//            String ext = FilenameUtils.getExtension(filename);
//            String contentType = MIMETypeUtil.getInstance().getMIMETypeImage(ext);
//            tmp.setContentType(contentType);
//            int err = BannerNewsPhotoClient.getInstance().putMPhoto(tmp);
//            if(err >= 0){
//                PhotoCommon.SET_BANNER_NEWS_PHOTO.add(PhotoCommon.BANNER_NEWS_PHOTO_TABLE, idThumb);
//            }
        }
	}

}
