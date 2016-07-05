package com.mit.file.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.promotion.PromotionTemplateDAO;
import com.mit.entities.promotion.PromotionTemplate;
import com.mit.file.utils.SizeUtils;
import com.mit.utils.ConfigUtils;

public class TemplateHandler extends HandlerWrapper {
	private final Logger _logger = LoggerFactory.getLogger(TemplateHandler.class);
	
	private static final String _path = ConfigUtils.getConfig().getString(
			"template.promotion.path");
	private static final List<Integer> _templateSize = new ArrayList<Integer>(
			10);
	private static final int _defaultSize = ConfigUtils.getConfig().getInt(
			"template.promotion.defaultsize");
	private static final String _staticUri = ConfigUtils.getConfig().getString("uri.static");
	private static final int _staticUriLen = _staticUri.split("/").length;

	public TemplateHandler() {
		String[] configSize = ConfigUtils.getConfig()
				.getString("template.promotion.size").split(",");
		for (String s : configSize) {
			int val = NumberUtils.toInt(s);
			if (!_templateSize.contains(val)) {
				_templateSize.add(val);
			}
		}
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String uri = "/loop/img/promo/template/";
		if (target.startsWith(uri)) {
			
			String[] targetSplit = target.replace(uri, "").split("/");

			int size = 0;
			int id = 0;
			if (targetSplit.length > 1) {
				size = NumberUtils.toInt(targetSplit[0]);
				id = NumberUtils.toInt(targetSplit[1]);
			} else if (targetSplit.length > 0) {
				id = NumberUtils.toInt(targetSplit[0]);
			}

			PromotionTemplate template = null;
			if (id > 0) {
				template = PromotionTemplateDAO.getInstance().getById(id);
			}

			if (template != null) {
				int returnSize = _defaultSize;
				if (size > 0) {
					returnSize = SizeUtils.calculateSize(size, _templateSize);
				}

				Path source = Paths.get(_path + returnSize + "/"
						+ template.getImage());
				if(returnSize <= 0) {
					source = Paths.get(_path + template.getImage());
				}

				File imgFile = source.toFile();

				if (imgFile.exists()) {
					response.addHeader(HttpHeader.CONTENT_TYPE.asString(),
							Files.probeContentType(source));
					response.addHeader(HttpHeader.CONTENT_LENGTH.asString(),
							imgFile.length() + "");
					Files.copy(source, response.getOutputStream());
				} else {
					response.setStatus(HttpStatus.FORBIDDEN_403);
				}
			}

			baseRequest.setHandled(true);
		}
	}
}
