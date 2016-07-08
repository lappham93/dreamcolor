package com.mit.api;

import com.mit.utils.JsonUtils;

public class ApiMessage {
	private int err;
	private Object data;

	public ApiMessage() {}

	public ApiMessage(int err) {
		super();
		this.err = err;
	}

	public ApiMessage(Object data) {
		super();
		this.data = data;
	}

	public int getErr() {
		return err;
	}

	public void setErr(int err) {
		this.err = err;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getData() {
//		if(data == null) {
//			return "";
//		}
		return data;
	}

	@Override
	public String toString() {
		return JsonUtils.Instance.toJson(this);
	}

}
