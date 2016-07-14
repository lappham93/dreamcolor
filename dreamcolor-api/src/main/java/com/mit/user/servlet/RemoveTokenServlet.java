package com.mit.user.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;

import com.mit.api.ApiError;
import com.mit.api.ApiMessage;
import com.mit.dao.user.DeviceTokenDAO;
import com.mit.servlet.ServletWrapper;

public class RemoveTokenServlet extends ServletWrapper {
	private final List<String> paramRequire = new ArrayList<String>(Arrays.asList("imei", "deviceId", "token"));

	@Override
	protected void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> params = getParameter(req);
		ApiMessage msg = new ApiMessage();
		if (params != null && checkEmptyParameter(params, paramRequire)) {
			int deviceId = NumberUtils.toInt(String.valueOf(params.get("deviceId")));
			String token = String.valueOf(params.get("token"));
			String imei = String.valueOf(params.get("imei"));
			int appId = (int) req.getAttribute("appId");

			DeviceTokenDAO.getInstance().deleteByImei(imei);
		} else {
			msg.setErr(ApiError.MISSING_PARAM.getValue());
		}

		printJson(req, resp, msg.toString());
	}

}
