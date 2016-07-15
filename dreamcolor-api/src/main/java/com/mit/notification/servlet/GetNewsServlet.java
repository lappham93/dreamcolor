package com.mit.notification.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;

import com.mit.api.ApiError;
import com.mit.api.ApiMessage;
import com.mit.dao.user.DeviceTokenDAO;
import com.mit.entities.user.DeviceToken;
import com.mit.models.NewsModel;
import com.mit.servlet.ServletWrapper;

public class GetNewsServlet extends ServletWrapper {
	private final List<String> paramRequire = Arrays.asList("page", "count", "imei");

	@Override
	protected void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, Object> params = getParameter(req);
		ApiMessage msg = new ApiMessage();
		if (params != null && checkEmptyParameter(params, paramRequire)) {
			String imei = String.valueOf("imei");
			int page = NumberUtils.toInt(String.valueOf(params.get("page")));
			int count = NumberUtils.toInt(String.valueOf(params.get("count")), 10);

			int from = (page > 1) ? (page - 1) * count : 0;
			DeviceToken device = DeviceTokenDAO.getInstance().getByImei(imei);
			if (device != null) {
				Map<String, Object> rs = NewsModel.Instance.getNewsList(device.getId(), from, count);
				msg.setData(rs);
			} else {
				msg.setErr(ApiError.PARAMS_INVALID.getValue());
			}
		} else {
			msg.setErr(ApiError.MISSING_PARAM.getValue());
		}

		printJson(req, resp, msg.toString());
	}
}
