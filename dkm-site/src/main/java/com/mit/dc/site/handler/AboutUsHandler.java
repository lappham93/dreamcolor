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

import com.eclipsesource.json.JsonObject;
import com.mit.dc.site.utils.HttpHelper;
import com.mit.dc.site.utils.UploadFormUtil;
import hapax.TemplateDataDictionary;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Jul 11, 2016
 */
public class AboutUsHandler extends BaseHandler {
    private static Logger logger = LoggerFactory.getLogger(AboutUsHandler.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            TemplateDataDictionary dic = getDictionary();
        	
        	dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "aboutus.xtm", req));
        	
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
//                    if("getState".equalsIgnoreCase(action)){
//                        getStateOfCountry(req, resp, result);
//                    } else if("getbiz".equalsIgnoreCase(action)){
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
}
