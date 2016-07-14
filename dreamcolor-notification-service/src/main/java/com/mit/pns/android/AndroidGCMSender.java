package com.mit.pns.android;

import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.user.DeviceTokenDAO;
import com.mit.entities.user.DeviceToken;
import com.mit.pns.android.entities.GCMResult;
import com.mit.pns.android.entities.Result;
import com.mit.pns.utils.JettyHTTPRequest;
import com.mit.utils.ConfigUtils;
import com.mit.utils.JsonUtils;


public class AndroidGCMSender {
	private static Logger _logger = LoggerFactory
			.getLogger(AndroidGCMSender.class);

	protected static final String UTF8 = "UTF-8";
	protected static final String API_KEY = ConfigUtils.getConfig().getString("gcm.api.key");

	protected static final int BACKOFF_INITIAL_DELAY = 60000;
	protected static final int MAX_RETRIES = 5;
	protected static final int MAX_BACKOFF_DELAY = 512000;

	protected static final String GCM_SEND_ENDPOINT = "https://android.googleapis.com/gcm/send";
	/**
	 * Too many messages sent by the sender. Retry after a while.
	 */
	public static final String ERROR_QUOTA_EXCEEDED = "QuotaExceeded";

	/**
	 * Too many messages sent by the sender to a specific device. Retry after a
	 * while.
	 */
	public static final String ERROR_DEVICE_QUOTA_EXCEEDED = "DeviceQuotaExceeded";

	/**
	 * Missing registration_id. Sender should always add the registration_id to
	 * the request.
	 */
	public static final String ERROR_MISSING_REGISTRATION = "MissingRegistration";

	/**
	 * Bad registration_id. Sender should remove this registration_id.
	 */
	public static final String ERROR_INVALID_REGISTRATION = "InvalidRegistration";

	/**
	 * The sender_id contained in the registration_id does not match the
	 * sender_id used to register with the GCM servers.
	 */
	public static final String ERROR_MISMATCH_SENDER_ID = "MismatchSenderId";

	/**
	 * The user has uninstalled the application or turned off notifications.
	 * Sender should stop sending messages to this device and delete the
	 * registration_id. The client needs to re-register with the GCM servers to
	 * receive notifications again.
	 */
	public static final String ERROR_NOT_REGISTERED = "NotRegistered";

	/**
	 * The payload of the message is too big, see the limitations. Reduce the
	 * size of the message.
	 */
	public static final String ERROR_MESSAGE_TOO_BIG = "MessageTooBig";

	/**
	 * Collapse key is required. Include collapse key in the request.
	 */
	public static final String ERROR_MISSING_COLLAPSE_KEY = "MissingCollapseKey";

	/**
	 * A particular message could not be sent because the GCM servers were not
	 * available. Used only on JSON requests, as in plain text requests
	 * unavailability is indicated by a 503 response.
	 */
	public static final String ERROR_UNAVAILABLE = "Unavailable";

	/**
	 * A particular message could not be sent because the GCM servers
	 * encountered an error. Used only on JSON requests, as in plain text
	 * requests internal errors are indicated by a 500 response.
	 */
	public static final String ERROR_INTERNAL_SERVER_ERROR = "InternalServerError";

	/**
	 * Time to Live value passed is less than zero or more than maximum.
	 */
	public static final String ERROR_INVALID_TTL = "InvalidTtl";

	public static GCMResult send(String message, List<DeviceToken> deviceToken) throws IOException {
		int attempt = 0;
		GCMResult result = null;
		int backoff = BACKOFF_INITIAL_DELAY;
		boolean tryAgain;
		//String message = JsonUtils.Instance.toJson(data);

		do {
			attempt++;
			_logger.debug("GCMSender retry: " + attempt + " send: " + message);
			ContentResponse response = sendRequest(message);
			if(response != null) {
				int status = response.getStatus();
				if(status / 100 == 5) {
					_logger.error("GCM service is unavailable (status " + status + ")");
				} else if (status != 200) {
					String responseBody = response.getContentAsString();
					throw new InvalidRequestException(status, responseBody);
				} else {
					String responseBody = response.getContentAsString();
					result = JsonUtils.Instance.getObject(GCMResult.class, responseBody);
				}
			}

			tryAgain = result == null && attempt <= MAX_RETRIES;
			if (tryAgain) {
				int sleepTime = backoff;
				sleep(sleepTime);
				if (2 * backoff < MAX_BACKOFF_DELAY) {
					backoff *= 2;
				}
			}
		} while (tryAgain);
		if (result == null) {
			throw new IOException("Could not send message after " + attempt
					+ " attempts");
		}
		if (deviceToken != null) {
			handleResult(deviceToken, result);
		}
		return result;
	}

	private static ContentResponse sendRequest(String message) {
		ContentResponse response = null;
		HttpClient httpClient = JettyHTTPRequest.Instance.getHttpClient();
		if (httpClient != null && httpClient.isStarted()) {
			Request request = httpClient.POST(GCM_SEND_ENDPOINT);
			request.header("Authorization", " key=" + API_KEY);
			request = request.content(new StringContentProvider(message), "application/json; charset=utf-8");
			try {
				response = request.send();
			} catch (Exception e) {
				_logger.error("GCMRequest error: ", e);
			}
		} else {
			_logger.error("HttpClient don't run");
			try {
				httpClient.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void handleResult(List<DeviceToken> deviceToken, GCMResult results) {
		if(results.getFailure() > 0 || results.getCanonicalIds() > 0) {
			int i = 0;
			for(Result data : results.getResults()) {
				if(data.getErrorCode() != null) {
					handleError(data.getErrorCode(), deviceToken.get(i));
				}

				if(data.getRegistrationId() != null) {
					updateRegistration(data.getRegistrationId(), deviceToken.get(i));
				}

				i++;
			}
		}
	}

	private static void handleError(String error, DeviceToken token) {
		if(error.equals(ERROR_NOT_REGISTERED)) {
//			DeviceTokenDAO.getInstance().deleteByToken(token.getDeviceToken());
		}
	}

	private static void updateRegistration(String newToken, DeviceToken token) {
		token.setDeviceToken(newToken);
		DeviceTokenDAO.getInstance().update(token);
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
