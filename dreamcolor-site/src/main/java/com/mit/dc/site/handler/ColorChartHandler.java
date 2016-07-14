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

package com.mit.dc.site.handler;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.mit.dao.color.CategoryDAO;
import com.mit.dao.color.ColorDAO;
import com.mit.dc.site.utils.HttpHelper;
import com.mit.dc.site.utils.PhotoUtil;
import com.mit.dc.site.utils.UploadFormUtil;
import com.mit.entities.color.Category;
import com.mit.entities.color.Color;
import com.mit.entities.photo.PhotoType;
import com.mit.midutil.MIdNoise;
import hapax.TemplateDataDictionary;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Jul 12, 2016
 */
public class ColorChartHandler extends BaseHandler {
    private static Logger logger = LoggerFactory.getLogger(ColorChartHandler.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            TemplateDataDictionary dic = getDictionary();
        	
            renderPageColorChart(dic, req, resp);
        	dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "colorchart.xtm", req));
        	
            print(applyTemplateLayoutMain(dic, req, resp), resp);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            JsonObject result = new JsonObject();
            result.set("err", -1);
            result.set("msg", "Execute fail. Please try again.");
            
            String action = "";
            String callback = "";
            if(HttpHelper.isMultipartRequest(req)){ // process request multipart form data.
                Map<String, FileItem> mapFile = new HashMap<String, FileItem>();
                Map<String, String> params = new HashMap<String, String>();
                UploadFormUtil.getInstance().getMapFormUpload(req, mapFile, params);
                if(params != null && !params.isEmpty()){
                    System.out.println("multipart params: " + params);
                    System.out.println("multipart mapFile: " + mapFile);
                    callback = params.containsKey("callback") ? params.get("callback") : "";
                    action = params.containsKey("action") ? params.get("action") : "";
                    if(action != null && !action.isEmpty()) {
//                        if("addbiz".equalsIgnoreCase(action)){
//                            addBiz(req, resp, result,mapFile, params);
//                        } else if("cavt".equalsIgnoreCase(action)){
//                            changeAvatarPhoto(req, resp, result, mapFile, params);
//                        } else if("ccov".equalsIgnoreCase(action)){
//                            changeCoverPhoto(req, resp, result, mapFile, params);
//                        }
                    }
                }
            } else{ // process request nomal.
                action = req.getParameter("action");
                callback = req.getParameter("callback");
                if(action != null && !action.isEmpty()) {
                    if("getColorOfCate".equalsIgnoreCase(action)){
                        getColorOfCate(req, resp, result);
                    } 
//                    else if("getbiz".equalsIgnoreCase(action)){
//                        getBiz(req, resp, result);
//                    } else if("editbiz".equalsIgnoreCase(action)){
//                        editBiz(req, resp, result);
//                    } else if("getbizmanager".equalsIgnoreCase(action)){
//                    	getBizManager(req, resp, result);
//                    } else if("addbm".equalsIgnoreCase(action)){
//                    	addBizManager(req, resp, result);
//                    } else if("delbm".equalsIgnoreCase(action)){
//                    	delBizManager(req, resp, result);
//                    }
                }
            }
            
            //render JsonObject.
            if(action != null && !action.isEmpty()) {
                if (HttpHelper.isAjaxRequest(req)) {
                    if (callback != null && !callback.isEmpty()) {
                        printStrJSON(callback + "(" + result.toString() + ")", resp);
                    } else {
                        printStrJSON(result.toString(), resp);
                    }
                } else {
                    TemplateDataDictionary dic = getDictionary();
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
    
    private void renderPageColorChart(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp){
        //get list category.
        List<Category> listCate = CategoryDAO.getInstance().getAll();
        if(listCate != null && !listCate.isEmpty()){
            int i = 0;
            for(Category cate : listCate){
                List<Color> listCL = ColorDAO.getInstance().getByCateId(cate.getId());
                if(listCL != null && !listCL.isEmpty()){
                    TemplateDataDictionary loopCT = dic.addSection("loop_ct");
                    if(i == 0){
                        loopCT.setVariable("DISPLAY", "");
                    } else{
                        loopCT.setVariable("DISPLAY", "display: none;");
                    }
                    loopCT.setVariable("ID", MIdNoise.enNoiseIId(cate.getId()));
                    
                    for(Color cl : listCL){
                        TemplateDataDictionary loopRow = loopCT.addSection("loop_color");
                        String uri = PhotoUtil.Instance.buildURIImg(cl.getPhoto(), PhotoType.COLOR);
                        loopRow.setVariable("URI_CL", uri);
                        loopRow.setVariable("CL_CODE", cl.getCode());
                        
                        TemplateDataDictionary loopAllCL = dic.addSection("loop_all_color");
                        String uriCL = PhotoUtil.Instance.buildURIImg(cl.getPhoto(), PhotoType.COLOR);
                        loopAllCL.setVariable("URI_CL", uriCL);
                    }
                    i++;
                }
            }
            int j = 0;
            for(Category cate : listCate){
                TemplateDataDictionary loopRow = dic.addSection("loop_cate");
                String uri = PhotoUtil.Instance.buildURIImg(cate.getPhotoNum(), PhotoType.COLOR);
				loopRow.setVariable("URI_CATE", uri);
                loopRow.setVariable("ID", MIdNoise.enNoiseIId(cate.getId()));
                if(j == 0){
                    loopRow.setVariable("CATE_ACTIVE", "border-color");
                }
                j++;
            }
        }
    }
    
    private void getColorOfCate(HttpServletRequest req, HttpServletResponse resp, JsonObject result) {
		String sid = req.getParameter("id");
		if (sid != null && !sid.isEmpty()) {
			int id = MIdNoise.deNoiseIId(sid);
            
            JsonArray jlistCL = new JsonArray();
            List<Color> listCL = ColorDAO.getInstance().getByCateId(id);
            if(listCL != null && !listCL.isEmpty()){
                for(Color cl : listCL){
                    JsonObject objCL = new JsonObject();
                    String uri = PhotoUtil.Instance.buildURIImg(cl.getPhoto(), PhotoType.COLOR);
                    objCL.set("uri", uri);
                    objCL.set("code", cl.getCode());
                    jlistCL.add(objCL);
                }
            }
            result.set("err", 0);
            result.set("arrCL", jlistCL);
            result.set("msg", "Get category successfully.");
		} else {
			result.set("err", -1);
			result.set("msg", "Parameter invaliad.");
		}
	}
}
