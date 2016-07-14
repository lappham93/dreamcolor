package com.mit.payment.handler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.utils.JsonUtils;

public class AuthorizeRelay extends HandlerWrapper {
	private final Logger _logger = LoggerFactory.getLogger(AuthorizeRelay.class);
	private final List<String> paramRequire = Arrays.asList();

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (target.startsWith("/authorize/process")) {
			System.out.println(JsonUtils.Instance.toJson(request.getParameterMap()));
			
			baseRequest.setHandled(true);
		}
	}
}