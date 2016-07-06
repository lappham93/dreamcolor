package com.mit.dreamcolor.app;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.banner.servlet.GetListBannerServlet;
import com.mit.color.servlet.GetListColorCategoryServlet;
import com.mit.color.servlet.GetListColorServlet;
import com.mit.color.servlet.ViewColorServlet;
import com.mit.distributor.servlet.GetListDistributorServlet;
import com.mit.handler.NoFunctionHandler;
import com.mit.handler.ValidateHandler;
import com.mit.jetty.server.WebServers;
import com.mit.product.servlet.GetListProductServlet;
import com.mit.product.servlet.GetProductDetailServlet;
import com.mit.product.servlet.ViewProductServlet;
import com.mit.utils.ConfigUtils;
import com.mit.video.servlet.GetListVideoServlet;
import com.mit.video.servlet.ViewVideoServlet;

public class MainApp {

	public static void main(String[] args) {
		Logger _logger = LoggerFactory.getLogger(MainApp.class);
		int port = ConfigUtils.getConfig().getInt("webserver.port");
		System.out.println("Web config port " + port);
		_logger.debug("Web config port " + port);
		WebServers server = new WebServers("webserver");

		ServletHandler handler = new ServletHandler();
		addServlets(handler);

		List<HandlerWrapper> handlerList = new ArrayList<HandlerWrapper>();
		GzipHandler gzipHandler = new GzipHandler();
		handlerList.add(gzipHandler);
		handlerList.add(new ValidateHandler());
		handlerList.add(handler);
		handlerList.add(new NoFunctionHandler());

		server.setup(buildWrapper(handlerList));

		try {
			System.out.println(server.getInfo());
			if (!server.start()) {
                System.err.println("Could not start http servers! Exit now.");
                _logger.error("Could not start http servers! Exit now.");
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
		//color
		handler.addServletWithMapping(GetListColorCategoryServlet.class, "/dreaMau/color/category/list");
		handler.addServletWithMapping(GetListColorServlet.class, "/dreaMau/color/list");
		handler.addServletWithMapping(ViewColorServlet.class, "/dreaMau/color/view");
		
		//product
		handler.addServletWithMapping(GetListProductServlet.class, "/dreaMau/product/list");
		handler.addServletWithMapping(GetProductDetailServlet.class, "/dreaMau/product/detail");
		handler.addServletWithMapping(ViewProductServlet.class, "/dreaMau/product/view");
		
		//distributor
		handler.addServletWithMapping(GetListDistributorServlet.class, "/dreaMau/distributor/list");
		
		//banner
		handler.addServletWithMapping(GetListBannerServlet.class, "/dreaMau/banner/list");
		
		//video
		handler.addServletWithMapping(GetListVideoServlet.class, "/dreaMau/video/list");
		handler.addServletWithMapping(ViewVideoServlet.class, "dreaMau/video/view");
	}
}
