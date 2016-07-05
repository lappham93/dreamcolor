/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.file.handler;

import hapax.Template;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.html.HapaxTemplate;

/**
 *
 * @author nghiatc
 * @since Sep 7, 2015
 */
public class FAQHandler extends HandlerWrapper {
    private final Logger _logger = LoggerFactory.getLogger(DownloadAppHandler.class);
    
    @Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String uri = "/loop/app/faq";
		if (target.startsWith(uri)) {
            try {
                TemplateDataDictionary template = TemplateDictionary.create();
                template.addSection("CONTENT");
                Template tmp = HapaxTemplate.Loader.getTemplate("app/faq.xtm");
                String html = tmp.renderToString(template);
                printHtml(request, response, html);
            } catch (Exception e) {
				_logger.error("DownloadAppHandler ", e);
				response.setStatus(HttpStatus.FORBIDDEN_403);
			}
		}
	}

	public void printHtml(HttpServletRequest req, ServletResponse resp, String htmlString) throws IOException {
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter writer = resp.getWriter();
		writer.write(htmlString);
		writer.close();
		_logger.debug("\tEND_REQUEST\t" + req.getMethod() + "\t"
				+ req.getContextPath() + "\t" + htmlString);
	}
}
