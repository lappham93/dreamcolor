package com.mit.api;

public enum ApiError {
	SUCCESS(0), UNKNOWN(-1), UNSUPPORT(-2),
	API_INVALID(-100), PARAMS_INVALID(-101),DATA_INVALID(-102), SESSION_TIMEOUT(-103), REQUEST_TIMEOUT(-104), UNACCESSABLE(-105),
	ACTIVE_INVALID(-110), DUPLICATE_USERNAME(-111), ERR_IMEI(-112), INVALID_LOGIN(-113), EXIST_IMEI(-114),
	ERR_PASSWORD(-115), ERR_REPEAT_PASSWORD(-116), PASSWORD_INVALID(-117), PASSWORD_WEAK(-118), DUPLICATE_SENDINFO(-119),
	RESTRICT_AUTO_LOGIN(-120), PHONE_NOT_EXIST(-121), TOKEN_INVALID(-122),
	NOT_ACCEPT_LICENSE(-125), PASSWORD_EXPIRE(-129),
	LIMIT_ACCESS(-130),UPLOAD_LIMIT(-131), PHONE_FORMAT_INVALID(-132),PART_EXIST(-133), DATA_ERROR(-134), WAITING_APPROVAL(-135), DECLINED(-136),

	NO_BIZ(-137), LIMIT_BIZ(-138), SERVICE_NOT_EXIST(-139), LIMIT_FOLLOW(-140), REF_BIZ_REGISTERED(-141), NAME_DUPLICATE(-142), NO_PHONE(-143),
	
	LIMIT_ADDRESS(-150), DISCOUNT_CODE_INVALID(-151), EMPTY_CART(-152), SHIPPING_ADDRESS_NOT_EXIST(-153),
	SHIPPING_OPTION_NOT_EXIST(-154), BILLING_ADDRESS_NOT_EXIST(-155), INVALID_PAYMENT_OPTION(-156),
	INVALID_CARD_NUMBER(-158), INVALID_CARD_CODE(-157), INVALID_EXPIRATION_DATE(-159), CANT_USE_POINT(-160), INVALID_CHECKOUT_TOKEN(-161),
	PAYMENT_DECLINED(-162), PAYMENT_ERROR(-163), INVALID_PROMO_CODE(-164), COD_ONLY(-165),

	PROMOTION_USED(-500), PROMOTION_EXPIRED(-501), DENY_DELETE(-502), QR_CODE_EXIPRED(-503), LIMIT_SKU(-504), LIMIT_QUANTITY(-505),

	MISSING_PARAM(-200), ITEM_NOT_FOUND(-201),BANNED(-202), CHECKIN_SOFAR(-203),
	ZIP_CODE_INVALID(-400),
	COLOR_NOT_EXIST(-701), PRODUCT_NOT_EXIST(-702), VIDEO_NOT_EXIST(-703), ALREADY_SUBMIT(-704);

	private int value;

	private ApiError(int val) {
		value = val;
	}

	public int getValue() {
		return value;
	}
}
