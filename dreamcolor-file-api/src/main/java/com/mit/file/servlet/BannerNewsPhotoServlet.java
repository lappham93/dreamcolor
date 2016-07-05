/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.file.servlet;

import com.eclipsesource.json.JsonObject;
import com.mit.dao.photo.BannerNewsPhotoClient;
import com.mit.file.common.HttpHelper;
import com.mit.file.utils.ImageResize;
import com.mit.midutil.MIdNoise;
import com.mit.mphoto.thrift.TMPhoto;
import com.mit.mphoto.thrift.TMPhotoResult;
import hapax.TemplateDataDictionary;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Feb 2, 2016
 */
public class BannerNewsPhotoServlet extends BaseHandler {
    private static Logger logger = LoggerFactory.getLogger(BannerNewsPhotoServlet.class);
    private final int minSize = 20;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            renderBannerNewsPhoto(req, resp);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            TemplateDataDictionary dic = getDictionary();
            JsonObject result = new JsonObject();
            result.set("err", -1);
            result.set("msg", "Execute fail. Please try again.");
            
            String action = req.getParameter("action");
            String callback = req.getParameter("callback");
            if(action != null && !action.isEmpty()) {
//                if("addsm".equalsIgnoreCase(action)){
//                    addSaleMan(req, resp, result);
//                }
                
                if (HttpHelper.isAjaxRequest(req)) {
                    if (callback != null && !callback.isEmpty()) {
                        printStrJSON(callback + "(" + result.toString() + ")", resp);
                    } else {
                        printStrJSON(result.toString(), resp);
                    }
                } else {
                    dic.setVariable("callback", callback);
                    dic.setVariable("data", result.toString());
                    print(applyTemplate(dic, "iframe_callback", req), resp);
                }
            } else{
                printStrJSON(result.toString(), resp);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    private void renderBannerNewsPhoto(HttpServletRequest req, HttpServletResponse resp){
        try {
            long t = System.nanoTime();
            long fileSize = 0L;
            String p = req.getParameter("p");
            String ssize = req.getParameter("size");
            int size = 0;
            if(ssize != null && !ssize.isEmpty()){
                size = Integer.valueOf(ssize);
            }
            if(p != null && !p.isEmpty()){
                //logger.info("BannerNewsPhotoServlet load - pid: " + p);
                long pid = MIdNoise.deNoiseLId(p);
                TMPhotoResult tptrs = BannerNewsPhotoClient.getInstance().getMPhoto(pid);
                if(tptrs != null && tptrs.error >= 0 && tptrs.value != null){
                    TMPhoto tmp = tptrs.getValue();
                    String fileName = tmp.getFilename();
                    String mimeType = tmp.getContentType();
                    byte[] data = tmp.getData();

                    logger.info("BannerNewsPhotoServlet load from rockdb - pid: " + p + " Size: " + size + " FileSize: " + FileUtils.byteCountToDisplaySize(data.length));
                    if(data != null && size > 0){
                        size = size > minSize ? size : minSize;
                        ByteArrayOutputStream out = ImageResize.resizeImage(data, size);
                        if(out != null) {
                            data = out.toByteArray();
                            out.close();
                        }
                    }
                    fileSize = data.length;
                    logger.info("BannerNewsPhotoServlet after resizeImage - pid: " + p + " Size: " + size + " FileSize: " + FileUtils.byteCountToDisplaySize(fileSize));
                    ByteArrayOutputStream outData = new ByteArrayOutputStream(data.length);
                    outData.write(data, 0, data.length);
                    printBigFile(resp, outData, fileName, mimeType);
                } else{
                    logger.info("BannerNewsPhotoServlet load from rockdb - pid: " + p + " Size: " + size + " -------> NULL ");
                    resp.setStatus(HttpStatus.NOT_FOUND_404);
                    print("", resp);
                }
            } else{
                resp.setStatus(HttpStatus.BAD_REQUEST_400);
                print("", resp);
            }
            logger.info("BannerNewsPhotoServlet - pid: " + p + " Size: " + size + " FileSize: " + FileUtils.byteCountToDisplaySize(fileSize) + " Time: " + (System.nanoTime() - t) + "ns ");
        } catch (Exception e) {
            logger.error("renderBannerNewsPhoto " + e.getMessage(), e);
        }
    }
}
