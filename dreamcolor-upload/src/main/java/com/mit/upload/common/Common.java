/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.upload.common;

import com.mit.utils.ConfigUtils;

/**
 *
 * @author nghiatc
 * @since Aug 24, 2015
 */
public class Common {
    public static final String DOMAIN_APP;
    public static final String MEDIA_LOAD_SERV_PATH;
    public static final String PHOTO_LOAD_SERV_PATH;
    
    static{
        DOMAIN_APP = ConfigUtils.getConfig().getString("system.domain");
        MEDIA_LOAD_SERV_PATH = ConfigUtils.getConfig().getString("mmedia.servletPath", "/loop/load/media");
        PHOTO_LOAD_SERV_PATH = ConfigUtils.getConfig().getString("mphoto.servletPath", "/loop/load/photo");
    }
}
