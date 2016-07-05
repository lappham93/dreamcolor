package com.mit.entities.banner;

public enum BannerType {
	WEB(1), PRODUCT(2), VIDEO(3), WELCOME(4);
	
	private int value;
	
	private BannerType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
