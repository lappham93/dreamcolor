package com.mit.entities.color;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mit.dao.color.ColorDAO;
import com.mit.entities.photo.PhotoType;
import com.mit.entities.photo.PhotoView;

public class Category {
	private static final int ACTIVE = 1;

	private int id;
	private String name;
	private String description;
	private long photo;
	private int status;
	private long createTime;
	private long updateTime;

	public Category(int id, String name, String description, long photo) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.photo = photo;
		this.status = ACTIVE;
	}

	public Category(int id, String name, String description, long photo, int status, long createTime, long updateTime) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.photo = photo;
		this.status = status;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIgnore
	public long getPhotoNum() {
		return photo;
	}

	public void setPhoto(long photo) {
		this.photo = photo;
	}

	public PhotoView getPhoto() {
		return new PhotoView(photo, PhotoType.COLOR.getValue());
	}
	
	public int getColors() {
		return ColorDAO.getInstance().totalAll(this.id);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

}
