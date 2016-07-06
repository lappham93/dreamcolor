package com.mit.upload.http.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.MultipartConfigElement;
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
import com.mit.utils.ConfigUtils;
import com.mit.utils.JsonUtils;

public class MultiPartValidateHandler extends HandlerWrapper {
	private final Logger _logger = LoggerFactory.getLogger(MultiPartValidateHandler.class);
	private final List<String> _paramValids = Arrays.asList("api_key", "timestamp", "params");

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
        long t = System.nanoTime();
		MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(ConfigUtils.getConfig().getString("upload.temp"));
		request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);

		Map<String, byte[]> params = MultipartUpload.getMultipartData(request);

		List<String> paramValids = new ArrayList<String>();
		paramValids.addAll(_paramValids);

		if(isValidRequestParams(params, paramValids)) {
			String apiKey = new String(params.get("api_key"));
			String paramEncode = new String(params.get("params"));

			AppKey appKey = AppKeyModel.getInstance().getAppKey(apiKey);
			if(appKey != null) {
				try {
					Map<String, Object> paramDecode = JsonUtils.Instance.getMapObject(AESDigestUtils.decrypt(paramEncode, appKey.getSecretKey()));
					if(paramDecode != null) {
						request.setAttribute("params", paramDecode);
						request.setAttribute("uuid", System.nanoTime());
						request.setAttribute("appName", appKey.getAppName());
						request.setAttribute("file", params.get("file"));
                        request.setAttribute("filename", params.get("filename"));

//						if(paramDecode.get("sessionKey") != null) {
//							String sessionKey = (String)paramDecode.get("sessionKey");
//							LoginInfo loginInfo = (LoginInfo)AuthenticateModel.Instance.logged(sessionKey);
//							if(loginInfo.getErr() == UserErrCode.ERR_IMEI) {
//								printError(response, ApiError.ERR_IMEI.getValue());
//							} else if(loginInfo.getErr() == UserErrCode.SUCCESS) {
//								request.setAttribute("accountInfo", new UserSessionEx(loginInfo.getUserId(), loginInfo.getTypeId(), loginInfo.getBizId(), loginInfo.getRoleId(), loginInfo.getImei()));
//								super.handle(target, baseRequest, request, response);
//							} else if(loginInfo.getErr() == UserErrCode.NOT_AUTHENTICATE) {
//								printError(response, ApiError.SESSION_TIMEOUT.getValue());
//							}
//						} else {
//							printError(response, ApiError.SESSION_TIMEOUT.getValue());
//						}
					} else {
						printError(response, ApiError.DATA_INVALID.getValue());
					}

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
        _logger.info("MultiPartValidateHandler.handle Time: " + (System.nanoTime() - t) + "ns, params: " + params);
	}

	private boolean isValidRequestParams(Map<String, byte[]> params, List<String> paramValids) {
		boolean isValid = true;
		isValid = !paramValids.retainAll(params.keySet());
		if(isValid) {
			for(String p : paramValids) {
				byte[] val = params.get(p);
				if(val == null || val.length == 0) {
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
