package com.mit.upload.http.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipartUpload {
	private static final Logger _logger = LoggerFactory.getLogger(MultipartUpload.class);

	public static Map<String, byte[]> getMultipartData(HttpServletRequest req) {
		Map<String, byte[]> data = new HashMap<String, byte[]>();

		try {
			Collection<Part> parts = req.getParts();
			for(Part p : parts) {
				byte[] bytes = new byte[(int)p.getSize()];
				p.getInputStream().read(bytes, 0, bytes.length);
				data.put(p.getName(), bytes);
				p.delete();
			}
		} catch (Exception e) {
			_logger.error("error multipart ", e);
		}

		return data;
	}
}
