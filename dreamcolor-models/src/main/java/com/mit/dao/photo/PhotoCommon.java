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
    //Photo Product.
	public static final String productPhotoName = "productphoto";
    public static final String productPhotoIdGen = "productphotoid";
    public static final String PRODUCT_PHOTO_TABLE = "set_product_photo";
    
    //Photo Video
    public static final String videoPhotoName = "videophoto";
    public static final String videoPhotoIdGen = "videophotoid";
    public static final String VIDEO_PHOTO_TABLE = "set_video_photo";
    
    //Banner News User.
    public static final String bannerPhotoName = "bannerphoto";
    public static final String bannerNewsPhotoIdGen = "bannerphotoid";
    public static final String BANNER_NEWS_PHOTO_TABLE = "set_banner_photo";
    
    //Color
    public static final String colorPhotoName = "colorphoto";
    public static final String colorPhotoIdGen = "colorphotoid";
    public static final String COLOR_PHOTO_TABLE = "set_color_photo";
    
    public static final Map<Integer, String> photoName;
    static {
    	photoName = new HashMap<>();
    	photoName.put(PhotoType.BANNER.getValue(), bannerPhotoName);
    	
    }
    
    
    
}
