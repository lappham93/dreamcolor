package com.mit.api;

import com.mit.api.ApiMessage;
import com.fasterxml.jackson.annotation.JsonRawValue;

public class ApiMessageRawData extends ApiMessage {

	public ApiMessageRawData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ApiMessageRawData(int err) {
		super(err);
		// TODO Auto-generated constructor stub
	}

	public ApiMessageRawData(Object data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@JsonRawValue
	@Override
	public Object getData() {
		return super.getData();
	}

}
