package com.mit.pns.android.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {
	private String messageId;
	private String registrationId;
	private String error;

	public Result() {}

	@JsonProperty("message_id")
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	@JsonProperty("registration_id")
	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}


	@JsonProperty("error")
	public String getErrorCode() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
