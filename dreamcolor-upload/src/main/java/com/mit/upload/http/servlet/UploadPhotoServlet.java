package com.mit.upload.http.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;

import com.mit.api.ApiError;
import com.mit.api.ApiMessage;
import com.mit.models.ModelError;
import com.mit.models.UploadPhotoModel;

public class UploadPhotoServlet extends ServletWrapper{
	private final List<String> paramRequire = Arrays.asList();

	@Override
	protected void doProcess(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Map<String, Object> params = getParameter(req);
		ApiMessage msg = new ApiMessage();
		if(params != null && checkEmptyParameter(params, paramRequire)) {
			int type = NumberUtils.toInt(String.valueOf(params.get("type")));
			String fileName = String.valueOf(params.get("fileName"));
			byte[] data = (byte[])req.getAttribute("file");
			
			Map<String, Object> rs = UploadPhotoModel.Instance.uploadPhoto(data, fileName, type);
			int err = (int)rs.get("err");
			if (err == ModelError.PHOTO_DATA_NULL) {
				msg.setErr(ApiError.PARAMS_INVALID.getValue());
			} else {
				rs.remove("err");
				msg.setData(rs);
			}
		} else {
			msg.setErr(ApiError.MISSING_PARAM.getValue());
		}

		printJson(req, resp, msg.toString());
	}
}
