/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.upload.utils;

import java.io.File;
import java.util.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Aug 10, 2015
 */
public class UploadFormUtil extends HttpServlet{
    private static Logger logger = LoggerFactory.getLogger(UploadFormUtil.class);
    
    public static UploadFormUtil instance = new UploadFormUtil();
    
    private boolean isMultipart;
    private String dirUpload;
    private int maxFileSize = 50 * 1024 * 1024; // 50 Mb.
    private int maxMemSize = 60 * 1024 * 1024; // 60 Mb.
    private File file ;
    private ServletFileUpload upload;
    
    private UploadFormUtil(){
        // Get the file location where it would be stored.
        File wdir = new File("");
        dirUpload = wdir.getAbsolutePath() + File.separator + "public" + File.separator + "uploads";
        
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(maxMemSize);
        // Location to save data that is larger than maxMemSize.
        File wdirUpload = new File(dirUpload);
        if(!wdirUpload.exists()){
            wdirUpload.mkdir();
        }
        factory.setRepository(wdirUpload);

        // Create a new file upload handler
        upload = new ServletFileUpload(factory);
        // maximum file size to be uploaded.
        upload.setSizeMax(maxFileSize);
    }
    
    public static UploadFormUtil getInstance(){
        return instance;
    }
    
    public void getMapFormUpload(HttpServletRequest req, Map<String, FileItem> mapFile, Map<String, String> params){
        try {
            // Check that we have a file upload request
            isMultipart = ServletFileUpload.isMultipartContent(req);
            if(isMultipart ){
                //Upload file.
                // Parse the request to get file items.
                List<FileItem> fileItems = upload.parseRequest(req);

                // Process the uploaded file items
                Iterator i = fileItems.iterator();
                while (i.hasNext()) {
                    FileItem fi = (FileItem)i.next();
                    // Process form file field (input type="file").
                    if (!fi.isFormField()) {
                        // Get the uploaded file parameters
                        String fieldName = fi.getFieldName();
                        mapFile.put(fieldName, fi);
                    } else{
                        // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                        // Get the uploaded file parameters
                        String fieldName = fi.getFieldName();
                        String value = fi.getString();
                        params.put(fieldName, value);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    
}
