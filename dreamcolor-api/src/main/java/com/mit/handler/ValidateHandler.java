package com.mit.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.api.ApiError;
import com.mit.api.ApiMessage;
import com.mit.entities.app.AppKey;
import com.mit.models.AppKeyModel;
import com.mit.utils.AESDigestUtils;
import com.mit.utils.JsonUtils;

public class ValidateHandler extends HandlerWrapper {
	private final Logger _logger = LoggerFactory.getLogger(ValidateHandler.class);
	private final List<String> _paramValids = Arrays.asList("api_key", "timestamp", "params");

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		long t1 = System.nanoTime();
		List<String> paramValids = new ArrayList<String>();
		paramValids.addAll(_paramValids);

		if(isValidRequestParams(request, paramValids)) {
			String apiKey = request.getParameter("api_key");
			String paramEncode = request.getParameter("params");

			AppKey appKey = AppKeyModel.getInstance().getAppKey(apiKey);
_logger.debug(apiKey + " -- " + (appKey != null ? appKey.getSecretKey() : "") );
			if(appKey != null) {
				try {
					Map<String, Object> paramDecode = JsonUtils.Instance.getMapObject(AESDigestUtils.decrypt(paramEncode, appKey.getSecretKey()));
					request.setAttribute("params", paramDecode);
					request.setAttribute("uuid", System.nanoTime());
					request.setAttribute("appId", appKey.getAppId());
					request.setAttribute("os", appKey.getOs());

					super.handle(target, baseRequest, request, response);
				} catch (Exception e) {
					printError(response, ApiError.DATA_INVALID.getValue());
					_logger.error("decrypt data error: ", e);
				}
			} else {
				printError(response, ApiError.API_INVALID.getValue());
			}
		} else {
			printError(response, ApiError.PARAMS_INVALID.getValue());
		}

		baseRequest.setHandled(true);
_logger.debug(target + ": " + (System.nanoTime() - t1) + "ns");
	}

	private boolean isValidRequestParams(HttpServletRequest req, List<String> paramValids) {
		boolean isValid = true;
		List<String> queryParams = Collections.list(req.getParameterNames());
		isValid = !paramValids.retainAll(queryParams);
		if(isValid) {
			for(String p : paramValids) {
				String val = req.getParameter(p).trim();
				if(val.isEmpty()) {
					isValid = false;
					break;
				}
			}
		}
		return  isValid;
	}

	public void printError(HttpServletResponse resp, int err) throws IOException {
		resp.setContentType("application/json;charset=utf-8");

		ApiMessage msg = new ApiMessage(err);

		PrintWriter writer = resp.getWriter();
		writer.write(msg.toString());
		writer.close();
	}
}
