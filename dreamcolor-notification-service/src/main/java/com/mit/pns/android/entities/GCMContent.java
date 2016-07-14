package com.mit.pns.android.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GCMContent {
	// id list push notification
	private List<String> registrationIds;
	private String collapseKey;
	private Object data;
	private boolean delayWhileIdle;
	private int timeToLive = (int) TimeUnit.DAYS.toSeconds(7);
	// restricted_package_name
	private boolean dryRun;

	public GCMContent() {
	}

	public GCMContent(List<String> registrationIds, Object data) {
		this.registrationIds = registrationIds;
		this.data = data;
	}

	public GCMContent(List<String> registrationIds, String collapseKey,
			Object data) {
		this.registrationIds = registrationIds;
		this.collapseKey = collapseKey;
		this.data = data;
	}

	@JsonProperty("registration_ids")
	public List<String> getRegistrationIds() {
		return registrationIds;
	}

	public void setRegistrationIds(List<String> registrationIds) {
		this.registrationIds = registrationIds;
	}

	@JsonProperty("collapse_key")
	public String getCollapseKey() {
		return collapseKey;
	}

	public void setCollapseKey(String collapseKey) {
		this.collapseKey = collapseKey;
	}

	@JsonProperty("data")
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@JsonProperty("delay_while_idle")
	public boolean isDelayWhileIdle() {
		return delayWhileIdle;
	}

	public void setDelayWhileIdle(boolean delayWhileIdle) {
		this.delayWhileIdle = delayWhileIdle;
	}

	@JsonProperty("time_to_live")
	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	@JsonProperty("dry_run")
	public boolean isDryRun() {
		return dryRun;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	public Map<String, Object> buildPayloadMessage() {
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("registration_ids", registrationIds);
		msg.put("time_to_live", timeToLive);
		msg.put("data", data);
		msg.put("dry_run", dryRun);
		return msg;
	}

}
