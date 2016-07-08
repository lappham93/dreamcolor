/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.dreamcolor.admin.common;

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
        publicUrl.add("/web/admin/load/email");
        
        //saleAccessUrl.add("/web/admin/repcode");
        
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
