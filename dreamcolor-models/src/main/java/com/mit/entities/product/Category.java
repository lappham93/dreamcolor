package com.mit.entities.product;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Category {
	private static final int ACTIVE = 1;
	
	private int id;
	private String name;
	private String path;
	private int parentId;
	private int position;
	private int status;
	private long createTime;
	private long updateTime;
	
	public Category(int id, String name, String path, int parentId, int position) {
		this.id = id;
		this.name = name;
		this.path = path;
		this.parentId = parentId;
		this.position = position;
		this.status = ACTIVE;
	}
	
	public Category(int id, String name, String path, int parentId, int position, int status,
			long createTime, long updateTime) {
		super();
		this.id = id;
		this.name = name;
		this.path = path;
		this.parentId = parentId;
		this.position = position;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
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
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public JsonBuilder buildJson() {
		return new JsonBuilder(this);
	}
	
	public static class JsonBuilder {
		public static final int UNCHANGE = 0;
		public static final int CREATE = 1;
		public static final int UPDATE = 2;
		public static final int DELETE = 3;
		
		private int id;
		private String name;
		private String path;
		private List<Category.JsonBuilder> subCategories;
		private int position;
		private int updateType;
		private long createTime;
		private long updateTime;
		
		private JsonBuilder(Category category) {
			this.id = category.getId();
			this.name = category.getName();
			this.path = category.getPath();
			this.subCategories = new LinkedList<Category.JsonBuilder>();
			this.position = category.getPosition();
			this.createTime = category.getCreateTime();
			this.updateTime = category.getUpdateTime();
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		@JsonIgnore
		public String getPath() {
			return path;
		}

		public List<Category.JsonBuilder> getSubCategories() {
			return subCategories;
		}

		public Category.JsonBuilder setSubCategories(List<Category.JsonBuilder> subCategories) {
			this.subCategories = subCategories;
			return this;
		}

		public long getCreateTime() {
			return createTime;
		}

		public long getUpdateTime() {
			return updateTime;
		}
		
		public int getPosition() {
			return position;
		}

		public int getUpdateType() {
			return updateType;
		}

		public JsonBuilder setUpdateType(int updateType) {
			this.updateType = updateType;
			return this;
		}
	}

    @Override
    public String toString() {
        return "Category{" + "id=" + id + ", name=" + name + ", path=" + path + ", position=" + position + ", status=" + status + ", createTime=" + createTime + ", updateTime=" + updateTime + '}';
    }
    
    
}
