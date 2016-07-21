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

import com.eclipsesource.json.JsonObject;
import com.mit.dreamcolor.admin.common.Configuration;
import com.mit.dreamcolor.admin.entities.LoginSession;
import com.mit.dreamcolor.admin.entities.ROLE;

import hapax.Template;
import hapax.TemplateCache;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Dec 23, 2015
 */
public class LoginHandler extends HttpServlet{
    private static Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp){
        try {
            TemplateDataDictionary dic = getDictionary();
            JsonObject result = new JsonObject();
            result.set("err", -1);
            result.set("msg", "Execute fail. Please try again.");

			String username = req.getParameter("username");
			String password = req.getParameter("password");
			//boolean autoLogin = BooleanUtils.toBoolean(req.getParameter("rememberLogin"));

            String callback = req.getParameter("callfunc");
            String reloadurl = req.getParameter("reloadurl");
            reloadurl = Configuration.APP_DOMAIN_HOME;
//            if(checkSession(req, resp)){
//                resp.sendRedirect(Configuration.APP_DOMAIN_HOME);
//                return;
//            }
            if(username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                if(checkIsAdmin(req, resp, username, password)){
                    result.set("err", 0);
                    result.set("msg", "Login successfull.");
                    result.set("reloadurl", reloadurl);
                } else{
                    result.set("err", -1);
                    result.set("msg", "Infomation login is not correct. Try again!!!");
                }
            } else {
                if(password != null && !password.isEmpty()) {
                    result.set("err", -1);
                    result.set("msg", "Infomation login is not correct. Try again!!!");
                } else{
                    renderFormLogin(req, resp, reloadurl);
                    return;
                }
            }
            
            dic.setVariable("callback", callback);
            dic.setVariable("data", result.toString());
            print(applyTemplate(dic, "iframe_callback.xtm"), resp);
        } catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
    }
    
    public boolean checkSession(HttpServletRequest req, HttpServletResponse resp){
        LoginSession lgss = (LoginSession) req.getSession().getAttribute("loginss");
        //System.out.println("userSession: " + userSession);
        if(lgss != null && lgss.isIsLogin() && lgss.getRole() >= 0 && lgss.getUserId() >= 0){
            //System.out.println("LoginSession: " + lgss);
            return true;
        }
        return false;
    }
    
    protected boolean checkIsAdmin(HttpServletRequest req, HttpServletResponse resp, String username, String password){
        if(Configuration.AD_USERNAME.equals(username) && Configuration.AD_PASSWORD.equals(password)){
            //create session.
            LoginSession lgss = new LoginSession(true, ROLE.ADMIN.getValue(), Configuration.AD_USERNAME, 0);
            createSessionLoginSS(req, resp, lgss);
            return true;
        }
        return false;
    }
    
    public HttpSession createSessionLoginSS(HttpServletRequest req, HttpServletResponse resp, LoginSession lgss) {
		HttpSession session = req.getSession();
        session.setAttribute("loginss", lgss);
        //setting session to expiry in 24h.
        session.setMaxInactiveInterval(24*60*60);
//        Cookie sessionCookie = new Cookie("barssk", info.getSessionKey());
//        sessionCookie.setMaxAge(24*60*60);
//        resp.addCookie(sessionCookie);
        return session;
	}
    
    private void renderFormLogin(HttpServletRequest req, HttpServletResponse resp, String reloadurl) throws TemplateException{
        TemplateDataDictionary dic = getDictionary();

        dic.setVariable("domain", Configuration.APP_DOMAIN);
        dic.setVariable("domain-home", Configuration.APP_DOMAIN_HOME);
        dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
        dic.setVariable("main_title", Configuration.APP_TITLE);
        reloadurl = (reloadurl != null && !reloadurl.isEmpty()) ? reloadurl : Configuration.APP_DOMAIN_HOME;
        dic.setVariable("RELOAD_URL", reloadurl);
        dic.setVariable("domain-home", reloadurl);
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
    
    public void renderDenyPage(HttpServletRequest req, HttpServletResponse resp){
        try {
            TemplateDataDictionary dic = getDictionary();
            TemplateLoader templateLoader = TemplateCache.create("./views"); //TemplateResourceLoader.create("./views");
            Template template = templateLoader.getTemplate("deny.xtm");
            
            dic.setVariable("Project-Title", Configuration.APP_TITLE);
            dic.setVariable("domain", Configuration.APP_DOMAIN);
            dic.setVariable("domain-home", Configuration.APP_DOMAIN_HOME);
            dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
            
            String content = "";
            if (template != null) {
                content = template.renderToString(dic);
            }
            print(content, resp);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
//    public static HttpSession createSessionAdmin(HttpServletRequest req, HttpServletResponse resp, AdminAccount account, AdminInfo info) {
//        String sessionKey = Base64.encodeBase64String(DigestUtils.sha256(info.toString() + System.nanoTime()));
//		HttpSession session = req.getSession();
//        session.setAttribute("accountInfo", account);
//        session.setAttribute("adminInfo", info);
//        //setting session to expiry in 30 mins
//        session.setMaxInactiveInterval(24*60*60);
//        Cookie sessionCookie = new Cookie("admssk", sessionKey);
//        sessionCookie.setMaxAge(24*60*60);
//        resp.addCookie(sessionCookie);
//        return session;
//	}
    
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
}
