/*
 * Copyright 2016 nghiatc.
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

package com.mit.dc.site.common;

import com.github.kevinsawicki.timeago.TimeAgo;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author nghiatc
 * @since Jul 16, 2015
 */
public class Common {
    public static final List<String> publicUrl = new ArrayList<String>();
    public static final List<String> bizAdmin = new ArrayList<String>();
    public static final List<String> CNDAdmin = new ArrayList<String>();
    public static final List<String> saleAccessUrl = new ArrayList<String>();
    public static final String AUTH = "fdgkJHEjd";
    public static final String swdirFile;
    public static final String swdirImg;
    public static final String swdirTmp;
    public static final String swdirCSV;
    public static final String swdirPDB;
    
    
    
    static{
        //publicUrl.add("/");
        publicUrl.add("/web/admin/static");
        publicUrl.add("/web/admin/deny");
        publicUrl.add("/web/admin/login");
        publicUrl.add("/web/admin/logout");
        publicUrl.add("/web/admin/load/edu/content");
        
        //biz admin
        bizAdmin.add("/web/admin/undefined");
        bizAdmin.add("/web/admin/home");
        bizAdmin.add("/web/admin/biz");
        bizAdmin.add("/web/admin/product");
        bizAdmin.add("/web/admin/combo");
        bizAdmin.add("/web/admin/edu");
        bizAdmin.add("/web/admin/order");
        bizAdmin.add("/web/admin/notification");
        bizAdmin.add("/web/admin/");
        bizAdmin.add("/web/admin");
        bizAdmin.add("/web/admin/promotion");
        bizAdmin.add("/web/admin/bizbanner");
        bizAdmin.add("/web/admin/report");
        bizAdmin.add("/web/admin/shopvideo");
        //CND admin
        CNDAdmin.add("/web/admin/cnd");
        
        
        
        File wdir = new File("");
        swdirFile = wdir.getAbsolutePath() + File.separator + "public" + File.separator + "file";
        swdirImg = wdir.getAbsolutePath() + File.separator + "public" + File.separator + "file" + File.separator + "img";
        swdirTmp = wdir.getAbsolutePath() + File.separator + "public" + File.separator + "file" + File.separator + "tmp";
        swdirCSV = wdir.getAbsolutePath() + File.separator + "public" + File.separator + "file" + File.separator + "csv";
        swdirPDB = wdir.getAbsolutePath() + File.separator + "public" + File.separator + "pdb";
        
        
        
    }
    
    public static String buildURICSV(String filename){
        String uri = Configuration.APP_DOMAIN + "/web/admin/csv?f=" + filename;
        return uri;
    }
    
    public static String timeAgo(long millis) {
		TimeAgo time = new TimeAgo();
		return time.timeAgo(millis);
	}
    
    public static String formatDateTime(long millis) {
		String pattern = "dd/MM/yyyy - hh:mm:ss";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		return formatter.format(cal.getTime());
	}
    
}
