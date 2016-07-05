package com.mit.upload.http.app;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.mit.jetty.server.WebServers;
import com.mit.upload.http.handler.NoFunctionHandler;
import com.mit.utils.ConfigUtils;


public class MainApp {

    public static void main(String[] args) {
		int port = ConfigUtils.getConfig().getInt("webserver.port");
        System.out.println("Web config port " + port);
		WebServers server = new WebServers("webserver");

		ServletHandler handler = new ServletHandler();
		addServlets(handler);

		List<HandlerWrapper> handlerList = new ArrayList<HandlerWrapper>();
		GzipHandler gzipHandler = new GzipHandler();
		handlerList.add(gzipHandler);
		handlerList.add(new MultiPartValidateHandler());
		handlerList.add(handler);
		handlerList.add(new NoFunctionHandler());

		server.setup(buildWrapper(handlerList));

		try {
            System.out.println(server.getInfo());
			if (!server.start()) {
                System.err.println("Could not start http servers! Exit now.");
                System.exit(1);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	public static HandlerWrapper buildWrapper(List<HandlerWrapper> handlerList) {
		for(int i = 1; i < handlerList.size(); i++) {
			handlerList.get(i-1).setHandler(handlerList.get(i));
		}
		return handlerList.get(0);
	}
	
	public static void addServlets(ServletHandler handler) {
//		handler.addServletWithMapping(UploadUserAvatarServlet.class, "/cyog/upload/user/avt");
//		handler.addServletWithMapping(UploadBizAvatarServlet.class, "/cyog/upload/biz/avt");
//		handler.addServletWithMapping(UploadBizCoverServlet.class, "/cyog/upload/biz/cov");
//		handler.addServletWithMapping(UploadTemplateVertServlet.class, "/cyog/upload/tml/v");
//		handler.addServletWithMapping(UploadTemplateHorzServlet.class, "/cyog/upload/tml/h");
//        //mmedia
//        handler.addServletWithMapping(UploadMMediaServlet.class, "/cyog/upload/media");
        //mphoto
//        handler.addServletWithMapping(UploadMPhotoServlet.class, "/cyog/upload/photo");
	}
}
