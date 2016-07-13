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

package com.mit.dreamcolor.admin.handler;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.JsonObject;
import com.mit.dao.color.ColorDAO;
import com.mit.dreamcolor.admin.utils.HttpHelper;
import com.mit.dreamcolor.admin.utils.PhotoUtil;
import com.mit.entities.color.Color;
import com.mit.entities.photo.PhotoType;

import hapax.TemplateDataDictionary;

/**
 *
 * @author nghiatc
 * @since Dec 18, 2015
 */
public class HomeHandler extends BaseHandler {
    private static Logger logger = LoggerFactory.getLogger(HomeHandler.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            TemplateDataDictionary dic = getDictionary();
            
            renderHome(dic, req, resp);
            
            dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "home.xtm", req));
            print(applyTemplateLayoutMain(dic, req, resp), resp);
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
    
    private void renderHome(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp){
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.DAY_OF_YEAR, start.get(Calendar.DAY_OF_YEAR)-2);
        
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MINUTE, 59);
        
        int totalColor = ColorDAO.getInstance().totalAll();
        int totalFeature = ColorDAO.getInstance().totalFeature();
        dic.setVariable("NUM_COLORS", String.valueOf(totalColor));
        dic.setVariable("NUM_FEATURES", String.valueOf(totalFeature));
        //top color accord view
        List<Color> colors = ColorDAO.getInstance().getSlice(5, 0, "views", false);
        for (Color color : colors) {
        	String uri = PhotoUtil.Instance.buildURIImg(color.getPhoto(), PhotoType.COLOR);
        	TemplateDataDictionary colorDic = dic.addSection("loopcolor");
        	colorDic.setVariable("URI_CP", uri);
        	colorDic.setVariable("CODE", color.getCode());
        	colorDic.setVariable("VIEW", String.valueOf(color.getViews()));
        }
        
    }
    
}
