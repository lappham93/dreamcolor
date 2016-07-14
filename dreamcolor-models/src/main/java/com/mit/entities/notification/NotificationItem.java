package com.mit.entities.notification;

import java.util.List;

public class NotificationItem {
	private Object id;
	private int srcId;
	private int destId;
	private int appId;
	private int type;
	private String msg;
	private List<Long> objIds;
	private long createTime;

	public NotificationItem(Object id, int srcId, int destId, int appId, int type, String msg) {
		super();
		this.id = id;
		this.srcId = srcId;
		this.destId = destId;
		this.appId = appId;
		this.type = type;
		this.msg = msg;
		this.createTime = System.currentTimeMillis();
	}
	
	public NotificationItem(Object id, int srcId, int destId, int appId, int type, 
			List<Long> objIds, long createTime) {
		super();
		this.id = id;
		this.srcId = srcId;
		this.destId = destId;
		this.appId = appId;
		this.type = type;
		this.objIds = objIds;
		this.createTime = createTime;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	public int getSrcId() {
		return srcId;
	}

	public void setSrcId(int srcId) {
		this.srcId = srcId;
	}

	public int getDestId() {
		return destId;
	}

	public void setDestId(int destId) {
		this.destId = destId;
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<Long> getObjIds() {
		return objIds;
	}

	public void setObjIds(List<Long> objIds) {
		this.objIds = objIds;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
}
