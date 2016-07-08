/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.common.conts;

import com.mit.utils.ConfigUtils;
import com.mit.utils.DUnit;
import com.mit.utils.EDiscountCodeType;
import com.mit.utils.POType;
import com.mit.utils.SKUStatus;
import com.mit.utils.WUnit;
import java.util.*;

/**
 *
 * @author nghiatc
 * @since Aug 24, 2015
 */
public class Common {
    public static final String DOMAIN_APP;
    public static final String DOMAIN_FILE;
    public static final String DOMAIN_ADMIN;
    public static final String DOMAIN_STATIC_ADMIN;
    public static final String MEDIA_LOAD_SERV_PATH;
    public static final String PHOTO_LOAD_SERV_PATH;
    public static final String PRODUCT_PHOTO_LOAD_SERV_PATH;
    public static final String PRODUCT_PHOTO_LOAD_DIR_PATH = "/sources/web/photo/pro/";
    public static final String WDIR_STREAM_MEDIA;
    public static final String PATH_FFMPEG;
    public static final String PATH_QT_FASTSTART;
    public static final int PORT_RTMP;
    
    public static final Map<Integer, String> mapDUnit = new HashMap<Integer, String>();
    public static final Map<Integer, String> mapWUnit = new HashMap<Integer, String>();
    public static final Map<Integer, String> mapBizSKUStatus = new HashMap<Integer, String>();
    public static final Map<Integer, String> mapPOType = new HashMap<Integer, String>();
    public static final Map<Integer, String> mapEDCT = new HashMap<Integer, String>();
    public static final Map<Integer, String> mapALLEDCT = new HashMap<Integer, String>();
    
    static{
        DOMAIN_APP = ConfigUtils.getConfig().getString("system.domain", "http://115.79.45.86");
        DOMAIN_FILE = ConfigUtils.getConfig().getString("system.domain-file", "http://115.79.45.86");
        DOMAIN_ADMIN = ConfigUtils.getConfig().getString("system.domain-admin", "http://115.79.45.86");
        DOMAIN_STATIC_ADMIN = ConfigUtils.getConfig().getString("system.domain-static-admin", "http://admin.bconnectapp.com/web/admin/static");
        MEDIA_LOAD_SERV_PATH = ConfigUtils.getConfig().getString("mmedia.servletPath", "/cyog/load/media");
        PHOTO_LOAD_SERV_PATH = ConfigUtils.getConfig().getString("mphoto.servletPath", "/cyog/load/photo");
        WDIR_STREAM_MEDIA = ConfigUtils.getConfig().getString("mmedia.wdirStream", "/home/tmp/media_server");
        PATH_FFMPEG = ConfigUtils.getConfig().getString("mmedia.pathffmpeg", "/root/bin/ffmpeg");
        PATH_QT_FASTSTART = ConfigUtils.getConfig().getString("mmedia.qt_faststart", "/root/bin/qt-faststart");
        PORT_RTMP = ConfigUtils.getConfig().getInt("mmedia.portrtmp", 8787);
        PRODUCT_PHOTO_LOAD_SERV_PATH = DOMAIN_APP + "/statics/pt/pro/";
        
        mapDUnit.put(DUnit.INCH.getValue(), DUnit.INCH.name().toLowerCase());
        mapDUnit.put(DUnit.FEET.getValue(), DUnit.FEET.name().toLowerCase());
        mapDUnit.put(DUnit.MILIMETER.getValue(), DUnit.MILIMETER.name().toLowerCase());
        mapDUnit.put(DUnit.CENTIMETER.getValue(), DUnit.CENTIMETER.name().toLowerCase());
        mapDUnit.put(DUnit.METER.getValue(), DUnit.METER.name().toLowerCase());
        
        mapWUnit.put(WUnit.OZ.getValue(), WUnit.OZ.name().toLowerCase());
        mapWUnit.put(WUnit.POUND.getValue(), WUnit.POUND.name().toLowerCase());
        mapWUnit.put(WUnit.GRAM.getValue(), WUnit.GRAM.name().toLowerCase());
        mapWUnit.put(WUnit.KILOGRAM.getValue(), WUnit.KILOGRAM.name().toLowerCase());
        
        mapBizSKUStatus.put(SKUStatus.DELETE.getValue(), SKUStatus.DELETE.name().toLowerCase());
        mapBizSKUStatus.put(SKUStatus.ACTIVE.getValue(), SKUStatus.ACTIVE.name().toLowerCase());
        
        mapPOType.put(POType.SIZE.getValue(), POType.SIZE.name().toLowerCase());
        
        //mapEDCT.put(EDiscountCodeType.SALON_BUYER.getValue(), EDiscountCodeType.SALON_BUYER.name().toLowerCase());
        mapEDCT.put(EDiscountCodeType.DISTRIBUTOR.getValue(), EDiscountCodeType.DISTRIBUTOR.name().toLowerCase());
        mapEDCT.put(EDiscountCodeType.MASTER.getValue(), EDiscountCodeType.MASTER.name().toLowerCase());
        mapEDCT.put(EDiscountCodeType.COUNTRY_MASTER.getValue(), EDiscountCodeType.COUNTRY_MASTER.name().toLowerCase());
        mapEDCT.put(EDiscountCodeType.GLOBAL_MASTER.getValue(), EDiscountCodeType.GLOBAL_MASTER.name().toLowerCase());
        
        mapALLEDCT.put(EDiscountCodeType.SALON_BUYER.getValue(), EDiscountCodeType.SALON_BUYER.name().toLowerCase());
        mapALLEDCT.put(EDiscountCodeType.DISTRIBUTOR.getValue(), EDiscountCodeType.DISTRIBUTOR.name().toLowerCase());
        mapALLEDCT.put(EDiscountCodeType.MASTER.getValue(), EDiscountCodeType.MASTER.name().toLowerCase());
        mapALLEDCT.put(EDiscountCodeType.COUNTRY_MASTER.getValue(), EDiscountCodeType.COUNTRY_MASTER.name().toLowerCase());
        mapALLEDCT.put(EDiscountCodeType.GLOBAL_MASTER.getValue(), EDiscountCodeType.GLOBAL_MASTER.name().toLowerCase());
        
    }
}
