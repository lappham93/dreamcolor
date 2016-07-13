package com.mit.user.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.api.ApiError;
import com.mit.api.ApiMessage;
import com.mit.servlet.ServletWrapper;
import com.mit.utils.SendEmailUtils;

public class SendFeedbackServlet extends ServletWrapper {
	private final List<String> paramRequire = new ArrayList<String>(Arrays.asList("message"));
	private final Logger logger = LoggerFactory.getLogger(SendFeedbackServlet.class);

	@Override
	protected void doProcess(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Map<String, Object> params = getParameter(req);
		ApiMessage msg = new ApiMessage();
		if(params != null && checkEmptyParameter(params, paramRequire)) {
			String message = String.valueOf(params.get("message"));
			String name = params.get("name") != null ? String.valueOf(params.get("name")) : "";
			String email = params.get("email") != null ? String.valueOf(params.get("email")) : "";
			String phone = params.get("phone") != null ? String.valueOf(params.get("phone")) : "";
		
//			String message = req.getParameter("message");
//			String name = req.getParameter("name") != null ? req.getParameter("name") : "";
//			String email = req.getParameter("email") != null ? req.getParameter("email") : "";
//			String phone = req.getParameter("phone") != null ? req.getParameter("phone") : "";
			logger.debug("lap: " + message + " -- " + name + " -- " + email + " -- " + phone);
			SendEmailUtils.Instance.sendFeedback(name, email, phone, message);
		} else {
			msg.setErr(ApiError.MISSING_PARAM.getValue());
		}

		printJson(req, resp, msg.toString());
	}

}
