package com.mit.entities.photo;

public enum PhotoType {
	COLOR(1),
	PRODUCT(2),
	BANNER(3),
	VIDEO_THUMBNAIL(4),
	DISTRIBUTOR(5);
	
	private int value;
	
	private PhotoType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static PhotoType getPhotoType(int value) {
		for (PhotoType p : PhotoType.values()) {
			if (p.getValue() == value) {
				return p;
			}
		}
		
		return null;
	}

}
