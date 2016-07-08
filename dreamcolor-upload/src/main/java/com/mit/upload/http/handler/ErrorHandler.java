package com.mit.upload.http.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import com.mit.api.ApiError;
import com.mit.api.ApiMessage;

public class ErrorHandler extends HandlerWrapper {

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		response.setContentType("application/json;charset=utf-8");

		ApiMessage msg = new ApiMessage(ApiError.UNKNOWN.getValue());

		PrintWriter writer = response.getWriter();
		writer.write(msg.toString());
		writer.close();
	}
}
