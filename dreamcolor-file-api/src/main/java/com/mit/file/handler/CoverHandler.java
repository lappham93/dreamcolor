package com.mit.file.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.bson.types.Binary;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.upload.UserCoverDAO;
import com.mit.file.utils.ImageResize;
import com.mit.file.utils.SizeUtils;
import com.mit.utils.ConfigUtils;
import com.mit.utils.ImageInfoUtils;
import com.mit.utils.NoiseIdUtils;

public class CoverHandler extends HandlerWrapper {
	private final Logger _logger = LoggerFactory
			.getLogger(PromotionHandler.class);
	private static final int _defaultSize = ConfigUtils.getConfig().getInt(
			"cov.defaultsize", 1280);
	private final List<Integer> _templateSize = Arrays.asList(320, 640, 1280);

	public CoverHandler() {
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String uri = "/loop/img/cov/";
		if (target.startsWith(uri)) {
			int size = NumberUtils.toInt(request.getParameter("size"), _defaultSize);
			String last = target.substring(uri.lastIndexOf("/") + 1);
			String idNoise = NoiseIdUtils.decryptString(last);
			String[] idNoiseArr = idNoise.split(",");

			if(idNoiseArr.length >= 3) {
				size = SizeUtils.calculateSize(size, _templateSize);
				int userId = NumberUtils.toInt(idNoiseArr[0]);
				int covVer = NumberUtils.toInt(idNoiseArr[1]);
				long tmpId = NumberUtils.toLong(idNoiseArr[2]);
				if(userId > 0) {
					byte[] data = null;
					if (UserCoverDAO.getInstance().isSameVersion(userId, covVer)) {
						Map<String, Object> covData = UserCoverDAO.getInstance().getByUserId(userId);
						if (covData != null && covData.get("data") != null) {
							data = ((Binary) covData.get("data")).getData();
						}
					} else if (tmpId > 0) {
						response.setStatus(HttpStatus.FORBIDDEN_403);
//						UploadTemp tempData = UploadTempDAO.getInstance().getById(tmpId);
//						if(tempData != null && tempData.getData() != null) {
//							ByteBuffer buffer = ByteBuffer.allocate(tempData.getSizeComplete());
//							for(byte[] d : tempData.getData()) {
//								buffer.put(d);
//							}
//							buffer.rewind();
//							data = buffer.array();
//						}
					} else {
						response.setStatus(HttpStatus.FORBIDDEN_403);
					}

					if (data != null) {
						if(size != 0) {
							ByteArrayOutputStream out = ImageResize.resizeImage(data, size);
							if(out != null) {
								data = out.toByteArray();
                                out.close();
							}
						}
						response.addHeader(HttpHeader.CONTENT_TYPE.asString(), new ImageInfoUtils(data).getMimeType());
						response.addHeader(HttpHeader.CONTENT_LENGTH.asString(),data.length + "");
						response.getOutputStream().write(data);
					} else {
						response.setStatus(HttpStatus.FORBIDDEN_403);
					}
				} else {
					response.setStatus(HttpStatus.FORBIDDEN_403);
				}
			} else {
				response.setStatus(HttpStatus.FORBIDDEN_403);
			}

			baseRequest.setHandled(true);
		}
	}
}
