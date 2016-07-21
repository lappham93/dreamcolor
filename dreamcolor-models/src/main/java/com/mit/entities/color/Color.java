package com.mit.entities.color;

import java.util.ArrayList;
import java.util.List;

import com.mit.entities.photo.PhotoType;
import com.mit.entities.photo.PhotoView;

public class Color {
	private static final int ACTIVE = 1;
	public static final int VIEWS_OF_FEATURE_DEFAUTL = 30;

	private long id;
	private int categoryId;
	private String code;
	private int views;
	private long photo;
	private boolean isFeature;
	private int status;
	private long createTime;
	private long updateTime;

	public Color(long id, int categoryId, String code, long photo, boolean isFeature) {
		this.id = id;
		this.categoryId = categoryId;
		this.code = code;
		this.views = 0;
		this.photo = photo;
		this.isFeature = isFeature;
		this.status = ACTIVE;
	}

	public Color(long id, int categoryId, String code, int views, long photo, boolean isFeature, int status, long createTime,
			long updateTime) {
		this.id = id;
		this.categoryId = categoryId;
		this.code = code;
		this.views = views;
		this.photo = photo;
		this.isFeature = isFeature;
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
	
	public boolean getIsFeature() {
		return isFeature;
	}
	
	public void setIsFeature(boolean isFeature) {
		this.isFeature = isFeature;
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
		private boolean isFeature;
		private PhotoView photo;
		private long createTime;
		private long updateTime;

		public ColorView(Color color) {
			id = color.getId();
			categoryId = color.getCategoryId();
			code = color.getCode();
			views = color.getViews();
			isFeature = color.getIsFeature();
			photo = new PhotoView(color.getPhoto(), PhotoType.COLOR.getValue());
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

		public PhotoView getPhoto() {
			return photo;
		}

		public void setPhoto(PhotoView photo) {
			this.photo = photo;
		}
		
		public boolean getIsFeature() {
			return isFeature;
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
