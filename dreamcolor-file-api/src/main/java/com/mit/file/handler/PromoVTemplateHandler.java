package com.mit.file.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.promotion.PromotionBizTemplateDAO;
import com.mit.file.utils.ImageResize;
import com.mit.file.utils.SizeUtils;
import com.mit.utils.ConfigUtils;
import com.mit.utils.ImageInfoUtils;
import com.mit.utils.NoiseIdUtils;

public class PromoVTemplateHandler extends HandlerWrapper {
	private final Logger _logger = LoggerFactory
			.getLogger(PromotionHandler.class);
	private static final int _defaultSize = ConfigUtils.getConfig().getInt(
			"avt.defaultsize", 0);
	private final List<Integer> _templateSize = Arrays.asList(183, 365, 730);

	public PromoVTemplateHandler() {
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String uri = "/loop/img/tml/v/";
		if (target.startsWith(uri)) {
			int size = NumberUtils.toInt(request.getParameter("size"), _defaultSize);
			String last = target.substring(uri.lastIndexOf("/") + 1);
			String idNoise = NoiseIdUtils.decryptString(last);
			String[] idNoiseArr = idNoise.split(",");

			if(idNoiseArr.length >= 2) {
				size = SizeUtils.calculateSize(size, _templateSize);
				int id = NumberUtils.toInt(idNoiseArr[0]);
				int vertVer = NumberUtils.toInt(idNoiseArr[1]);
				if(id > 0) {
					byte[] data = null;
					if (PromotionBizTemplateDAO.getInstance().isSameVertVersion(id, vertVer)) {
						data = PromotionBizTemplateDAO.getInstance().getVertImg(id);
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
		List<Integer> _templateSize = Arrays.asList(183, 365, 730);
		int size = 3000;
		size = SizeUtils.calculateSize(size, _templateSize);
		System.out.println(size);
	}
}
