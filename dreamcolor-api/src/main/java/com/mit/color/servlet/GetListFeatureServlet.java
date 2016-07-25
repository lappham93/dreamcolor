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
import com.mit.servlet.ServletWrapper;

public class GetListFeatureServlet extends ServletWrapper {
	private final List<String> paramRequire = Arrays.asList();

	@Override
	protected void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> params = getParameter(req);
		ApiMessage msg = new ApiMessage();
		if (params != null && checkEmptyParameter(params, paramRequire)) {
			try {
				// int page =
				// NumberUtils.toInt(String.valueOf(params.get("page")));
				int count = NumberUtils.toInt(String.valueOf(params.get("count")), 20);
				// int from = (page > 1) ? (page - 1)*count : 0;

				Map<String, Object> rs = ColorModel.Instance.getListFeature(count);
				int err = (int) rs.get("err");
				msg.setErr(err);
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
