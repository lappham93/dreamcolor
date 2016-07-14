package com.mit.pns.utils;

import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.ssl.SslContextFactory;

@SuppressWarnings({ "rawtypes" })
public class JettyHTTPRequest {
	public static final int CONNECT_TIME_OUT = 6000;
	public static final int READ_TIME_OUT = 6000;

	private static final String CONTENT_TYPE_HEADER = "application/x-www-form-urlencoded;charset=UTF-8";
	public static String USER_AGENT_FIREFOX = "Mozilla/5.0 (Windows NT 5.1; rv:9.0) Gecko/20100101 Firefox/9.0";

	public static final String POST = "POST";
	public static final String GET = "GET";

	public static JettyHTTPRequest Instance = new JettyHTTPRequest();

	private  static HttpClient httpClient;

	private JettyHTTPRequest() {
		try {
			init();
		} catch (Exception e) {

		}
	}

	public void init() throws Exception {
		httpClient = new HttpClient(new SslContextFactory());
		httpClient.setFollowRedirects(false);
		httpClient.setMaxRedirects(5);

		String proxyHost = System.getProperty("https.proxyHost", "");
		int port = NumberUtils.toInt(System.getProperty("https.proxyPort"));
		if(!proxyHost.isEmpty() && port > 0) {
			HttpProxy proxy = new HttpProxy(proxyHost, port);
			httpClient.getProxyConfiguration().getProxies().add(proxy);
		}

		httpClient.setUserAgentField(new HttpField(HttpHeader.USER_AGENT, USER_AGENT_FIREFOX));
		httpClient.setConnectTimeout(CONNECT_TIME_OUT);
		httpClient.start();
//		httpClient.setMaxConnectionsPerDestination(1);
	}

	public ContentResponse sendPostRequest(String url, Map<String, String> params) throws Exception {
		Request request = httpClient.POST(url);
		for(String key : params.keySet()) {
			request.param(key, params.get(key));
		}

		ContentResponse response = request.send();
		return response;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}
}
