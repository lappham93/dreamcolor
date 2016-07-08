package com.mit.entities.photo;

public class PhotoInfo {
	private long id;
	private long refId;
	private int type;
	private int height;
	private int width;
	
	public PhotoInfo(long id, long refId, int type, int height, int width) {
		super();
		this.id = id;
		this.refId = refId;
		this.type = type;
		this.height = height;
		this.width = width;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRefId() {
		return refId;
	}

	public void setRefId(long refId) {
		this.refId = refId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
}
