/*
 * Copyright 2015 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mit.dreamcolor.admin.common;

import com.mit.configer.MConfig;



/**
 *
 * @author nghiatc
 * @since May 10, 2015
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
        APP_TITLE = MConfig.getConfig().getString("systeminfo.app-title", "dreamcolor admin");
        APP_DOMAIN = MConfig.getConfig().getString("systeminfo.domain", "");
        APP_STATIC_DOMAIN = MConfig.getConfig().getString("systeminfo.static-domain", "");
        
        APP_PAGING_PAGE_SIZE = MConfig.getConfig().getInt("systeminfo.pagingsize", 10);
        APP_PAGING_NUM_DISPLAY = MConfig.getConfig().getInt("systeminfo.pagingnum", 3);
        
        APP_DOMAIN_HOME = APP_DOMAIN + "/web/admin";
        AVATAR_DEFAULT = APP_STATIC_DOMAIN + "/common/avatars/avatar2.png";
        IMG_DEFAULT = APP_STATIC_DOMAIN + "/common/imgs/default-image.jpg";
        APP_STATIC_IMG = APP_STATIC_DOMAIN + "/file/img";
        
        AD_USERNAME = MConfig.getConfig().getString("admin.username", "");
        AD_PASSWORD = MConfig.getConfig().getString("admin.password", "");
        
        AD_EMAIL = MConfig.getConfig().getString("admin.email", "");
    }
}
