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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.html.HapaxTemplate;
import com.mit.redis.user.BizActiveCodeRedis;
import com.mit.utils.Base64UrlEncode;
import com.mit.utils.IDCipher;

public class VXMLHandler extends HandlerWrapper {
	private final Logger _logger = LoggerFactory
			.getLogger(VXMLHandler.class);

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String uri = "/loop/img/vxml/";
		if (target.startsWith(uri)) {
			String[] targetSplit = target.replace(uri, "").split("/");

			String hash = null;
			if (targetSplit.length > 0) {
				hash = targetSplit[0];
			}

			try {
				if (hash != null && !hash.trim().isEmpty()) {
					String refId = IDCipher.decryptString(Base64UrlEncode.getInstance().decode(hash));
					if (!refId.isEmpty()) {
						TemplateDataDictionary template = TemplateDictionary
								.create();
						template.addSection("CONTENT");
						
						String activeCode = BizActiveCodeRedis.Instance.getActiveCode(refId);
						
						if (activeCode != null) {
							template.setVariable("CODE", StringUtils.join(activeCode.split(""), " "));
	
							Template tmp = HapaxTemplate.Loader
									.getTemplate("vxml/voice.xtm");
							String html = tmp.renderToString(template);
							printHtml(request, response, html);
						} else {
							response.setStatus(HttpStatus.FORBIDDEN_403);
						}
					} else {
						response.setStatus(HttpStatus.FORBIDDEN_403);
					}
				} else {
					response.setStatus(HttpStatus.FORBIDDEN_403);
				}
			} catch (Exception e) {
				_logger.error("promotion share ", e);
				response.setStatus(HttpStatus.FORBIDDEN_403);
			}

			baseRequest.setHandled(true);
		}
	}

	public void printHtml(HttpServletRequest req, ServletResponse resp,
			String htmlString) throws IOException {
		resp.setContentType("text/xml;charset=utf-8");
		PrintWriter writer = resp.getWriter();
		writer.write(htmlString);
		writer.close();
	}
}
