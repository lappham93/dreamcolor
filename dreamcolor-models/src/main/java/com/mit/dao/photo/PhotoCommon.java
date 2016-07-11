/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.dao.photo;

import java.util.HashMap;
import java.util.Map;

import com.mit.entities.photo.PhotoType;

/**
 *
 * @author nghiatc
 * @since Jan 12, 2016
 */
public class PhotoCommon {
//    //Photo Product.
//	public static final String productPhotoName = "productphoto";
//    public static final String productPhotoIdGen = "productphotoid";
//    public static final String PRODUCT_PHOTO_TABLE = "set_product_photo";
//    
//    //Photo Video
//    public static final String videoPhotoName = "videophoto";
//    public static final String videoPhotoIdGen = "videophotoid";
//    public static final String VIDEO_PHOTO_TABLE = "set_video_photo";
//    
//    //Banner News User.
//    public static final String bannerPhotoName = "bannerphoto";
//    public static final String bannerNewsPhotoIdGen = "bannerphotoid";
//    public static final String BANNER_NEWS_PHOTO_TABLE = "set_banner_photo";
//    
//    //Color
//    public static final String colorPhotoName = "colorphoto";
//    public static final String colorPhotoIdGen = "colorphotoid";
//    public static final String COLOR_PHOTO_TABLE = "set_color_photo";
    
    public static final Map<Integer, String> photoName;
    public static final Map<Integer, String> idGen;
    public static final Map<Integer, String> photoTable;
    static {
    	photoName = new HashMap<>();
    	photoName.put(PhotoType.BANNER.getValue(), "bannerphoto");
    	photoName.put(PhotoType.COLOR.getValue(), "colorphoto");
    	photoName.put(PhotoType.PRODUCT.getValue(), "productphoto");
    	photoName.put(PhotoType.VIDEO_THUMBNAIL.getValue(), "videophoto");
    	photoName.put(PhotoType.DISTRIBUTOR.getValue(), "distributorphoto");
    	
    	idGen = new HashMap<>();
    	idGen.put(PhotoType.BANNER.getValue(), "bannerphotoid");
    	idGen.put(PhotoType.COLOR.getValue(), "colorphotoid");
    	idGen.put(PhotoType.PRODUCT.getValue(), "productphotoid");
    	idGen.put(PhotoType.VIDEO_THUMBNAIL.getValue(), "videophotoid");
    	idGen.put(PhotoType.DISTRIBUTOR.getValue(), "photodistributorid");
    	
    	photoTable = new HashMap<>();
    	photoTable.put(PhotoType.BANNER.getValue(), "set_banner_photo");
    	photoTable.put(PhotoType.COLOR.getValue(), "set_color_photo");
    	photoTable.put(PhotoType.PRODUCT.getValue(), "set_product_photo");
    	photoTable.put(PhotoType.VIDEO_THUMBNAIL.getValue(), "set_video_photo");
    	photoTable.put(PhotoType.DISTRIBUTOR.getValue(), "set_distributor_photo");
    	
    }
    
    
    
}
