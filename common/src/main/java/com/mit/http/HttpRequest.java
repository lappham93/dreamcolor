package com.mit.http;

import java.util.Map;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {
	private final Logger _logger = LoggerFactory.getLogger(HttpRequest.class);
	HttpClient _httpClient;

	public static HttpRequest Instance = new HttpRequest();

	private HttpRequest() {
		_httpClient = new HttpClient();
		try {
			_httpClient.setRequestBufferSize(10240);
			_httpClient.start();
		} catch (Exception e) {
			_logger.error("error start httpClient ", e);
		}
	}

	public ContentResponse doGetResponse(String url) {
		ContentResponse response = null;
		try {
			response = _httpClient.GET(url);
		} catch (Exception e) {
			_logger.error("doGet ", e);
			e.printStackTrace();
		}

		return response;
	}

	public ContentResponse doPost(String url, Map<String, String> params) {
		ContentResponse response = null;
		try {
			Request request = _httpClient.POST(url);
			for(String key : params.keySet()) {
				request = request.param(key, params.get(key));
			}
			response = request.send();
		} catch (Exception e) {
			_logger.error("doPost ", e);
			e.printStackTrace();
		}

		return response;
	}
}
