package com.mit.dc.site.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;

import com.mit.dao.mid.MIdGenLongDAO;
import com.mit.dao.photo.BannerPhotoClient;
import com.mit.dao.photo.ColorPhotoClient;
import com.mit.dao.photo.DistributorPhotoClient;
import com.mit.dao.photo.PhotoCommon;
import com.mit.dao.photo.ProductPhotoClient;
import com.mit.dao.photo.VideoThumbnailClient;
import com.mit.dc.site.common.Common;
import com.mit.dc.site.common.Configuration;
import com.mit.entities.photo.PhotoType;
import com.mit.midutil.MIdNoise;
import com.mit.mlist.MSortType;
import com.mit.mlist.MStrSortSetI64DAO;
import com.mit.mphoto.thrift.TMPhoto;
import com.mit.mphoto.thrift.TMPhotoResult;
import com.mit.utils.MIMETypeUtil;
import java.util.HashMap;
import java.util.Map;

public class PhotoUtil {

	public static final PhotoUtil Instance = new PhotoUtil();

	private PhotoUtil() {
	}

	public long uploadPhoto(FileItem file, PhotoType type) {
		int err = -1;
		long idThumb = MIdGenLongDAO.getInstance(PhotoCommon.idGen.get(type.getValue())).getNext();
		if (idThumb >= 0) {
			String filename = file.getName();
			String ext = FilenameUtils.getExtension(filename);
			String contentType = MIMETypeUtil.getInstance().getMIMETypeImage(ext);
			TMPhoto tmp = new TMPhoto();
			tmp.setId(idThumb);
			tmp.setFilename(filename);
			tmp.setData(file.get());
			tmp.setContentType(contentType);
			err = putPhoto(tmp, type.getValue());
			if (err >= 0) {
				String table = PhotoCommon.photoTable.get(type.getValue());
				MStrSortSetI64DAO setPhoto = new MStrSortSetI64DAO(table, MSortType.DESC);
				setPhoto.add(table, idThumb);

				return idThumb;
			}
		}

		return -1;
	}

	private int putPhoto(TMPhoto tmp, int type) {
		int err = -1;
		if (type == PhotoType.BANNER.getValue()) {
			err = BannerPhotoClient.getInstance().putMPhoto(tmp);
		} else if (type == PhotoType.COLOR.getValue()) {
			err = ColorPhotoClient.getInstance().putMPhoto(tmp);
		} else if (type == PhotoType.PRODUCT.getValue()) {
			err = ProductPhotoClient.getInstance().putMPhoto(tmp);
		} else if (type == PhotoType.VIDEO_THUMBNAIL.getValue()) {
			err = VideoThumbnailClient.getInstance().putMPhoto(tmp);
		} else if (type == PhotoType.DISTRIBUTOR.getValue()) {
			err = DistributorPhotoClient.getInstance().putMPhoto(tmp);
		}

		return err;
	}

	private TMPhotoResult getPhoto(long id, int type) {
		TMPhotoResult rs = null;
		if (type == PhotoType.BANNER.getValue()) {
			rs = BannerPhotoClient.getInstance().getMPhoto(id);
		} else if (type == PhotoType.COLOR.getValue()) {
			rs = ColorPhotoClient.getInstance().getMPhoto(id);
		} else if (type == PhotoType.PRODUCT.getValue()) {
			rs = ProductPhotoClient.getInstance().getMPhoto(id);
		} else if (type == PhotoType.VIDEO_THUMBNAIL.getValue()) {
			rs = VideoThumbnailClient.getInstance().getMPhoto(id);
		} else if (type == PhotoType.DISTRIBUTOR.getValue()) {
			rs = DistributorPhotoClient.getInstance().getMPhoto(id);
		}

		return rs;
	}

    public static Map<String, Map<String, String>> cacheImgs = new HashMap<>();
    
    public Map<String, String> getMapCacheImgs(PhotoType type){
        Map<String, String> rs = null;
        switch(type){
            case BANNER:{
                if(cacheImgs.containsKey(PhotoType.BANNER.name())){
                    rs = cacheImgs.get(PhotoType.BANNER.name());
                } else{
                    rs = new HashMap<>();
                    cacheImgs.put(PhotoType.BANNER.name(), rs);
                }
            }
            case COLOR:{
                if(cacheImgs.containsKey(PhotoType.COLOR.name())){
                    rs = cacheImgs.get(PhotoType.COLOR.name());
                } else{
                    rs = new HashMap<>();
                    cacheImgs.put(PhotoType.COLOR.name(), rs);
                }
            }
            case PRODUCT:{
                if(cacheImgs.containsKey(PhotoType.PRODUCT.name())){
                    rs = cacheImgs.get(PhotoType.PRODUCT.name());
                } else{
                    rs = new HashMap<>();
                    cacheImgs.put(PhotoType.PRODUCT.name(), rs);
                }
            }
            case VIDEO_THUMBNAIL:{
                if(cacheImgs.containsKey(PhotoType.VIDEO_THUMBNAIL.name())){
                    rs = cacheImgs.get(PhotoType.VIDEO_THUMBNAIL.name());
                } else{
                    rs = new HashMap<>();
                    cacheImgs.put(PhotoType.VIDEO_THUMBNAIL.name(), rs);
                }
            }
            case DISTRIBUTOR:{
                if(cacheImgs.containsKey(PhotoType.DISTRIBUTOR.name())){
                    rs = cacheImgs.get(PhotoType.DISTRIBUTOR.name());
                } else{
                    rs = new HashMap<>();
                    cacheImgs.put(PhotoType.DISTRIBUTOR.name(), rs);
                }
            }
        }
        return rs;
    }
    
    public Map<String, String> putMapCacheImgs(PhotoType type, Map<String, String> mapImgs){
        Map<String, String> rs = null;
        switch(type){
            case BANNER:{
                rs = cacheImgs.put(PhotoType.BANNER.name(), mapImgs);
            }
            case COLOR:{
                rs = cacheImgs.put(PhotoType.COLOR.name(), mapImgs);
            }
            case PRODUCT:{
                rs = cacheImgs.put(PhotoType.PRODUCT.name(), mapImgs);
            }
            case VIDEO_THUMBNAIL:{
                rs = cacheImgs.put(PhotoType.VIDEO_THUMBNAIL.name(), mapImgs);
            }
            case DISTRIBUTOR:{
                rs = cacheImgs.put(PhotoType.DISTRIBUTOR.name(), mapImgs);
            }
        }
        return rs;
    }
    
	public String buildURIImg(long pid, PhotoType type) {
        String uriImg = "";
		try {
            String pidn = MIdNoise.enNoiseLId(pid);
            //get uri from cache image.
            Map<String, String> mapImgs = getMapCacheImgs(type);
            if(mapImgs.containsKey(pidn)){
                uriImg = mapImgs.get(pidn);
            } else{ // get from DB Photo to write local directory and add to cache imgs.
                TMPhotoResult tmprs = getPhoto(pid, type.getValue());
                if (tmprs != null && tmprs.value != null) {
                    byte[] dataImg = tmprs.value.getData();
                    String filename = tmprs.value.getFilename();
                    String ext = FilenameUtils.getExtension(filename);

                    String folderPath = Common.swdirPDB + File.separator + "pt" + File.separator
                            + PhotoCommon.photoName.get(type.getValue());
                    // create folder if it is not exist;
                    File folder = new File(folderPath);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    String pathSaveImg = folderPath + File.separator + pidn + "." + ext;
                    uriImg = Configuration.APP_STATIC_DOMAIN + "/pdb/pt" + File.separator
                            + PhotoCommon.photoName.get(type.getValue()) + File.separator + pidn + "." + ext;
                    File img = new File(pathSaveImg);
                    if (!img.exists()) {
                        FileOutputStream fos = new FileOutputStream(img);
                        fos.write(dataImg, 0, dataImg.length);
                        fos.close();
                    }
                    
                    // add to  cache imgs.
                    mapImgs.put(pidn, uriImg);
                    putMapCacheImgs(type, mapImgs);
                }
            }
		} catch (Exception e) {
		}
		return uriImg;
	}

	public static void main(String[] args) {
		System.out.println(PhotoUtil.Instance.buildURIImg(4, PhotoType.getPhotoType(4)));
	}

}
