package com.mit.pns.android.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GCMResult {
	private long multicastId;
	private int success;
	private int failure;
	private long canonicalIds;
	private List<Result> results;

	public GCMResult() {}

	@JsonProperty("multicast_id")
	public long getMulticastId() {
		return multicastId;
	}

	public void setMulticastId(long multicastId) {
		this.multicastId = multicastId;
	}

	@JsonProperty("success")
	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	@JsonProperty("failure")
	public int getFailure() {
		return failure;
	}

	public void setFailure(int failure) {
		this.failure = failure;
	}

	@JsonProperty("canonical_ids")
	public long getCanonicalIds() {
		return canonicalIds;
	}

	public void setCanonicalIds(long canonicalIds) {
		this.canonicalIds = canonicalIds;
	}

	@JsonProperty("results")
	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}
}
