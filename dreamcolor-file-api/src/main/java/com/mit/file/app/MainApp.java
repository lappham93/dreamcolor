package com.mit.file.app;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.mit.file.servlet.LoadBannerPhotoServlet;
import com.mit.file.servlet.LoadColorPhotoServlet;
import com.mit.file.servlet.LoadProductPhotoServlet;
import com.mit.file.servlet.LoadVideoThumbPhotoServlet;
import com.mit.jetty.server.WebServers;
import com.mit.utils.ConfigUtils;

public class MainApp {

    public static void main(String[] args) {
		int port = ConfigUtils.getConfig().getInt("webserver.port");
        System.out.println("Web config port " + port);
		WebServers server = new WebServers("webserver");

        ServletHandler handler = new ServletHandler();

        //MAPPING ==============================================================/loop/img/promo/share/
        //Web
        handler.addServletWithMapping(LoadColorPhotoServlet.class, "/dreamau/load/color/photo");
        handler.addServletWithMapping(LoadBannerPhotoServlet.class, "/dreamau/load/bn/photo");
        handler.addServletWithMapping(LoadProductPhotoServlet.class, "/dreamau/load/pro/photo");
        handler.addServletWithMapping(LoadVideoThumbPhotoServlet.class, "/dreamau/load/video/photo");
        
        //MAPPING ==============================================================
        
        ServletContextHandler h = new ServletContextHandler(ServletContextHandler.SESSIONS);
		h.setServletHandler(handler);
        
        //static file
        ContextHandler resource_handler = new ContextHandler("/dreamau/load/static");
        resource_handler.setResourceBase("./public/file/");
        resource_handler.setHandler(new ResourceHandler());
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, h});
        
		server.setup(handlers);

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
    
}
