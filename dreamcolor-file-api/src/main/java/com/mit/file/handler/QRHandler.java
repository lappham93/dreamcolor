package com.mit.file.handler;

import java.io.IOException;

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

import com.mit.qr.thrift.QRData;
import com.mit.qr.thrift.wrapper.QRReadServiceClient;

public class QRHandler extends HandlerWrapper {
	private final Logger _logger = LoggerFactory.getLogger(QRHandler.class);

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {


		String uri = "/loop/img/qrcode/";
		if (target.startsWith(uri)) {
			String[] targetSplit = target.replace(uri, "").split("/");

			int size = 0;
			String id = null;
			if (targetSplit.length > 1) {
				size = NumberUtils.toInt(targetSplit[0]);
				id = targetSplit[1];
			} else if (targetSplit.length > 0) {
				id = targetSplit[0];
			}

			try {
				if(id != null && !id.trim().isEmpty()) {
					QRData qrData = QRReadServiceClient.Instance.getData(id);
					if (qrData != null && qrData.getErrCode() >= 0 && qrData.getData() != null && qrData.getData().length > 0) {
						response.addHeader(HttpHeader.CONTENT_TYPE.asString(),
								"image/jpeg");
						response.addHeader(HttpHeader.CONTENT_LENGTH.asString(),
								qrData.getData().length + "");
						response.getOutputStream().write(qrData.getData());
					} else {
						response.setStatus(HttpStatus.NOT_FOUND_404);
					}
				} else {
					response.setStatus(HttpStatus.NOT_FOUND_404);
				}
			} catch (Exception e) {
				_logger.error("qr error ", e);
			}

			baseRequest.setHandled(true);
		}
	}

	public static void main(String[] args) {
		String id = "00000008564a5160ac76306f";
		QRData qrData = QRReadServiceClient.Instance.getData(id);
		System.out.println(qrData);
	}
}
