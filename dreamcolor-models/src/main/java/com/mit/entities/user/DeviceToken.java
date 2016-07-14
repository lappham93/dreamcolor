package com.mit.entities.user;

public class DeviceToken {
	public static final int ACTIVE = 1;

	private int id;
	private int appId;
	private int deviceId;
	private String imei;
	private String deviceToken;
	private int status;

	public DeviceToken() {}

	public DeviceToken(int id, int appId, int deviceId, String imei,
			String deviceToken, int status) {
		this.id = id;
		this.appId = appId;
		this.deviceId = deviceId;
		this.imei = imei;
		this.deviceToken = deviceToken;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
}
