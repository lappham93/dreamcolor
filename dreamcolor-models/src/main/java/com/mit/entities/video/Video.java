package com.mit.entities.video;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mit.utils.LinkBuilder;

public class Video {
	private static final int ACTIVE = 1;
	
	private long id;
	private String link;
	private String site;
	private String title;
	private String desc;
	private long thumbnail;
	private int views;
	private int status;
	private long createTime;
	private long updateTime;
	
	public Video(long id, String link, String site, String title, String description, long thumbnail) {
		super();
		this.id = id;
		this.link = link;
		this.site = site;
		this.title = title;
		this.desc = description;
		this.thumbnail = thumbnail;
		this.views = 0;
		this.status = ACTIVE;
	}

	public Video(long id, String link, String site, String title, String description, long thumbnail, int views,
			int status, long createTime, long updateTime) {
		super();
		this.id = id;
		this.link = link;
		this.site = site;
		this.title = title;
		this.desc = description;
		this.thumbnail = thumbnail;
		this.views = views;
		this.status = status;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String description) {
		this.desc = description;
	}

	@JsonIgnore
	public long getThumbnailNum() {
		return thumbnail;
	}
	
	public String getThumbnail() {
		return LinkBuilder.buildVideoThumbLink(thumbnail);
	}

	public void setThumbnail(long thumbnail) {
		this.thumbnail = thumbnail;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
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
