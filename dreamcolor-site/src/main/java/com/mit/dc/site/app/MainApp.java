/*
 * Copyright 2016 nghiatc.
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

package com.mit.dc.site.app;

import com.mit.configer.MConfig;
import com.mit.dc.site.handler.HomeHandler;
import com.mit.jetty.server.WebServers;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 *
 * @author nghiatc
 * @since Jun 21, 2016
 */
public class MainApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int port = MConfig.getConfig().getInt("webserver.port");
        System.out.println("Web config port " + port);
        WebServers server = new WebServers("webserver");

		ServletHandler handler = new ServletHandler();

        //MAPPING ==============================================================
        //Web
        handler.addServletWithMapping(HomeHandler.class, "/");
        
        
        //MAPPING ==============================================================
        
        ServletContextHandler h = new ServletContextHandler(ServletContextHandler.SESSIONS);
		h.setServletHandler(handler);
        
        //static file
        ContextHandler resource_handler = new ContextHandler("/web/site/static");
        resource_handler.setResourceBase("./public/");
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
