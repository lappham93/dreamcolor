///*
// * Copyright 2016 nghiatc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.mit.dreamcolor.admin.handler;
//
//import com.eclipsesource.json.JsonObject;
//import com.mit.dao.mid.MIdGenLongDAO;
//import com.mit.dao.notification.BannerDAO;
//import com.mit.dao.photo.BannerNewsPhotoClient;
//import com.mit.dao.photo.PhotoCommon;
//import com.mit.dao.product.ProductDAO;
//import com.mit.dao.product.SKUDAO;
//import com.mit.dreamcolor.admin.common.Common;
//import com.mit.dreamcolor.admin.common.Configuration;
//import com.mit.dreamcolor.admin.utils.HttpHelper;
//import com.mit.dreamcolor.admin.utils.UploadFormUtil;
//import com.mit.entities.notification.Banner;
//import com.mit.entities.notification.BannerType;
//import com.mit.entities.notification.OrderBanner;
//import com.mit.entities.notification.ProductBanner;
//import com.mit.entities.notification.WebBanner;
//import com.mit.entities.notification.WelcomeBanner;
//import com.mit.entities.service.Product;
//import com.mit.entities.service.SKU;
//import com.mit.midutil.MIdNoise;
//import com.mit.mphoto.thrift.TMPhoto;
//import com.mit.mphoto.thrift.TMPhotoResult;
//import com.mit.utils.MIMETypeUtil;
//import hapax.TemplateDataDictionary;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.apache.commons.fileupload.FileItem;
//import org.apache.commons.io.FilenameUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// *
// * @author nghiatc
// * @since Feb 2, 2016
// */
//public class BannerHandler extends BaseHandler {
//    private static Logger logger = LoggerFactory.getLogger(ProductHandler.class);
//    
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
//        try {
//            TemplateDataDictionary dic = getDictionary();
//            
//            //select template.
//            String option = req.getParameter("option");
//            if("bpro".equalsIgnoreCase(option)){
//                renderBannerProduct(dic, req, resp);
//                dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "bannerproduct.xtm", req));
//            } else if("bord".equalsIgnoreCase(option)){
//                renderBannerOrther(dic, req, resp);
//                dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "bannerorder.xtm", req));
//            } else if("bwel".equalsIgnoreCase(option)){
//                renderBannerWelcome(dic, req, resp);
//                dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "bannerwelcome.xtm", req));
//            } else{
//                renderBannerWeb(dic, req, resp);
//                dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "bannerweb.xtm", req));
//            }
//            
//            print(applyTemplateLayoutMain(dic, req, resp), resp);
//        } catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//        }
//    }
//    
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
//        try {
//            JsonObject result = new JsonObject();
//            result.set("err", -1);
//            result.set("msg", "Execute fail. Please try again.");
//            
//            String action = "";
//            String callback = "";
//            if(HttpHelper.isMultipartRequest(req)){ // process request multipart form data.
//                Map<String, FileItem> mapFile = new HashMap<String, FileItem>();
//                Map<String, String> params = new HashMap<String, String>();
//                UploadFormUtil.getInstance().getMapFormUpload(req, mapFile, params);
//                if(params != null && !params.isEmpty()){
//                    System.out.println("multipart params: " + params);
//                    System.out.println("multipart mapFile: " + mapFile);
//                    callback = params.containsKey("callback") ? params.get("callback") : "";
//                    action = params.containsKey("action") ? params.get("action") : "";
//                    if(action != null && !action.isEmpty()) {
//                        if("addbweb".equalsIgnoreCase(action)){
//                            addBannerWeb(req, resp, result,mapFile, params);
//                        } else if("cimg".equalsIgnoreCase(action)){
//                            changeBannerWebPhoto(req, resp, result, mapFile, params);
//                        } else if("addborder".equalsIgnoreCase(action)){
//                            addBannerOrder(req, resp, result, mapFile, params);
//                        } else if("cimgbord".equalsIgnoreCase(action)){
//                            changeBannerOrderPhoto(req, resp, result, mapFile, params);
//                        } else if("addbwel".equalsIgnoreCase(action)){
//                            addBannerWelcome(req, resp, result, mapFile, params);
//                        } else if("cimgbwel".equalsIgnoreCase(action)){
//                            changeBannerWelcomePhoto(req, resp, result, mapFile, params);
//                        } else if("addbpro".equalsIgnoreCase(action)){
//                            addBannerProduct(req, resp, result, mapFile, params);
//                        } else if("cimgbpro".equalsIgnoreCase(action)){
//                            changeBannerProductPhoto(req, resp, result, mapFile, params);
//                        }
//                        
//                    }
//                }
//            } else{ // process request nomal.
//                action = req.getParameter("action");
//                callback = req.getParameter("callback");
//                if(action != null && !action.isEmpty()) {
//                    if("getwb".equalsIgnoreCase(action)){
//                        getBannerWeb(req, resp, result);
//                    } else if("editwb".equalsIgnoreCase(action)){
//                        editBannerWeb(req, resp, result);
//                    } else if("getob".equalsIgnoreCase(action)){
//                        getBannerOrder(req, resp, result);
//                    } else if("editob".equalsIgnoreCase(action)){
//                        editBannerOrder(req, resp, result);
//                    } else if("getwelb".equalsIgnoreCase(action)){
//                        getBannerWelcome(req, resp, result);
//                    } else if("editwelb".equalsIgnoreCase(action)){
//                        editBannerWelcome(req, resp, result);
//                    } else if("getpb".equalsIgnoreCase(action)){
//                        getBannerProduct(req, resp, result);
//                    } else if("editpb".equalsIgnoreCase(action)){
//                        editBannerProduct(req, resp, result);
//                    }
//                }
//            }
//            
//            //render JsonObject.
//            if(action != null && !action.isEmpty()) {
//                if (HttpHelper.isAjaxRequest(req)) {
//                    if (callback != null && !callback.isEmpty()) {
//                        printStrJSON(callback + "(" + result.toString() + ")", resp);
//                    } else {
//                        printStrJSON(result.toString(), resp);
//                    }
//                } else {
//                    TemplateDataDictionary dic = getDictionary();
//                    dic.setVariable("callback", callback);
//                    dic.setVariable("data", result.toString());
//                    print(applyTemplate(dic, "iframe_callback", req), resp);
//                }
//            } else{
//                printStrJSON(result.toString(), resp);
//            }
//        } catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//        }
//    }
//    
//    private void renderBannerWeb(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException{
//        List<Banner> listB = BannerDAO.getInstance().getAllListByType(BannerType.WEB.getValue());
//        //System.out.println("listB: " + listB);
//        if(listB != null && !listB.isEmpty()){
//            int i = 1;
//            for(Banner b : listB){
//                if(b.getType() == BannerType.WEB.getValue()){
//                    WebBanner wb = (WebBanner) b;
//                    //System.out.println("wb: " + wb);
//                    TemplateDataDictionary loopRow = dic.addSection("loop_row");
//                    loopRow.setVariable("NO", String.valueOf(i));
//                    loopRow.setVariable("ID", MIdNoise.enNoiseLId(wb.getuId()));
//                    String uripri = buildURIImg(wb.getThumb());
//                    uripri = uripri != null && !uripri.isEmpty() ? uripri : Configuration.IMG_DEFAULT;
//                    loopRow.setVariable("URI_TP", uripri);
//                    loopRow.setVariable("URL", URLDecoder.decode(wb.getId(), "UTF-8"));
//                    loopRow.setVariable("MSG", wb.getMsg());
//                    if(wb.getStatus() > 0){
//                        loopRow.addSection("STATUS_ON");
//                    } else{
//                        loopRow.addSection("STATUS_OFF");
//                    }
//                    
//                    i++;
//                }
//            }
//        } else{
//            dic.addSection("table_empty");
//        }
//        
//    }
//    
//    private void renderBannerOrther(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException{
//        List<Banner> listB = BannerDAO.getInstance().getAllListByType(BannerType.ORDER.getValue());
//        System.out.println("listB: " + listB);
//        if(listB != null && !listB.isEmpty()){
//            int i = 1;
//            for(Banner b : listB){
//                if(b.getType() == BannerType.ORDER.getValue()){
//                    OrderBanner ob = (OrderBanner) b;
//                    //System.out.println("wb: " + wb);
//                    TemplateDataDictionary loopRow = dic.addSection("loop_row");
//                    loopRow.setVariable("NO", String.valueOf(i));
//                    loopRow.setVariable("ID", MIdNoise.enNoiseLId(ob.getuId()));
//                    String uripri = buildURIImg(ob.getThumb());
//                    uripri = uripri != null && !uripri.isEmpty() ? uripri : Configuration.IMG_DEFAULT;
//                    loopRow.setVariable("URI_TP", uripri);
//                    loopRow.setVariable("ORDER_NUMBER", ob.getId());
//                    loopRow.setVariable("MSG", ob.getMsg());
//                    if(ob.getStatus() > 0){
//                        loopRow.addSection("STATUS_ON");
//                    } else{
//                        loopRow.addSection("STATUS_OFF");
//                    }
//                    
//                    i++;
//                }
//            }
//        } else{
//            dic.addSection("table_empty");
//        }
//        
//    }
//    
//    private void renderBannerWelcome(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException{
//        List<Banner> listB = BannerDAO.getInstance().getAllListByType(BannerType.WELCOME.getValue());
//        System.out.println("listB: " + listB);
//        if(listB != null && !listB.isEmpty()){
//            int i = 1;
//            for(Banner b : listB){
//                if(b.getType() == BannerType.WELCOME.getValue()){
//                    WelcomeBanner wb = (WelcomeBanner) b;
//                    //System.out.println("wb: " + wb);
//                    TemplateDataDictionary loopRow = dic.addSection("loop_row");
//                    loopRow.setVariable("NO", String.valueOf(i));
//                    loopRow.setVariable("ID", MIdNoise.enNoiseLId(wb.getuId()));
//                    String uripri = buildURIImg(wb.getThumb());
//                    uripri = uripri != null && !uripri.isEmpty() ? uripri : Configuration.IMG_DEFAULT;
//                    loopRow.setVariable("URI_TP", uripri);
//                    loopRow.setVariable("MSG", wb.getMsg());
//                    if(wb.getStatus() > 0){
//                        loopRow.addSection("STATUS_ON");
//                    } else{
//                        loopRow.addSection("STATUS_OFF");
//                    }
//                    
//                    i++;
//                }
//            }
//        } else{
//            dic.addSection("table_empty");
//        }
//        
//    }
//    
//    private void renderBannerProduct(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException{
//        List<Product> listP = ProductDAO.getInstance().listAll();
//        Map<Long, String> mapP = new HashMap<Long, String>();
//        if(listP != null && !listP.isEmpty()){
//            for(Product p : listP){
//                if(p.getStatus() > 0){
//                    TemplateDataDictionary loopOptionProduct = dic.addSection("loop_option_product");
//                    loopOptionProduct.setVariable("KEY", MIdNoise.enNoiseLId(p.getId()));
//                    loopOptionProduct.setVariable("VALUE", p.getModel());
//                }
//                mapP.put(p.getId(), p.getModel());
//            }
//        }
//        
//        List<Banner> listB = BannerDAO.getInstance().getAllListByType(BannerType.PRODUCT.getValue());
//        System.out.println("listB: " + listB);
//        if(listB != null && !listB.isEmpty()){
//            int i = 1;
//            for(Banner b : listB){
//                if(b.getType() == BannerType.PRODUCT.getValue()){
//                    ProductBanner pb = (ProductBanner) b;
//                    //System.out.println("pb: " + pb);
//                    TemplateDataDictionary loopRow = dic.addSection("loop_row");
//                    loopRow.setVariable("NO", String.valueOf(i));
//                    loopRow.setVariable("ID", MIdNoise.enNoiseLId(pb.getuId()));
//                    String uripri = buildURIImg(pb.getThumb());
//                    uripri = uripri != null && !uripri.isEmpty() ? uripri : Configuration.IMG_DEFAULT;
//                    loopRow.setVariable("URI_TP", uripri);
//                    
//                    long pid = pb.getProductId();
//                    String nameProduct = mapP.containsKey(pid) ? mapP.get(pid) : "";
//                    loopRow.setVariable("PRODUCT", nameProduct);
//                    
//                    long sid = pb.getSkuId();
//                    String nameSku = "";
//                    SKU sku = SKUDAO.getInstance().getById(sid);
//                    if(sku != null){
//                        nameSku = sku.getName();
//                    }
//                    loopRow.setVariable("SKU", nameSku);
//                    
//                    loopRow.setVariable("MSG", pb.getMsg());
//                    if(pb.getStatus() > 0){
//                        loopRow.addSection("STATUS_ON");
//                    } else{
//                        loopRow.addSection("STATUS_OFF");
//                    }
//                    
//                    i++;
//                }
//            }
//        } else{
//            dic.addSection("table_empty");
//        }
//        
//    }
//    
//    public String buildURIImg(long pid){
//        try {
//            TMPhotoResult tmprs = BannerNewsPhotoClient.getInstance().getMPhoto(pid);
//            if(tmprs != null && tmprs.value != null){
//                byte[] dataImg = tmprs.value.getData();
//                String filename = tmprs.value.getFilename();
//                String ext = FilenameUtils.getExtension(filename);
//                String pidn = MIdNoise.enNoiseLId(pid);
//                String pathSaveImg = Common.swdirPDB + File.separator + "bn" + File.separator + pidn + "." + ext;
//                String uriImg = Configuration.APP_STATIC_DOMAIN + "/pdb/bn/" + pidn + "." + ext;
//                File img = new File(pathSaveImg);
//                if(!img.exists()){
//                    FileOutputStream fos = new FileOutputStream(img);
//                    fos.write(dataImg, 0, dataImg.length);
//                    fos.close();
//                }
//                return uriImg;
//            }
//        } catch (Exception e) {
//        }
//        return "";
//    }
//    
//    
//    
//    
//    private void addBannerWeb(HttpServletRequest req, HttpServletResponse resp, JsonObject result, Map<String, FileItem> mapFile, Map<String, String> params) throws IOException{
//        try {
//            if(params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()){
//                String surl = params.containsKey("url") ? params.get("url") : "";
//                String smessage = params.containsKey("message") ? params.get("message") : "";
//                String sstatus = params.containsKey("status") ? params.get("status") : "";
//                FileItem thumb = mapFile.containsKey("thumb") ? mapFile.get("thumb") : null;
//                if(surl != null && !surl.isEmpty() && smessage != null && !smessage.isEmpty() 
//                        && thumb != null && thumb.getSize() > 0 ){
//                    // save image.
//                    Set<Long> listPhoto = new HashSet<Long>();
//                    long idThumb = MIdGenLongDAO.getInstance(PhotoCommon.bannerNewsPhotoIdGen).getNext();
//                    if(idThumb >= 0){
//                        TMPhoto tmp = new TMPhoto();
//                        tmp.setId(idThumb);
//                        String filename = thumb.getName();
//                        tmp.setFilename(filename);
//                        tmp.setData(thumb.get());
//                        String ext = FilenameUtils.getExtension(filename);
//                        String contentType = MIMETypeUtil.getInstance().getMIMETypeImage(ext);
//                        tmp.setContentType(contentType);
//                        int err = BannerNewsPhotoClient.getInstance().putMPhoto(tmp);
//                        listPhoto.add(idThumb);
//                        if(err < 0){
//                            result.set("err", -1);
//                            result.set("msg", "Error no insert thumb photo.");
//                            return;
//                        }
//                    } else{
//                        result.set("err", -1);
//                        result.set("msg", "Error gen id thumb photo.");
//                        return;
//                    }
//                    
//                    //save WebBanner.
//                    int status = Integer.valueOf(sstatus);
//                    surl = URLEncoder.encode(surl, "UTF-8");
//                    WebBanner wb = new WebBanner();
//                    wb.setuId(-1);
//                    wb.setId(surl);
//                    wb.setThumb(idThumb);
//                    wb.setMsg(smessage);
//                    wb.setStatus(status);
//                    wb.setType(BannerType.WEB.getValue());
//                    
//                    System.out.println(wb.toString());
//                    int err = BannerDAO.getInstance().insert(wb);
//                    if(err >= 0){
//                        PhotoCommon.SET_BANNER_NEWS_PHOTO.addAll(PhotoCommon.BANNER_NEWS_PHOTO_TABLE, listPhoto);
//                        
//                        result.set("err", 0);
//                        result.set("msg", "Add Banner Web successfully.");
//                    } else{
//                        result.set("err", -1);
//                        result.set("msg", "Add Banner Web fail.");
//                    }
//                } else{
//                    result.set("err", -1);
//                    result.set("msg", "Parameter invalid.");
//                }
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Parameter invalid.");
//            }
//        } catch (Exception e) {
//            logger.error("BannerHandler.addBannerWeb: " + e, e);
//        }
//    }
//    
//    private void changeBannerWebPhoto(HttpServletRequest req, HttpServletResponse resp, JsonObject result, Map<String, FileItem> mapFile, Map<String, String> params) throws IOException{
//        try {
//            if(params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()){
//                String siidp = params.containsKey("iidp") ? params.get("iidp") : "";
//                FileItem thumbphoto = mapFile.containsKey("cimg") ? mapFile.get("cimg") : null;
//                if(siidp != null && !siidp.isEmpty() && thumbphoto != null && thumbphoto.getSize() > 0){
//                    long id = MIdNoise.deNoiseLId(siidp);
//                    WebBanner wb = (WebBanner) BannerDAO.getInstance().getById(id);
//                    if(wb != null){
//                        // save image.
//                        long idThumb = MIdGenLongDAO.getInstance(PhotoCommon.bannerNewsPhotoIdGen).getNext();
//                        if(idThumb >= 0){
//                            TMPhoto tmp = new TMPhoto();
//                            tmp.setId(idThumb);
//                            String filename = thumbphoto.getName();
//                            tmp.setFilename(filename);
//                            tmp.setData(thumbphoto.get());
//                            String ext = FilenameUtils.getExtension(filename);
//                            String contentType = MIMETypeUtil.getInstance().getMIMETypeImage(ext);
//                            tmp.setContentType(contentType);
//                            int err = BannerNewsPhotoClient.getInstance().putMPhoto(tmp);
//                            if(err >= 0){
//                                PhotoCommon.SET_BANNER_NEWS_PHOTO.add(PhotoCommon.BANNER_NEWS_PHOTO_TABLE, idThumb);
//                                wb.setThumb(idThumb);
//                                int err1 = BannerDAO.getInstance().update(wb);
//                                if(err1 >= 0){
//                                    result.set("err", 0);
//                                    result.set("msg", "Change thumb photo Banner Web successfully.");
//                                    return;
//                                } else{
//                                    result.set("err", -1);
//                                    result.set("msg", "Change thumb photo Banner Web fail.");
//                                    return;
//                                }
//                            } else{
//                                result.set("err", -1);
//                                result.set("msg", "Error no insert thumb photo Banner Web.");
//                                return;
//                            }
//                        } else{
//                            result.set("err", -1);
//                            result.set("msg", "Error gen id thumb photo Banner Web.");
//                            return;
//                        }
//                    } else{
//                        result.set("err", -1);
//                        result.set("msg", "Banner Web not exist.");
//                        return;
//                    }
//                } else{
//                    result.set("err", -1);
//                    result.set("msg", "Parameter invalid.");
//                }
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Parameter invalid.");
//            }
//        } catch (Exception e) {
//            logger.error("BannerHandler.changeBannerWebPhoto: " + e, e);
//        }
//    }
//    
//    private void getBannerWeb(HttpServletRequest req, HttpServletResponse resp, JsonObject result) throws UnsupportedEncodingException{
//        String sid = req.getParameter("id");
//        if(sid != null && !sid.isEmpty()){
//            long id = MIdNoise.deNoiseLId(sid);
//            WebBanner wb = (WebBanner) BannerDAO.getInstance().getById(id);
//            if(wb != null){
//                JsonObject owb = new JsonObject();
//                owb.set("id", sid);
//                owb.set("url", URLDecoder.decode(wb.getId(), "UTF-8"));
//                owb.set("msg", wb.getMsg());
//                String status = "";
//                if(wb.getStatus() == 0){
//                    status = "off";
//                } else{
//                    status = "on";
//                }
//                owb.set("status", status);
//                owb.set("idstatus", wb.getStatus());
//                
//                result.set("err", 0);
//                result.set("owb", owb);
//                result.set("msg", "Get Banner Web successfully.");
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Get Banner Web fail.");
//            }
//        } else{
//            result.set("err", -1);
//            result.set("msg", "Parameter invaliad.");
//        }
//    }
//    
//    private void editBannerWeb(HttpServletRequest req, HttpServletResponse resp, JsonObject result) throws UnsupportedEncodingException{
//        String sid = req.getParameter("eidp");
//        String seurl = req.getParameter("eurl");
//        String semsg = req.getParameter("emsg");
//        String sestatus = req.getParameter("estatus");
//        if(sid != null && !sid.isEmpty() && seurl != null && !seurl.isEmpty()
//                && semsg != null && !semsg.isEmpty() && sestatus != null && !sestatus.isEmpty() ){
//            long id = MIdNoise.deNoiseLId(sid);
//            WebBanner wb = (WebBanner) BannerDAO.getInstance().getById(id);
//            if(wb != null){
//                String surl = URLEncoder.encode(seurl, "UTF-8");
//                int status = Integer.valueOf(sestatus);
//                wb.setId(surl);
//                wb.setMsg(semsg);
//                wb.setStatus(status);
//
//                System.out.println(wb.toString());
//                int err = BannerDAO.getInstance().update(wb);
//                if(err >= 0){
//                    result.set("err", 0);
//                    result.set("msg", "Edit Banner Web successfully.");
//                } else{
//                    result.set("err", -1);
//                    result.set("msg", "Edit Banner Web fail.");
//                }
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Banner Web not exist.");
//            }
//        } else{
//            result.set("err", -1);
//            result.set("msg", "Parameter invaliad.");
//        }
//    }
//    
//    
//    private void addBannerOrder(HttpServletRequest req, HttpServletResponse resp, JsonObject result, Map<String, FileItem> mapFile, Map<String, String> params) throws IOException{
//        try {
//            if(params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()){
//                String sordnumber = params.containsKey("ordnumber") ? params.get("ordnumber") : "";
//                String smessage = params.containsKey("message") ? params.get("message") : "";
//                String sstatus = params.containsKey("status") ? params.get("status") : "";
//                FileItem thumb = mapFile.containsKey("thumb") ? mapFile.get("thumb") : null;
//                if(sordnumber != null && !sordnumber.isEmpty() && smessage != null && !smessage.isEmpty() 
//                        && thumb != null && thumb.getSize() > 0 ){
//                    // save image.
//                    Set<Long> listPhoto = new HashSet<Long>();
//                    long idThumb = MIdGenLongDAO.getInstance(PhotoCommon.bannerNewsPhotoIdGen).getNext();
//                    if(idThumb >= 0){
//                        TMPhoto tmp = new TMPhoto();
//                        tmp.setId(idThumb);
//                        String filename = thumb.getName();
//                        tmp.setFilename(filename);
//                        tmp.setData(thumb.get());
//                        String ext = FilenameUtils.getExtension(filename);
//                        String contentType = MIMETypeUtil.getInstance().getMIMETypeImage(ext);
//                        tmp.setContentType(contentType);
//                        int err = BannerNewsPhotoClient.getInstance().putMPhoto(tmp);
//                        listPhoto.add(idThumb);
//                        if(err < 0){
//                            result.set("err", -1);
//                            result.set("msg", "Error no insert thumb photo.");
//                            return;
//                        }
//                    } else{
//                        result.set("err", -1);
//                        result.set("msg", "Error gen id thumb photo.");
//                        return;
//                    }
//                    
//                    //save OrderBanner.
//                    int status = Integer.valueOf(sstatus);
//                    OrderBanner ob = new OrderBanner();
//                    ob.setuId(-1);
//                    ob.setId(sordnumber);
//                    ob.setThumb(idThumb);
//                    ob.setMsg(smessage);
//                    ob.setStatus(status);
//                    ob.setType(BannerType.ORDER.getValue());
//                    
//                    System.out.println(ob.toString());
//                    int err = BannerDAO.getInstance().insert(ob);
//                    if(err >= 0){
//                        PhotoCommon.SET_BANNER_NEWS_PHOTO.addAll(PhotoCommon.BANNER_NEWS_PHOTO_TABLE, listPhoto);
//                        
//                        result.set("err", 0);
//                        result.set("msg", "Add Banner Order successfully.");
//                    } else{
//                        result.set("err", -1);
//                        result.set("msg", "Add Banner Order fail.");
//                    }
//                } else{
//                    result.set("err", -1);
//                    result.set("msg", "Parameter invalid.");
//                }
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Parameter invalid.");
//            }
//        } catch (Exception e) {
//            logger.error("BannerHandler.addBannerOrder: " + e, e);
//        }
//    }
//    
//    private void changeBannerOrderPhoto(HttpServletRequest req, HttpServletResponse resp, JsonObject result, Map<String, FileItem> mapFile, Map<String, String> params) throws IOException{
//        try {
//            if(params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()){
//                String siidp = params.containsKey("iidp") ? params.get("iidp") : "";
//                FileItem thumbphoto = mapFile.containsKey("cimg") ? mapFile.get("cimg") : null;
//                if(siidp != null && !siidp.isEmpty() && thumbphoto != null && thumbphoto.getSize() > 0){
//                    long id = MIdNoise.deNoiseLId(siidp);
//                    OrderBanner ob = (OrderBanner) BannerDAO.getInstance().getById(id);
//                    if(ob != null){
//                        // save image.
//                        long idThumb = MIdGenLongDAO.getInstance(PhotoCommon.bannerNewsPhotoIdGen).getNext();
//                        if(idThumb >= 0){
//                            TMPhoto tmp = new TMPhoto();
//                            tmp.setId(idThumb);
//                            String filename = thumbphoto.getName();
//                            tmp.setFilename(filename);
//                            tmp.setData(thumbphoto.get());
//                            String ext = FilenameUtils.getExtension(filename);
//                            String contentType = MIMETypeUtil.getInstance().getMIMETypeImage(ext);
//                            tmp.setContentType(contentType);
//                            int err = BannerNewsPhotoClient.getInstance().putMPhoto(tmp);
//                            if(err >= 0){
//                                PhotoCommon.SET_BANNER_NEWS_PHOTO.add(PhotoCommon.BANNER_NEWS_PHOTO_TABLE, idThumb);
//                                ob.setThumb(idThumb);
//                                int err1 = BannerDAO.getInstance().update(ob);
//                                if(err1 >= 0){
//                                    result.set("err", 0);
//                                    result.set("msg", "Change thumb photo Banner Order successfully.");
//                                    return;
//                                } else{
//                                    result.set("err", -1);
//                                    result.set("msg", "Change thumb photo Banner Order fail.");
//                                    return;
//                                }
//                            } else{
//                                result.set("err", -1);
//                                result.set("msg", "Error no insert thumb photo Banner Order.");
//                                return;
//                            }
//                        } else{
//                            result.set("err", -1);
//                            result.set("msg", "Error gen id thumb photo Banner Order.");
//                            return;
//                        }
//                    } else{
//                        result.set("err", -1);
//                        result.set("msg", "Banner Order not exist.");
//                        return;
//                    }
//                } else{
//                    result.set("err", -1);
//                    result.set("msg", "Parameter invalid.");
//                }
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Parameter invalid.");
//            }
//        } catch (Exception e) {
//            logger.error("BannerHandler.changeBannerOrderPhoto: " + e, e);
//        }
//    }
//    
//    private void getBannerOrder(HttpServletRequest req, HttpServletResponse resp, JsonObject result) throws UnsupportedEncodingException{
//        String sid = req.getParameter("id");
//        if(sid != null && !sid.isEmpty()){
//            long id = MIdNoise.deNoiseLId(sid);
//            OrderBanner ob = (OrderBanner) BannerDAO.getInstance().getById(id);
//            if(ob != null){
//                JsonObject oob = new JsonObject();
//                oob.set("id", sid);
//                oob.set("ordnumber", ob.getId());
//                oob.set("msg", ob.getMsg());
//                String status = "";
//                if(ob.getStatus() == 0){
//                    status = "off";
//                } else{
//                    status = "on";
//                }
//                oob.set("status", status);
//                oob.set("idstatus", ob.getStatus());
//                
//                result.set("err", 0);
//                result.set("oob", oob);
//                result.set("msg", "Get Banner Order successfully.");
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Get Banner Order fail.");
//            }
//        } else{
//            result.set("err", -1);
//            result.set("msg", "Parameter invaliad.");
//        }
//    }
//    
//    private void editBannerOrder(HttpServletRequest req, HttpServletResponse resp, JsonObject result) throws UnsupportedEncodingException{
//        String sid = req.getParameter("eidp");
//        String seordnumber = req.getParameter("eordnumber");
//        String semsg = req.getParameter("emsg");
//        String sestatus = req.getParameter("estatus");
//        if(sid != null && !sid.isEmpty() && seordnumber != null && !seordnumber.isEmpty()
//                && semsg != null && !semsg.isEmpty() && sestatus != null && !sestatus.isEmpty() ){
//            long id = MIdNoise.deNoiseLId(sid);
//            OrderBanner ob = (OrderBanner) BannerDAO.getInstance().getById(id);
//            if(ob != null){
//                int status = Integer.valueOf(sestatus);
//                ob.setId(seordnumber);
//                ob.setMsg(semsg);
//                ob.setStatus(status);
//
//                System.out.println(ob.toString());
//                int err = BannerDAO.getInstance().update(ob);
//                if(err >= 0){
//                    result.set("err", 0);
//                    result.set("msg", "Edit Banner Order successfully.");
//                } else{
//                    result.set("err", -1);
//                    result.set("msg", "Edit Banner Order fail.");
//                }
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Banner Order not exist.");
//            }
//        } else{
//            result.set("err", -1);
//            result.set("msg", "Parameter invaliad.");
//        }
//    }
//    
//    
//    private void addBannerWelcome(HttpServletRequest req, HttpServletResponse resp, JsonObject result, Map<String, FileItem> mapFile, Map<String, String> params) throws IOException{
//        try {
//            if(params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()){
//                String smessage = params.containsKey("message") ? params.get("message") : "";
//                String sstatus = params.containsKey("status") ? params.get("status") : "";
//                FileItem thumb = mapFile.containsKey("thumb") ? mapFile.get("thumb") : null;
//                if(smessage != null && !smessage.isEmpty() && thumb != null && thumb.getSize() > 0 ){
//                    // save image.
//                    Set<Long> listPhoto = new HashSet<Long>();
//                    long idThumb = MIdGenLongDAO.getInstance(PhotoCommon.bannerNewsPhotoIdGen).getNext();
//                    if(idThumb >= 0){
//                        TMPhoto tmp = new TMPhoto();
//                        tmp.setId(idThumb);
//                        String filename = thumb.getName();
//                        tmp.setFilename(filename);
//                        tmp.setData(thumb.get());
//                        String ext = FilenameUtils.getExtension(filename);
//                        String contentType = MIMETypeUtil.getInstance().getMIMETypeImage(ext);
//                        tmp.setContentType(contentType);
//                        int err = BannerNewsPhotoClient.getInstance().putMPhoto(tmp);
//                        listPhoto.add(idThumb);
//                        if(err < 0){
//                            result.set("err", -1);
//                            result.set("msg", "Error no insert thumb photo.");
//                            return;
//                        }
//                    } else{
//                        result.set("err", -1);
//                        result.set("msg", "Error gen id thumb photo.");
//                        return;
//                    }
//                    
//                    //save OrderBanner.
//                    int status = Integer.valueOf(sstatus);
//                    WelcomeBanner wb = new WelcomeBanner();
//                    wb.setuId(-1);
//                    wb.setThumb(idThumb);
//                    wb.setMsg(smessage);
//                    wb.setStatus(status);
//                    wb.setType(BannerType.WELCOME.getValue());
//                    
//                    //System.out.println(wb.toString());
//                    int err = BannerDAO.getInstance().insert(wb);
//                    if(err >= 0){
//                        PhotoCommon.SET_BANNER_NEWS_PHOTO.addAll(PhotoCommon.BANNER_NEWS_PHOTO_TABLE, listPhoto);
//                        
//                        result.set("err", 0);
//                        result.set("msg", "Add Banner Welcome successfully.");
//                    } else{
//                        result.set("err", -1);
//                        result.set("msg", "Add Banner Welcome fail.");
//                    }
//                } else{
//                    result.set("err", -1);
//                    result.set("msg", "Parameter invalid.");
//                }
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Parameter invalid.");
//            }
//        } catch (Exception e) {
//            logger.error("BannerHandler.addBannerWelcome: " + e, e);
//        }
//    }
//    
//    private void changeBannerWelcomePhoto(HttpServletRequest req, HttpServletResponse resp, JsonObject result, Map<String, FileItem> mapFile, Map<String, String> params) throws IOException{
//        try {
//            if(params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()){
//                String siidp = params.containsKey("iidp") ? params.get("iidp") : "";
//                FileItem thumbphoto = mapFile.containsKey("cimg") ? mapFile.get("cimg") : null;
//                if(siidp != null && !siidp.isEmpty() && thumbphoto != null && thumbphoto.getSize() > 0){
//                    long id = MIdNoise.deNoiseLId(siidp);
//                    WelcomeBanner wb = (WelcomeBanner) BannerDAO.getInstance().getById(id);
//                    if(wb != null){
//                        // save image.
//                        long idThumb = MIdGenLongDAO.getInstance(PhotoCommon.bannerNewsPhotoIdGen).getNext();
//                        if(idThumb >= 0){
//                            TMPhoto tmp = new TMPhoto();
//                            tmp.setId(idThumb);
//                            String filename = thumbphoto.getName();
//                            tmp.setFilename(filename);
//                            tmp.setData(thumbphoto.get());
//                            String ext = FilenameUtils.getExtension(filename);
//                            String contentType = MIMETypeUtil.getInstance().getMIMETypeImage(ext);
//                            tmp.setContentType(contentType);
//                            int err = BannerNewsPhotoClient.getInstance().putMPhoto(tmp);
//                            if(err >= 0){
//                                PhotoCommon.SET_BANNER_NEWS_PHOTO.add(PhotoCommon.BANNER_NEWS_PHOTO_TABLE, idThumb);
//                                wb.setThumb(idThumb);
//                                int err1 = BannerDAO.getInstance().update(wb);
//                                if(err1 >= 0){
//                                    result.set("err", 0);
//                                    result.set("msg", "Change thumb photo Banner Welcome successfully.");
//                                    return;
//                                } else{
//                                    result.set("err", -1);
//                                    result.set("msg", "Change thumb photo Banner Welcome fail.");
//                                    return;
//                                }
//                            } else{
//                                result.set("err", -1);
//                                result.set("msg", "Error no insert thumb photo Banner Welcome.");
//                                return;
//                            }
//                        } else{
//                            result.set("err", -1);
//                            result.set("msg", "Error gen id thumb photo Banner Welcome.");
//                            return;
//                        }
//                    } else{
//                        result.set("err", -1);
//                        result.set("msg", "Banner Welcome not exist.");
//                        return;
//                    }
//                } else{
//                    result.set("err", -1);
//                    result.set("msg", "Parameter invalid.");
//                }
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Parameter invalid.");
//            }
//        } catch (Exception e) {
//            logger.error("BannerHandler.changeBannerWelcomePhoto: " + e, e);
//        }
//    }
//    
//    private void getBannerWelcome(HttpServletRequest req, HttpServletResponse resp, JsonObject result) throws UnsupportedEncodingException{
//        String sid = req.getParameter("id");
//        if(sid != null && !sid.isEmpty()){
//            long id = MIdNoise.deNoiseLId(sid);
//            WelcomeBanner ob = (WelcomeBanner) BannerDAO.getInstance().getById(id);
//            if(ob != null){
//                JsonObject owb = new JsonObject();
//                owb.set("id", sid);
//                owb.set("msg", ob.getMsg());
//                String status = "";
//                if(ob.getStatus() == 0){
//                    status = "off";
//                } else{
//                    status = "on";
//                }
//                owb.set("status", status);
//                owb.set("idstatus", ob.getStatus());
//                
//                result.set("err", 0);
//                result.set("owb", owb);
//                result.set("msg", "Get Banner Welcome successfully.");
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Get Banner Welcome fail.");
//            }
//        } else{
//            result.set("err", -1);
//            result.set("msg", "Parameter invaliad.");
//        }
//    }
//    
//    private void editBannerWelcome(HttpServletRequest req, HttpServletResponse resp, JsonObject result) throws UnsupportedEncodingException{
//        String sid = req.getParameter("eidp");
//        String semsg = req.getParameter("emsg");
//        String sestatus = req.getParameter("estatus");
//        if(sid != null && !sid.isEmpty() && semsg != null && !semsg.isEmpty() && sestatus != null && !sestatus.isEmpty() ){
//            long id = MIdNoise.deNoiseLId(sid);
//            WelcomeBanner wb = (WelcomeBanner) BannerDAO.getInstance().getById(id);
//            if(wb != null){
//                int status = Integer.valueOf(sestatus);
//                wb.setMsg(semsg);
//                wb.setStatus(status);
//
//                System.out.println(wb.toString());
//                int err = BannerDAO.getInstance().update(wb);
//                if(err >= 0){
//                    result.set("err", 0);
//                    result.set("msg", "Edit Banner Welcome successfully.");
//                } else{
//                    result.set("err", -1);
//                    result.set("msg", "Edit Banner Welcome fail.");
//                }
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Banner Welcome not exist.");
//            }
//        } else{
//            result.set("err", -1);
//            result.set("msg", "Parameter invaliad.");
//        }
//    }
//    
//    
//    private void addBannerProduct(HttpServletRequest req, HttpServletResponse resp, JsonObject result, Map<String, FileItem> mapFile, Map<String, String> params) throws IOException{
//        try {
//            if(params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()){
//                String spid = params.containsKey("pid") ? params.get("pid") : "";
//                String ssid = params.containsKey("sid") ? params.get("sid") : "";
//                String smessage = params.containsKey("message") ? params.get("message") : "";
//                String sstatus = params.containsKey("status") ? params.get("status") : "";
//                FileItem thumb = mapFile.containsKey("thumb") ? mapFile.get("thumb") : null;
//                if(spid != null && !spid.isEmpty() && ssid != null && !ssid.isEmpty() 
//                        && smessage != null && !smessage.isEmpty() && thumb != null && thumb.getSize() > 0 ){
//                    // save image.
//                    Set<Long> listPhoto = new HashSet<Long>();
//                    long idThumb = MIdGenLongDAO.getInstance(PhotoCommon.bannerNewsPhotoIdGen).getNext();
//                    if(idThumb >= 0){
//                        TMPhoto tmp = new TMPhoto();
//                        tmp.setId(idThumb);
//                        String filename = thumb.getName();
//                        tmp.setFilename(filename);
//                        tmp.setData(thumb.get());
//                        String ext = FilenameUtils.getExtension(filename);
//                        String contentType = MIMETypeUtil.getInstance().getMIMETypeImage(ext);
//                        tmp.setContentType(contentType);
//                        int err = BannerNewsPhotoClient.getInstance().putMPhoto(tmp);
//                        listPhoto.add(idThumb);
//                        if(err < 0){
//                            result.set("err", -1);
//                            result.set("msg", "Error no insert thumb photo.");
//                            return;
//                        }
//                    } else{
//                        result.set("err", -1);
//                        result.set("msg", "Error gen id thumb photo.");
//                        return;
//                    }
//                    
//                    //save ProductBanner.
//                    int status = Integer.valueOf(sstatus);
//                    long pid = MIdNoise.deNoiseLId(spid);
//                    long sid = MIdNoise.deNoiseLId(ssid);
//                    ProductBanner pb = new ProductBanner();
//                    pb.setuId(-1);
//                    pb.setProductId(pid);
//                    pb.setSkuId(sid);
//                    pb.setThumb(idThumb);
//                    pb.setMsg(smessage);
//                    pb.setStatus(status);
//                    pb.setType(BannerType.PRODUCT.getValue());
//                    
//                    //System.out.println(wb.toString());
//                    int err = BannerDAO.getInstance().insert(pb);
//                    if(err >= 0){
//                        PhotoCommon.SET_BANNER_NEWS_PHOTO.addAll(PhotoCommon.BANNER_NEWS_PHOTO_TABLE, listPhoto);
//                        
//                        result.set("err", 0);
//                        result.set("msg", "Add Banner Product successfully.");
//                    } else{
//                        result.set("err", -1);
//                        result.set("msg", "Add Banner Product fail.");
//                    }
//                } else{
//                    result.set("err", -1);
//                    result.set("msg", "Parameter invalid.");
//                }
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Parameter invalid.");
//            }
//        } catch (Exception e) {
//            logger.error("BannerHandler.addBannerProduct: " + e, e);
//        }
//    }
//    
//    private void changeBannerProductPhoto(HttpServletRequest req, HttpServletResponse resp, JsonObject result, Map<String, FileItem> mapFile, Map<String, String> params) throws IOException{
//        try {
//            if(params != null && !params.isEmpty() && mapFile != null && !mapFile.isEmpty()){
//                String siidp = params.containsKey("iidp") ? params.get("iidp") : "";
//                FileItem thumbphoto = mapFile.containsKey("cimg") ? mapFile.get("cimg") : null;
//                if(siidp != null && !siidp.isEmpty() && thumbphoto != null && thumbphoto.getSize() > 0){
//                    long id = MIdNoise.deNoiseLId(siidp);
//                    ProductBanner pb = (ProductBanner) BannerDAO.getInstance().getById(id);
//                    if(pb != null){
//                        // save image.
//                        long idThumb = MIdGenLongDAO.getInstance(PhotoCommon.bannerNewsPhotoIdGen).getNext();
//                        if(idThumb >= 0){
//                            TMPhoto tmp = new TMPhoto();
//                            tmp.setId(idThumb);
//                            String filename = thumbphoto.getName();
//                            tmp.setFilename(filename);
//                            tmp.setData(thumbphoto.get());
//                            String ext = FilenameUtils.getExtension(filename);
//                            String contentType = MIMETypeUtil.getInstance().getMIMETypeImage(ext);
//                            tmp.setContentType(contentType);
//                            int err = BannerNewsPhotoClient.getInstance().putMPhoto(tmp);
//                            if(err >= 0){
//                                PhotoCommon.SET_BANNER_NEWS_PHOTO.add(PhotoCommon.BANNER_NEWS_PHOTO_TABLE, idThumb);
//                                pb.setThumb(idThumb);
//                                int err1 = BannerDAO.getInstance().update(pb);
//                                if(err1 >= 0){
//                                    result.set("err", 0);
//                                    result.set("msg", "Change thumb photo Banner Product successfully.");
//                                    return;
//                                } else{
//                                    result.set("err", -1);
//                                    result.set("msg", "Change thumb photo Banner Product fail.");
//                                    return;
//                                }
//                            } else{
//                                result.set("err", -1);
//                                result.set("msg", "Error no insert thumb photo Banner Product.");
//                                return;
//                            }
//                        } else{
//                            result.set("err", -1);
//                            result.set("msg", "Error gen id thumb photo Banner Product.");
//                            return;
//                        }
//                    } else{
//                        result.set("err", -1);
//                        result.set("msg", "Banner Product not exist.");
//                        return;
//                    }
//                } else{
//                    result.set("err", -1);
//                    result.set("msg", "Parameter invalid.");
//                }
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Parameter invalid.");
//            }
//        } catch (Exception e) {
//            logger.error("BannerHandler.changeBannerProductPhoto: " + e, e);
//        }
//    }
//    
//    private void getBannerProduct(HttpServletRequest req, HttpServletResponse resp, JsonObject result) throws UnsupportedEncodingException{
//        String sid = req.getParameter("id");
//        if(sid != null && !sid.isEmpty()){
//            long id = MIdNoise.deNoiseLId(sid);
//            ProductBanner pb = (ProductBanner) BannerDAO.getInstance().getById(id);
//            if(pb != null){
//                JsonObject opb = new JsonObject();
//                opb.set("id", sid);
//                opb.set("pid", MIdNoise.enNoiseLId(pb.getProductId()));
//                String namePro = "";
//                Product p = ProductDAO.getInstance().getById(pb.getProductId());
//                if(p != null){
//                    namePro = p.getModel();
//                }
//                opb.set("namePro", namePro);
//                opb.set("sid", MIdNoise.enNoiseLId(pb.getSkuId()));
//                String nameSku = "";
//                SKU sku = SKUDAO.getInstance().getById(pb.getSkuId());
//                if(sku != null){
//                    nameSku = sku.getName();
//                }
//                opb.set("nameSku", nameSku);
//                opb.set("msg", pb.getMsg());
//                String status = "";
//                if(pb.getStatus() == 0){
//                    status = "off";
//                } else{
//                    status = "on";
//                }
//                opb.set("status", status);
//                opb.set("idstatus", pb.getStatus());
//                
//                result.set("err", 0);
//                result.set("opb", opb);
//                result.set("msg", "Get Banner Product successfully.");
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Get Banner Product fail.");
//            }
//        } else{
//            result.set("err", -1);
//            result.set("msg", "Parameter invaliad.");
//        }
//    }
//    
//    private void editBannerProduct(HttpServletRequest req, HttpServletResponse resp, JsonObject result) throws UnsupportedEncodingException{
//        String spid = req.getParameter("epid");
//        String ssid = req.getParameter("esid");
//        String spbid = req.getParameter("eidp");
//        String semsg = req.getParameter("emsg");
//        String sestatus = req.getParameter("estatus");
//        if(spid != null && !spid.isEmpty() && ssid != null && !ssid.isEmpty()
//                && spbid != null && !spbid.isEmpty() && semsg != null && !semsg.isEmpty() && sestatus != null && !sestatus.isEmpty() ){
//            long id = MIdNoise.deNoiseLId(spbid);
//            ProductBanner pb = (ProductBanner) BannerDAO.getInstance().getById(id);
//            if(pb != null){
//                int status = Integer.valueOf(sestatus);
//                long pid = MIdNoise.deNoiseLId(spid);
//                long sid = MIdNoise.deNoiseLId(ssid);
//                pb.setProductId(pid);
//                pb.setSkuId(sid);
//                pb.setMsg(semsg);
//                pb.setStatus(status);
//
//                System.out.println(pb.toString());
//                int err = BannerDAO.getInstance().update(pb);
//                if(err >= 0){
//                    result.set("err", 0);
//                    result.set("msg", "Edit Banner Product successfully.");
//                } else{
//                    result.set("err", -1);
//                    result.set("msg", "Edit Banner Product fail.");
//                }
//            } else{
//                result.set("err", -1);
//                result.set("msg", "Banner Product not exist.");
//            }
//        } else{
//            result.set("err", -1);
//            result.set("msg", "Parameter invaliad.");
//        }
//    }
//    
//    
//    
//}
