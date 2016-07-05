package com.mit.file.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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

import com.mit.dao.upload.UploadTempDAO;
import com.mit.dao.upload.UserAvatarDAO;
import com.mit.entities.upload.UploadTemp;
import com.mit.file.utils.ImageResize;
import com.mit.file.utils.SizeUtils;
import com.mit.utils.ConfigUtils;
import com.mit.utils.ImageInfoUtils;
import com.mit.utils.JsonUtils;
import com.mit.utils.NoiseIdUtils;

public class AvatarHandler extends HandlerWrapper {
	private final Logger _logger = LoggerFactory
			.getLogger(PromotionHandler.class);
	private static final int _defaultSize = ConfigUtils.getConfig().getInt(
			"avt.defaultsize", 0);

	public AvatarHandler() {
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String uri = "/cyog/img/avt/";
		if (target.startsWith(uri)) {
			int size = NumberUtils.toInt(request.getParameter("size"), _defaultSize);
			String last = target.substring(uri.lastIndexOf("/") + 1);
			String idNoise = NoiseIdUtils.decryptString(last);
			String[] idNoiseArr = idNoise.split(",");

			if(idNoiseArr.length >= 3) {
				size = SizeUtils.calculateSize(size);
				int userId = NumberUtils.toInt(idNoiseArr[0]);
				int avtVer = NumberUtils.toInt(idNoiseArr[1]);
				long tmpId = NumberUtils.toLong(idNoiseArr[2]);
				if(userId > 0) {
					byte[] data = null;
					if (UserAvatarDAO.getInstance().isSameVersion(userId, avtVer)) {
						Map<String, Object> avtData = UserAvatarDAO.getInstance().getByUserId(userId);
						if (avtData != null && avtData.get("data") != null) {
							data = ((Binary) avtData.get("data")).getData();
						}
					} else if (tmpId > 0) {
						UploadTemp tempData = UploadTempDAO.getInstance().getById(tmpId);
						if(tempData != null && tempData.getData() != null) {
							ByteBuffer buffer = ByteBuffer.allocate(tempData.getSizeComplete());
							for(byte[] d : tempData.getData()) {
								buffer.put(d);
							}
							buffer.rewind();
							data = buffer.array();
						}
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

	public static void main(String[] args) {
		System.out.println(NoiseIdUtils.decryptString("0000000703e41e66927d6411"));
		//27,17,0
//		System.out.println(NoiseIdUtils.decryptString("AAAAB431Lddq4z9y"));
		System.out.println(JsonUtils.Instance.toJson(UserAvatarDAO.getInstance().getByUserId(15)));
//		System.out.println(JsonUtils.Instance.toJson(UploadTempDAO.getInstance().getById(27)));
//		UploadTemp tempData = UploadTempDAO.getInstance().getById(27);
//		if(tempData != null && tempData.getData() != null) {
//			ByteBuffer buffer = ByteBuffer.allocate(tempData.getSizeComplete());
//			for(byte[] d : tempData.getData()) {
//				buffer.put(d);
//			}
//
//			buffer.rewind();
//			byte[] data = buffer.array();
//		}
	}
}
