package com.mit.entities.app;

public class AppKey {
	public static final int CYOGEL = 1;
	public static final int BIZ_LOOP = 2;
    
	private int os;
	private int appId;
	private String appName;
	private int groupId;
	private String groupName;
	private String apiKey;
	private String secretKey;

	public AppKey() {
		super();
	}

	public AppKey(int os, int appId, String appName, int groupId, String groupName,
			String apiKey, String secretKey) {
		this.os = os;
		this.appId = appId;
		this.appName = appName;
		this.groupId = groupId;
		this.groupName = groupName;
		this.apiKey = apiKey;
		this.secretKey = secretKey;
	}

	public int getOs() {
		return os;
	}

	public void setOs(int os) {
		this.os = os;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

}
