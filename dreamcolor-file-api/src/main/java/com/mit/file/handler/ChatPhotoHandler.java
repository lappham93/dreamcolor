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

import com.mit.dao.upload.ChatPhotoDAO;
import com.mit.dao.upload.UploadTempDAO;
import com.mit.entities.upload.UploadTemp;
import com.mit.file.utils.ImageResize;
import com.mit.file.utils.SizeUtils;
import com.mit.utils.ImageInfoUtils;
import com.mit.utils.NoiseIdUtils;

public class ChatPhotoHandler extends HandlerWrapper {
//	private static final int _defaultSize = ConfigUtils.getConfig().getInt(
//			"avt.defaultsize", 0);

	public ChatPhotoHandler() {
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String uri = "/loop/img/c/";
		if (target.startsWith(uri)) {
			int size = NumberUtils.toInt(request.getParameter("size"), 0);
			String last = target.substring(uri.lastIndexOf("/") + 1);
			String idNoise = NoiseIdUtils.decryptString(last);
			String[] idNoiseArr = idNoise.split(",");

			if(idNoiseArr.length >= 3) {
				long tmpId = NumberUtils.toLong(idNoiseArr[0]);
				int userId = NumberUtils.toInt(idNoiseArr[1]);
				long time = NumberUtils.toLong(idNoiseArr[2]);

				if(userId > 0) {
					byte[] data = null;
					if (tmpId > 0) {
						Map<String, Object> photoData = ChatPhotoDAO.getInstance().getById(tmpId);
						if(photoData == null) {
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
							if (photoData.get("data") != null) {
								data = ((Binary) photoData.get("data")).getData();
							}
						}
					}

					if (data != null) {
						size = SizeUtils.calculateSize(size);
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
		String idNoise = NoiseIdUtils.decryptString("0000001404921fb45a9797ee71665778ac7cb49846cb654cba9b4979");

		String[] idNoiseArr = idNoise.split(",");
		int size = 0;

		if(idNoiseArr.length >= 3) {
			long tmpId = NumberUtils.toLong(idNoiseArr[0]);
			int userId = NumberUtils.toInt(idNoiseArr[1]);
			long time = NumberUtils.toLong(idNoiseArr[2]);

			if(userId > 0) {
				byte[] data = null;
				if (tmpId > 0) {
					Map<String, Object> photoData = ChatPhotoDAO.getInstance().getById(tmpId);
					if(photoData == null) {
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
						if (photoData.get("data") != null) {
							data = ((Binary) photoData.get("data")).getData();
						}
					}
				}

				if (data != null) {
					size = SizeUtils.calculateSize(size);
					if(size != 0) {
						ByteArrayOutputStream out = ImageResize.resizeImage(data, size);
						if(out != null) {
							data = out.toByteArray();
						}
					}
				} else {
				}
			} else {
			}
		} else {
		}
	}
}
