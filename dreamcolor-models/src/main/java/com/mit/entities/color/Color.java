package com.mit.entities.color;

import java.util.ArrayList;
import java.util.List;

import com.mit.utils.LinkBuilder;

public class Color {
	private static final int ACTIVE = 1;
	
	private long id;
	private int categoryId;
	private String code;
	private int views;
	private long photo;
	private int status;
	private long createTime;
	private long updateTime;
	
	public Color(long id, int categoryId, String code, long photo) {
		this.id = id;
		this.categoryId = categoryId;
		this.code = code;
		this.views = 0;
		this.photo = photo;
		this.status = ACTIVE;
	}
	
	public Color(long id, int categoryId, String code, int views, long photo, int status, long createTime, long updateTime) {
		this.id = id;
		this.categoryId = categoryId;
		this.code = code;
		this.views = views;
		this.photo = photo;
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

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}
	
	public long getPhoto() {
		return photo;
	}
	
	public void setPhoto(long photo) {
		this.photo = photo;
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
	
	public static List<ColorView> buildListColorView(List<Color> colors) {
		List<ColorView> colorViews = new ArrayList<ColorView>();
		if (colors != null && colors.size() > 0) {
			for (Color color : colors) {
				colorViews.add(color.buildColorView());
			}
		}
		
		return colorViews;
	}
	
	public ColorView buildColorView() {
		return new ColorView(this);
	}
	
 	public class ColorView {
		private long id;
		private int categoryId;
		private String code;
		private int views;
		private String photo;
		private long createTime;
		private long updateTime;
		
		public ColorView(Color color) {
			id = color.getId();
			categoryId = color.getCategoryId();
			code = color.getCode();
			views = color.getViews();
			photo = LinkBuilder.buildColorPhotoLink(color.getId());
			createTime = color.getCreateTime();
			updateTime = color.getUpdateTime();
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public int getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(int categoryId) {
			this.categoryId = categoryId;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public int getViews() {
			return views;
		}

		public void setViews(int views) {
			this.views = views;
		}

		public String getPhoto() {
			return photo;
		}

		public void setPhoto(String photo) {
			this.photo = photo;
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
	
}
