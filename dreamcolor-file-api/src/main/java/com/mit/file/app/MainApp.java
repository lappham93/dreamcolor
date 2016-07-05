package com.mit.file.app;

import com.mit.file.servlet.BannerNewsPhotoServlet;
import org.eclipse.jetty.server.handler.HandlerList;

import com.mit.file.servlet.ProductPhotoServlet;
import com.mit.file.servlet.ProductSkuServlet;
import com.mit.file.servlet.UserPhotoServlet;
import com.mit.jetty.server.WebServers;
import com.mit.utils.ConfigUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

public class MainApp {

    public static void main(String[] args) {
		int port = ConfigUtils.getConfig().getInt("webserver.port");
        System.out.println("Web config port " + port);
		WebServers server = new WebServers("webserver");

//		HandlerList handlers = new HandlerList();
//        handlers.addHandler(new ProductPhotoHandler());
//        handlers.addHandler(new UserPhotoHandler());
////		handlers.addHandler(new AvatarHandler());
////		handlers.addHandler(new CoverHandler());
////		handlers.addHandler(new ChatPhotoHandler());
////		handlers.addHandler(new QRHandler());
////		handlers.addHandler(new TemplateHandler());
////		handlers.addHandler(new PromotionHandler());
////		handlers.addHandler(new PromoVTemplateHandler());
////		handlers.addHandler(new PromoHTemplateHandler());
////		handlers.addHandler(new VXMLHandler());
////        handlers.addHandler(new LoadMMediaHandler());
////        handlers.addHandler(new LoadMPhotoHandler());
////        handlers.addHandler(new DownloadAppHandler());
////        handlers.addHandler(new FAQHandler());
//		handlers.addHandler(new ErrorHandler());

        ServletHandler handler = new ServletHandler();

        //MAPPING ==============================================================/loop/img/promo/share/
        //Web
        handler.addServletWithMapping(ProductPhotoServlet.class, "/cyog/load/pro/photo");
        handler.addServletWithMapping(UserPhotoServlet.class, "/cyog/load/user/photo");
        handler.addServletWithMapping(ProductSkuServlet.class, "/cyog/load/product/share");
        handler.addServletWithMapping(BannerNewsPhotoServlet.class, "/cyog/load/bn/photo");
        
        //MAPPING ==============================================================
        
        ServletContextHandler h = new ServletContextHandler(ServletContextHandler.SESSIONS);
		h.setServletHandler(handler);
        
        //static file
        ContextHandler resource_handler = new ContextHandler("/cyog/load/static");
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
