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

import hapax.Template;
import hapax.TemplateCache;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dreamcolor.admin.common.Configuration;

/**
 *
 * @author nghiatc
 * @since Dec 25, 2015
 */
public class LogoutHandler extends HttpServlet{
    private static Logger logger = LoggerFactory.getLogger(LogoutHandler.class);
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp){
        try {
            deleteLoginSession(req, resp);
            renderFormLogin(req, resp, null);
        } catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
    }
    
    public static HttpSession deleteLoginSession(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession session = req.getSession();
        session.removeAttribute("loginss");
        //delete cookie.
//        Cookie sessionCookie = new Cookie("barssk", "");
//        sessionCookie.setMaxAge(0);
//        resp.addCookie(sessionCookie);
        return session;
	}
    
    public static HttpSession deleteSessionAdmin(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession session = req.getSession();
        session.removeAttribute("accountInfo");
        session.removeAttribute("adminInfo");
        //delete cookie.
        Cookie sessionCookie = new Cookie("admssk", "");
        sessionCookie.setMaxAge(0);
        resp.addCookie(sessionCookie);
        return session;
	}
    
    protected TemplateDataDictionary getDictionary() {
		TemplateDataDictionary dic = TemplateDictionary.create();
		return dic;
	}
    
    protected void print(Object obj, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Connection", "Close");
			//response.setHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
            response.setStatus(HttpServletResponse.SC_OK);
            
			out = response.getWriter();
			out.print(obj);
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
    
    private void renderFormLogin(HttpServletRequest req, HttpServletResponse resp, String reloadurl) throws TemplateException{
        TemplateDataDictionary dic = getDictionary();

        dic.setVariable("domain", Configuration.APP_DOMAIN);
        dic.setVariable("domain-home", Configuration.APP_DOMAIN_HOME);
        dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
        dic.setVariable("main_title", Configuration.APP_TITLE);
        reloadurl = (reloadurl != null && !reloadurl.isEmpty()) ? reloadurl : Configuration.APP_DOMAIN_HOME;
        dic.setVariable("RELOAD_URL", reloadurl);
        resp.setHeader("X-FRAME-OPTIONS", "SAMEORIGIN");
        print(applyTemplate(dic, "login.xtm"), resp);
    }
    
    protected String applyTemplate(TemplateDataDictionary dic, String tplName) throws TemplateException {
		TemplateLoader templateLoader = TemplateCache.create("./views");
		Template template = templateLoader.getTemplate(tplName);
        
        dic.setVariable("Project-Title", Configuration.APP_TITLE);
        dic.setVariable("domain", Configuration.APP_DOMAIN);
        dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
        
		return template.renderToString(dic);
	}
}
