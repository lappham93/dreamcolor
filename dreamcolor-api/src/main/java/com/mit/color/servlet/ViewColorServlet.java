package com.mit.color.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;

import com.mit.api.ApiError;
import com.mit.api.ApiMessage;
import com.mit.models.ColorModel;
import com.mit.models.ModelError;
import com.mit.servlet.ServletWrapper;

public class ViewColorServlet extends ServletWrapper{
	private final List<String> paramRequire = Arrays.asList("colorId");

	@Override
	protected void doProcess(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Map<String, Object> params = getParameter(req);
		ApiMessage msg = new ApiMessage();
		if(params != null && checkEmptyParameter(params, paramRequire)) {
			try {
				long colorId = NumberUtils.toLong(String.valueOf(params.get("colorId")));
					
				Map<String, Object> rs = ColorModel.Instance.viewColor(colorId);
				int err = (int)rs.get("err");
				if (err == ModelError.COLOR_NOT_EXIST) {
					msg.setErr(ApiError.COLOR_NOT_EXIST.getValue());
				} else if (err == ModelError.SERVER_ERROR) {
					msg.setErr(ApiError.UNKNOWN.getValue());
				}
				rs.remove("err");
				msg.setData(rs);
			} catch (Exception e) {
				msg.setErr(ApiError.PARAMS_INVALID.getValue());
			}
		} else {
			msg.setErr(ApiError.MISSING_PARAM.getValue());
		}

		printJson(req, resp, msg.toString());
	}
}

