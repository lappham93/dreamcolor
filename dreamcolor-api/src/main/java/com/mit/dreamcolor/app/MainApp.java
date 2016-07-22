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
import com.mit.color.servlet.GetListColorDetailsServlet;
import com.mit.color.servlet.GetListColorServlet;
import com.mit.color.servlet.GetListFeatureServlet;
import com.mit.color.servlet.GetListNewColorServlet;
import com.mit.color.servlet.SearchResultServlet;
import com.mit.color.servlet.ViewColorServlet;
import com.mit.color.servlet.WordSuggestServlet;
import com.mit.distributor.servlet.GetListDistributorServlet;
import com.mit.handler.NoFunctionHandler;
import com.mit.handler.ValidateHandler;
import com.mit.jetty.server.WebServers;
import com.mit.notification.servlet.GetNewsCountServlet;
import com.mit.notification.servlet.GetNewsServlet;
import com.mit.notification.servlet.ViewNewsServlet;
import com.mit.product.servlet.GetListProductServlet;
import com.mit.product.servlet.GetProductDetailServlet;
import com.mit.product.servlet.ViewProductServlet;
import com.mit.user.servlet.RemoveTokenServlet;
import com.mit.user.servlet.SendFeedbackServlet;
import com.mit.user.servlet.SubmitTokenServlet;
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
		handler.addServletWithMapping(GetListColorCategoryServlet.class, "/dreamau/color/category/list");
		handler.addServletWithMapping(GetListColorServlet.class, "/dreamau/color/list");
		handler.addServletWithMapping(GetListColorDetailsServlet.class, "/dreamau/color/get");
		handler.addServletWithMapping(ViewColorServlet.class, "/dreamau/color/view");
		handler.addServletWithMapping(GetListFeatureServlet.class, "/dreamau/feature/list");
		handler.addServletWithMapping(WordSuggestServlet.class, "/dreamau/search/suggest");
		handler.addServletWithMapping(SearchResultServlet.class, "/dreamau/search/result");
		handler.addServletWithMapping(GetListNewColorServlet.class, "/dreamau/color/new/list");
		
		//product
		handler.addServletWithMapping(GetListProductServlet.class, "/dreamau/product/list");
		handler.addServletWithMapping(GetProductDetailServlet.class, "/dreamau/product/detail");
		handler.addServletWithMapping(ViewProductServlet.class, "/dreamau/product/view");
		
		//distributor
		handler.addServletWithMapping(GetListDistributorServlet.class, "/dreamau/distributor/list");
		
		//banner
		handler.addServletWithMapping(GetListBannerServlet.class, "/dreamau/banner/list");
		
		//video
		handler.addServletWithMapping(GetListVideoServlet.class, "/dreamau/video/list");
		handler.addServletWithMapping(ViewVideoServlet.class, "/dreamau/video/view");
		
		//user
		handler.addServletWithMapping(SendFeedbackServlet.class, "/dreamau/user/feedback");
		handler.addServletWithMapping(SubmitTokenServlet.class, "/dreamau/user/token/submit");
		handler.addServletWithMapping(RemoveTokenServlet.class, "/dreamau/user/token/remove");
		
		//new
		handler.addServletWithMapping(GetNewsCountServlet.class, "/dreamau/news/count");
		handler.addServletWithMapping(GetNewsServlet.class, "/dreamau/news/list");
		handler.addServletWithMapping(ViewNewsServlet.class, "/dreamau/news/view");
	}
	
}
