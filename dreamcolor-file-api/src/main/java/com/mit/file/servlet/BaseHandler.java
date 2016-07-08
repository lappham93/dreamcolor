/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.file.servlet;

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

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.JsonObject;
import com.mit.dao.photo.BannerPhotoClient;
import com.mit.dao.photo.ColorPhotoClient;
import com.mit.dao.photo.ProductPhotoClient;
import com.mit.dao.photo.VideoThumbnailClient;
import com.mit.entities.photo.PhotoType;
import com.mit.file.common.Configuration;
import com.mit.file.utils.ImageResize;
import com.mit.midutil.MIdNoise;
import com.mit.mphoto.thrift.TMPhoto;
import com.mit.mphoto.thrift.TMPhotoResult;

import hapax.Template;
import hapax.TemplateCache;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoader;

/**
 *
 * @author nghiatc
 * @since Jan 26, 2016
 */
public class BaseHandler extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(BaseHandler.class);
    private final int minSize = 20;
    
    public static final TemplateLoader Loader = TemplateCache.create("./view");
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp){
        try {
            
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
    
    protected void renderPhoto(HttpServletRequest req, HttpServletResponse resp, int type) {
		try {
			long t = System.nanoTime();
			long fileSize = 0L;
			String p = req.getParameter("p");
			String ssize = req.getParameter("size");
			int size = 0;
			if (ssize != null && !ssize.isEmpty()) {
				size = Integer.valueOf(ssize);
			}
			if (p != null && !p.isEmpty()) {
				long pid = MIdNoise.deNoiseLId(p);
				TMPhotoResult tptrs = null;
				if (type == PhotoType.BANNER.getValue()) {
					tptrs = BannerPhotoClient.getInstance().getMPhoto(pid);
				} else if (type == PhotoType.COLOR.getValue()) {
					tptrs = ColorPhotoClient.getInstance().getMPhoto(pid);
				} else if (type == PhotoType.PRODUCT.getValue()) {
					tptrs = ProductPhotoClient.getInstance().getMPhoto(pid);
				} else if (type == PhotoType.VIDEO_THUMBNAIL.getValue()) {
					tptrs = VideoThumbnailClient.getInstance().getMPhoto(pid);
				}
				
				if (tptrs != null && tptrs.error >= 0 && tptrs.value != null) {
					TMPhoto tmp = tptrs.getValue();
					String fileName = tmp.getFilename();
					String mimeType = tmp.getContentType();
					byte[] data = tmp.getData();

					logger.info("FeedPhotoServlet load from rockdb - pid: " + p + " Size: " + size + " FileSize: "
							+ FileUtils.byteCountToDisplaySize(data.length));
					if (data != null && size > 0) {
						size = size > minSize ? size : minSize;
						ByteArrayOutputStream out = ImageResize.resizeImage(data, size);
						if (out != null) {
							data = out.toByteArray();
							out.close();
						}
					}
					fileSize = data.length;
					logger.info("FeedPhotoServlet after resizeImage - pid: " + p + " Size: " + size + " FileSize: "
							+ FileUtils.byteCountToDisplaySize(fileSize));
					ByteArrayOutputStream outData = new ByteArrayOutputStream(data.length);
					outData.write(data, 0, data.length);
					printBigFile(resp, outData, fileName, mimeType);
				} else {
					logger.info("FeedPhotoServlet load from rockdb - pid: " + p + " Size: " + size + " -------> NULL ");
					resp.setStatus(HttpStatus.NOT_FOUND_404);
					print("", resp);
				}
			} else {
				resp.setStatus(HttpStatus.BAD_REQUEST_400);
				print("", resp);
			}
			logger.info("FeedPhotoServlet - pid: " + p + " Size: " + size + " FileSize: "
					+ FileUtils.byteCountToDisplaySize(fileSize) + " Time: " + (System.nanoTime() - t) + "ns ");
		} catch (Exception e) {
			logger.error("renderFeedPhoto " + e.getMessage(), e);
		}
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
    
    protected void printBigFile(HttpServletResponse response, ByteArrayOutputStream bigfile, String fileName, String mimeType) throws IOException {
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
            response.setHeader("Accept-Ranges", "bytes");
            //response.setHeader("Content-Length", String.valueOf(bigfile.size()));
            //response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            // the contentlength
            response.setContentLength(bigfile.size());

            InputStream input = new ByteArrayInputStream(bigfile.toByteArray());
            buf = new BufferedInputStream(input);

            //read from the file; write to the ServletOutputStream
            byte[] bb = new byte[1024 * 1024 * 5]; // 5M
            int readByte;
            while ((readByte = buf.read(bb, 0, bb.length)) != -1) {
                stream.write(bb, 0, readByte);
                //System.out.println("Sending stream: " + readByte);
            }
            //System.out.println("Complete send file photo. Done!!!...");
        } catch (Exception e) {
            logger.error("printBigFile: ", e);
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
            
            if (template != null) {
                return template.renderToString(dic);
            }
        } catch (TemplateException ex) {
            logger.error("applyTemplateLayoutMain: " + ex);
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
