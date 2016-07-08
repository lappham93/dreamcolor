/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.file.common;

import com.mit.configer.MConfig;

/**
 *
 * @author nghiatc
 * @since Jan 26, 2016
 */
public class Configuration {
    public static String APP_TITLE;
    public static String APP_DOMAIN;
	public static String APP_STATIC_DOMAIN;
    public static String APP_DOMAIN_HOME;
    public static String APP_STATIC_IMG;
    
    public static int    APP_PAGING_PAGE_SIZE = 10;
    public static int    APP_PAGING_NUM_DISPLAY = 3;
    
    public static String AVATAR_DEFAULT;
    public static String IMG_DEFAULT;
    
    public static String AD_USERNAME;
    public static String AD_PASSWORD;
    
    public static String AD_EMAIL;
    
    static{
        APP_TITLE = MConfig.getConfig().getString("systeminfo.app-title", "cyogel web");
        APP_DOMAIN = MConfig.getConfig().getString("systeminfo.domain", "");
        APP_STATIC_DOMAIN = MConfig.getConfig().getString("systeminfo.static-domain", "");
        
        APP_DOMAIN_HOME = APP_DOMAIN + "/web/admin";
        AVATAR_DEFAULT = APP_STATIC_DOMAIN + "/common/avatars/avatar2.png";
        IMG_DEFAULT = APP_STATIC_DOMAIN + "/common/imgs/default-image.jpg";
        APP_STATIC_IMG = APP_STATIC_DOMAIN + "/file/img";
        
        AD_USERNAME = MConfig.getConfig().getString("admin.username", "");
        AD_PASSWORD = MConfig.getConfig().getString("admin.password", "");
        
        AD_EMAIL = MConfig.getConfig().getString("admin.email", "");
    }
}
