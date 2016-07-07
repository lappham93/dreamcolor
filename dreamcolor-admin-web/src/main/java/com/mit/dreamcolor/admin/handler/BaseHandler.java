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
import com.mit.dreamcolor.admin.common.Common;
import com.mit.dreamcolor.admin.common.Configuration;
import com.mit.dreamcolor.admin.entities.LoginSession;
import com.mit.dreamcolor.admin.entities.ROLE;

import hapax.Template;
import hapax.TemplateCache;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoader;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Dec 18, 2015
 */
public class BaseHandler extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(BaseHandler.class);
    
    public static final TemplateLoader Loader = TemplateCache.create("./views");
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp){
        try {
            //Nếu truy suất vào url public thì không cần authentication.
            String subPath = req.getServletPath();
            if(!Common.publicUrl.contains(subPath)){
                // Kiểm tra login.
                if(!checkSession(req, resp)){
                    resp.sendRedirect(Configuration.APP_DOMAIN + "/web/admin/login");
                    return;
                }
                //Nếu đã login và không phải là Admin thì check quyền truy suất url.
                if(!isAdmin(req)){
                    renderDenyPage(req, resp);
                    return;
//                    //Nếu không truy suất vào các url đc phép thì render page deny.
//                    if(!Common.saleAccessUrl.contains(subPath)){
//                        renderDenyPage(req, resp);
//                        return;
//                    }
                }
            }
            
            /*
             * forward resquest
             */
            if ("GET".equals(req.getMethod())) {
                this.doGet(req, resp);
            } else if ("POST".equals(req.getMethod())) {
                this.doPost(req, resp);
            }
        } catch (Exception ex) {
            logger.error("BaseHandler: " + ex);
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
    
    public boolean isAdmin(HttpServletRequest req){
        LoginSession lgss = (LoginSession) req.getSession().getAttribute("loginss");
        if(lgss != null && lgss.isIsLogin() && lgss.getRole() >= 0 && lgss.getUserId() >= 0){
            return lgss.isIsLogin() && lgss.getRole() == ROLE.ADMIN.getValue();
        }
        return false;
    }
    
    protected void print(Object obj, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Connection", "Close");
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
    
    protected void printJS(Object obj, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Connection", "Close");
            response.setStatus(HttpServletResponse.SC_OK);
            out = response.getWriter();
            out.println("<script type=\"text/javascript\">");
            out.print(obj);
            out.println("</script>");
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    protected void printJSON(JsonObject json, HttpServletResponse resp) {
        PrintWriter out = null;
        try {
            resp.setContentType("application/json;charset=UTF-8");
            resp.setHeader("Connection", "Close");
            resp.setStatus(HttpServletResponse.SC_OK);
            out = resp.getWriter();
            out.print(json.toString());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    protected void printStrJSON(Object json, HttpServletResponse resp) {
		PrintWriter out = null;
		try {
			resp.setContentType("application/json;charset=UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
			out = resp.getWriter();
			out.print(json);
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

    protected void printXML(Object obj, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setContentType("text/xml;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Connection", "Close");
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

    protected void printText(String str, HttpServletResponse resp) {
        PrintWriter out = null;
        try {
            resp.setContentType("text/plain; charset=utf-8");
            resp.setHeader("Connection", "Close");
            resp.setStatus(HttpServletResponse.SC_OK);
            out = resp.getWriter();
            out.println(str);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    protected void printFile(HttpServletResponse response, ByteArrayOutputStream bigfile, String fileName, String mimeType) throws IOException {
        ServletOutputStream stream = null;
        BufferedInputStream buf = null;
        try {
            stream = response.getOutputStream();

            //set response headers
            response.setBufferSize(1024 * 1024 * 6); // 6M
            response.setContentType(mimeType + ";charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setHeader("Content-Length", String.valueOf(bigfile.size()));
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            // the contentlength
            response.setContentLength(bigfile.size());

            InputStream input = new ByteArrayInputStream(bigfile.toByteArray());
            buf = new BufferedInputStream(input);

            //read from the file; write to the ServletOutputStream
            byte[] bb = new byte[1024 * 1024 * 5]; // 5M
            int readByte;
            while ((readByte = buf.read(bb, 0, bb.length)) != -1) {
                stream.write(bb, 0, readByte);
            }
            //System.out.println("Complete send file media. Done!!!...");
        } catch (Exception e) {
            logger.error("printFile: ", e);
        } finally {
            if (stream != null) {
                stream.flush();
                stream.close();
            }
            if (buf != null) {
                buf.close();
            }
            if(bigfile != null){
                bigfile.close();
            }
        }
    }
    
    protected String getClientIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
    
    protected TemplateDataDictionary getDictionary() {
        TemplateDataDictionary dic = TemplateDictionary.create();
        return dic;
    }
    
    protected String getContentTemplate(String tplName, HttpServletRequest req){
        try {
            TemplateDataDictionary dic = getDictionary();
            Template template = Loader.getTemplate(tplName);
            
            dic.setVariable("domain", Configuration.APP_DOMAIN);
            dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
            
            if (template != null) {
                return template.renderToString(dic);
            }
            
        } catch (TemplateException ex) {
            logger.error("getContentTemplate: " + ex);
        }
        return "";
    }

    protected String applyTemplate(TemplateDataDictionary dic, String tplName, HttpServletRequest req){
        try {
            Template template = Loader.getTemplate(tplName);
            dic.setVariable("domain", Configuration.APP_DOMAIN);
            dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
            
//            AdminAccount account = (AdminAccount) req.getSession().getAttribute("accountInfo");
//            if(account != null && account.getId() > 0){
//                dic.setVariable("NAME_ADMIN", "Admin");
//                dic.setVariable("AVATAR_ADMIN", Configuration.AVATAR_DEFAULT);
//                dic.addSection("ISLOGIN");
//            } else{
//                dic.setVariable("NAME_ADMIN", "Guest");
//                dic.setVariable("AVATAR_ADMIN", Configuration.AVATAR_DEFAULT);
//                dic.addSection("NOLOGIN");
//            }
            
            if (template != null) {
                return template.renderToString(dic);
            }
            
        } catch (TemplateException ex) {
            logger.error("applyTemplate: " + ex);
        }
        return "";
    }
    
    protected String applyTemplateLayoutMain(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp){
        try {
            Template template = Loader.getTemplate("layout.xtm");
            dic.setVariable("Project-Title", Configuration.APP_TITLE);
            dic.setVariable("domain", Configuration.APP_DOMAIN);
            dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
            
//            AdminAccount account = (AdminAccount) req.getSession().getAttribute("accountInfo");
//            if(account != null && account.getId() > 0){
//                dic.setVariable("NAME_ADMIN", "Admin");
//                dic.setVariable("AVATAR_ADMIN", Configuration.AVATAR_DEFAULT);
//                dic.addSection("ISLOGIN");
//            } else{
//                dic.setVariable("NAME_ADMIN", "Guest");
//                dic.setVariable("AVATAR_ADMIN", Configuration.AVATAR_DEFAULT);
//                dic.addSection("NOLOGIN");
//            }
            
            String svl = req.getServletPath();
            if("/web/admin".equalsIgnoreCase(svl)){
                dic.setVariable("MN_HOME", "active");
                dic.addSection("BC_HOME");
            } else if("/web/admin/home".equalsIgnoreCase(svl)){
                dic.setVariable("MN_HOME", "active");
                dic.addSection("BC_HOME");
            } else if("/web/admin/product".equalsIgnoreCase(svl)){
                dic.setVariable("MN_PRODUCT", "active");
                dic.addSection("BC_PRODUCT");
            } else if("/web/admin/color".equalsIgnoreCase(svl)){
                dic.setVariable("MN_COLOR", "active");
                dic.addSection("BC_COLOR");
            } else if("/web/admin/banner".equalsIgnoreCase(svl)){
                dic.setVariable("MN_BANNER", "active");
                dic.addSection("BC_BANNER");
            } else if("/web/admin/video".equalsIgnoreCase(svl)){
                dic.setVariable("MN_VIDEO", "active");
                dic.addSection("BC_VIDEO");
            } else if("/web/admin/distributor".equalsIgnoreCase(svl)){
                dic.setVariable("MN_DISTRIBUTOR", "active");
                dic.addSection("BC_DISTRIBUTOR");
            } else{
                dic.setVariable("MN_HOME", "active");
                dic.addSection("BC_HOME");
            }
            
            if (template != null) {
                return template.renderToString(dic);
            }
        } catch (TemplateException ex) {
            logger.error("applyTemplateLayoutMain: " + ex);
        }
        return "";
    }
    
    protected String applyTemplateLayoutEmail(TemplateDataDictionary dic, HttpServletRequest req, HttpServletResponse resp){
        try {
            Template template = Loader.getTemplate("layoutEmail.xtm");
            dic.setVariable("Project-Title", Configuration.APP_TITLE);
            dic.setVariable("domain", Configuration.APP_DOMAIN);
            dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
            
            if (template != null) {
                return template.renderToString(dic);
            }
        } catch (TemplateException ex) {
            logger.error("applyTemplateLayoutEmail: " + ex);
        }
        return "";
    }
    
    protected String applyTemplateLayoutEmail2(TemplateDataDictionary dic){
        try {
            Template template = Loader.getTemplate("layoutEmail.xtm");
            dic.setVariable("Project-Title", Configuration.APP_TITLE);
            dic.setVariable("domain", Configuration.APP_DOMAIN);
            dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
            
            if (template != null) {
                return template.renderToString(dic);
            }
        } catch (TemplateException ex) {
            logger.error("applyTemplateLayoutEmail: " + ex);
        }
        return "";
    }
    
    public void renderErrorPage(HttpServletRequest req, HttpServletResponse resp){
        try {
            TemplateDataDictionary dic = getDictionary();

            dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "error500.xtm", req));
            print(applyTemplateLayoutMain(dic, req, resp), resp);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    public void renderPageNotFound(HttpServletRequest req, HttpServletResponse resp){
        try {
            TemplateDataDictionary dic = getDictionary();

            dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "error404.xtm", req));
            print(applyTemplateLayoutMain(dic, req, resp), resp);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    public void renderDenyPage(HttpServletRequest req, HttpServletResponse resp){
        try {
            TemplateDataDictionary dic = getDictionary();
            TemplateLoader templateLoader = TemplateCache.create("./views");
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
    
    protected String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
    
    
    
}
