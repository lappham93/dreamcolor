package com.mit.entities.product;

import java.util.ArrayList;
import java.util.List;

import com.mit.utils.LinkBuilder;


public class Product {
	public static final int ACTIVE = 1;
	
	private long id;
	private int categoryId;
	private String manufacturer;
	private String model;
	private String name;
	private String desc;
	private long primaryPhoto;
	private List<Long> photos;
	private int views;
	private int status;
	private long createTime;
	private long updateTime;
	
	public Product() {
		super();
	}
	
	public Product(long id, int categoryId, String manufacturer, String model, String name, String desc, long primaryPhoto, List<Long> photos) {
		this.id = id;
		this.categoryId = categoryId;
		this.manufacturer = manufacturer;
		this.model = model;
		this.name = name;
		this.desc = desc;
		this.primaryPhoto = primaryPhoto;
		this.photos = photos;
		this.views = 0;
		this.status = ACTIVE;
	}

	public Product(long id, int categoryId, String manufacturer, String model, String name, String desc, long primaryPhoto, List<Long> photos,
			int views, int status, long createTime, long updateTime) {
		super();
		this.id = id;
		this.categoryId = categoryId;
		this.manufacturer = manufacturer;
		this.model = model;
		this.name = name;
		this.desc = desc;
		this.primaryPhoto = primaryPhoto;
		this.photos = photos;
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

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getPrimaryPhoto() {
		return primaryPhoto;
	}

	public void setPrimaryPhoto(long primaryPhoto) {
		this.primaryPhoto = primaryPhoto;
	}

	public List<Long> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Long> photos) {
		this.photos = photos;
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
	
	public ProductViewBasic buildProductViewBasic() {
		return new ProductViewBasic(this);
	}
	
	public static List<ProductViewBasic> buildListProductViewBasic(List<Product> pros) {
		List<ProductViewBasic> rs = new ArrayList<ProductViewBasic>();
		if (pros != null && pros.size() > 0) {
			for (Product pro : pros) {
				rs.add(pro.buildProductViewBasic());
			}
		}
		
		return rs;
	}
	
	public ProductViewDetail buildProductViewDetail() {
		return new ProductViewDetail(this);
	}
	
//	public static List<ProductViewDetail> buildListProductViewDetail(List<Product> pros) {
//		List<ProductViewDetail> rs = new ArrayList<ProductViewDetail>();
//		if (pros != null && pros.size() > 0) {
//			for (Product pro : pros) {
//				rs.add(pro.buildProductViewDetail());
//			}
//		}
//		
//		return rs;
//	}
	
	public class ProductViewBasic {
		private long id;
		private String manufacturer;
		private String model;
		private String name;
		private String desc;
		private String primaryPhoto;
		private int views;
		
		public ProductViewBasic(Product pro) {
			id = pro.getId();
			manufacturer = pro.getManufacturer();
			model = pro.getModel();
			name = pro.getName();
			desc = pro.getDesc();
			primaryPhoto = LinkBuilder.buildProductPhotoLink(pro.getPrimaryPhoto());
			views = pro.getViews();
		}

		public long getId() {
			return id;
		}

		public String getManufacturer() {
			return manufacturer;
		}

		public String getModel() {
			return model;
		}

		public String getName() {
			return name;
		}

		public String getDesc() {
			return desc;
		}

		public String getPrimaryPhoto() {
			return primaryPhoto;
		}

		public int getViews() {
			return views;
		}
		
	}
	
	public class ProductViewDetail {
		private long id;
		private int categoryId;
		private String manufacturer;
		private String model;
		private String name;
		private String desc;
		private String primaryPhoto;
		private List<String> photos;
		private int views;
		private long createTime;
		private long updateTime;
		
		public ProductViewDetail(Product pro) {
			id = pro.getId();
			categoryId = pro.getCategoryId();
			manufacturer = pro.getManufacturer();
			model = pro.getModel();
			name = pro.getName();
			desc = pro.getDesc();
			primaryPhoto = LinkBuilder.buildProductPhotoLink(pro.getId());
			if (pro.getPhotos() != null && pro.getPhotos().size() > 0) {
				photos = new ArrayList<String>();
				for (long photoId : pro.getPhotos()) {
					photos.add(LinkBuilder.buildProductPhotoLink(photoId));
				}
			}
			views = pro.getViews();
			createTime = pro.getCreateTime();
			updateTime = pro.getUpdateTime();
		}

		public long getId() {
			return id;
		}

		public int getCategoryId() {
			return categoryId;
		}

		public String getManufacturer() {
			return manufacturer;
		}

		public String getModel() {
			return model;
		}

		public String getName() {
			return name;
		}

		public String getDesc() {
			return desc;
		}

		public String getPrimaryPhoto() {
			return primaryPhoto;
		}

		public List<String> getPhotos() {
			return photos;
		}
		
		public int getViews() {
			return views;
		}

		public long getCreateTime() {
			return createTime;
		}

		public long getUpdateTime() {
			return updateTime;
		}
		
	}
}
