package com.mit.upload.http.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.utils.JsonUtils;

public abstract class ServletWrapper extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(ServletWrapper.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug(req.getAttribute("uuid") + "\tSTART_REQUEST\tGET\t" + req.getRequestURI());
		doProcess(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug(req.getAttribute("uuid") + "\tSTART_REQUEST\tPOST\t" + req.getRequestURI());
		doProcess(req, resp);
	}

	protected void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {}

	public void printJson(HttpServletRequest req, ServletResponse resp, String json) throws IOException {
		resp.setContentType("application/json;charset=utf-8");
		PrintWriter writer = resp.getWriter();
		writer.write(json);
		writer.close();
		logger.debug(req.getAttribute("uuid") + "\tEND_REQUEST\t" + req.getMethod() + "\t" + req.getRequestURI() + "\t" + json);
	}

	public Map<String, Object> getParameter(HttpServletRequest req) {
		Map<String, Object> params = (Map<String, Object>) req.getAttribute("params");
		logger.debug(req.getAttribute("uuid") + "\tPARAMS\t" + req.getMethod() + "\t" + req.getRequestURI() + "\t" + req.getAttribute("params"));
		return params;
	}

	public List<Map<String, String>> getListParameter(HttpServletRequest req) {
		List<Map<String, String>> params = JsonUtils.Instance.getList((String)req.getAttribute("params"));
		logger.debug(req.getContextPath() + " params: " + req.getAttribute("params"));
		return params;
	}

	public boolean checkParameter(Map<String, Object> paramValues, List<String> params) {
		if(paramValues == null) {
			return false;
		}

		boolean isValid = !params.retainAll(paramValues.keySet());
		return isValid;
	}

	public boolean checkEmptyParameter(Map<String, Object> paramValues, List<String> params) {
		if(paramValues == null) {
			return false;
		}

		boolean isValid = true;
		for(String param : params) {
			if(paramValues.get(param) == null || paramValues.get(param).equals("")) {
				isValid = false;
				break;
			}
		}

		return isValid;
	}
}
