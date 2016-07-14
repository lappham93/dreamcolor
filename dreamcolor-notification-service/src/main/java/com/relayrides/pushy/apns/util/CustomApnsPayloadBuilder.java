package com.relayrides.pushy.apns.util;

import java.util.Map;

import org.json.simple.JSONObject;

public class CustomApnsPayloadBuilder extends ApnsPayloadBuilder{

	private String message = null;

	private static final String MESSAGE_KEY = "msg";

	public CustomApnsPayloadBuilder() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String buildWithMaximumLength(final int maximumPayloadLength) {
		final JSONObject payload = new JSONObject();

		{
			final JSONObject aps = new JSONObject();

			if (super.badgeNumber != null) {
				aps.put(BADGE_KEY, this.badgeNumber);
			}

			if (this.soundFileName != null) {
				aps.put(SOUND_KEY, this.soundFileName);
			}

			if (this.categoryName != null) {
				aps.put(CATEGORY_KEY, this.categoryName);
			}

			if (this.message != null) {
				aps.put(MESSAGE_KEY, this.message);
			}

			if (this.contentAvailable) {
				aps.put(CONTENT_AVAILABLE_KEY, 1);
			}

			final Object alertObject = super.createAlertObject();

			if (alertObject != null) {
				aps.put(ALERT_KEY, alertObject);
			}

			payload.put(APS_KEY, aps);
		}

		for (final Map.Entry<String, Object> entry : this.customProperties.entrySet()) {
			payload.put(entry.getKey(), entry.getValue());
		}

		final String payloadString = payload.toJSONString();
		final int initialPayloadLength = payloadString.getBytes(UTF8).length;

		if (initialPayloadLength <= maximumPayloadLength) {
			return payloadString;
		} else {
			// TODO This could probably be more efficient
			if (this.alertBody != null) {
				this.replaceMessageBody(payload, "");
				final int payloadLengthWithEmptyMessage = payload.toJSONString().getBytes(UTF8).length;

				if (payloadLengthWithEmptyMessage > maximumPayloadLength) {
					throw new IllegalArgumentException("Payload exceeds maximum length even with an empty message body.");
				}

				int maximumMessageBodyLength = maximumPayloadLength - payloadLengthWithEmptyMessage;

				this.replaceMessageBody(payload, this.abbreviateString(this.alertBody, maximumMessageBodyLength--));

				while (payload.toJSONString().getBytes(UTF8).length > maximumPayloadLength) {
					this.replaceMessageBody(payload, this.abbreviateString(this.alertBody, maximumMessageBodyLength--));
				}

				return payload.toJSONString();

			} else {
				throw new IllegalArgumentException(String.format(
						"Payload length is %d bytes (with a maximum of %d bytes) and cannot be shortened.",
						initialPayloadLength, maximumPayloadLength));
			}
		}
	}
}
