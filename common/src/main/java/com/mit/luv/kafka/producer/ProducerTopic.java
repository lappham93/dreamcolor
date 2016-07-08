package com.mit.luv.kafka.producer;

public class ProducerTopic {
	public static final String SEND_SMS = "cyog_sms_send";
	public static final String DELIVER_SMS = "cyog_sms_deliver";
	
	public static final String SEND_VOICE = "cyog_voice_send";
	public static final String DELIVER_VOICE_STATUS = "cyog_voice_status_deliver";
	
	public static final String SEND_EMAIL = "cyog_email_send";
    public static final String SEND_EMAIL_ORDER = "cyog_email_order";
    //public static final String SEND_EMAIL_REGISTER = "cyog_email_register";
    //public static final String SEND_EMAIL_RESET_PASS = "cyog_email_reset_pass";

	public static final String NOTIFICATION_ANDROID = "cyog_notif_android";
	public static final String NOTIFICATION_IOS = "cyog_notif_ios";

	public static final String NOTIFICATION_ANDROID_MULTI_DEST = "cyog_notif_android_multi_dest";
	public static final String NOTIFICATION_IOS_MULTI_DEST = "cyog_notif_ios_multi_dest";

	public static final String UPLOAD_CHAT = "cyog_chat_upload";
	public static final String ADD_LOCATION = "cyog_biz_location";

	public static final String UPLOAD_AVATAR_FROM_URL = "cyog_avt_url_upload";

	public static final String UPDATE_USERINFO = "cyog_update_info";
	public static final String UPDATE_USERLOCATION = "cyog_update_user_location";	

	public static final String USER_REGISTER = "cyog_user_register";

	public static final String PROMOTION_NEW= "cyog_promotion_addnew";
	public static final String PROMOTION_UPDATE_VIEW = "cyog_promotion_update_view";
	public static final String PROMOTION_UPDATE_LIKE = "cyog_promotion_update_like";
	public static final String REC_PROMOTION_UPDATE_VIEW = "cyog_rec_promotion_update_view";
	public static final String FACEBOOK_PUBLISH_FEED = "cyog_facebook_publish_feed";
	public static final String PROMOTION_SHARE = "cyog_promotion_share";
	public static final String PROMOTION_UPDATE_USE = "cyog_promotion_update_use";
	public static final String BIZ_INIT = "cyog_biz_init";
	public static final String BIZ_UPDATE_INFO = "cyog_biz_update";
    
    public static final String UPLOAD_MMEDIA = "cyog_mmedia";
    

	public static final String SUGGESTER_PROMOTION_UPDATE = "cyog_suggester_promotion_update";
	
	public static final String PRODUCT_VIEW = "cyog_product_view";
	public static final String PRODUCT_LIKE = "cyog_product_like";
	public static final String PRODUCT_WISH = "cyog_product_wish";
	public static final String PRODUCT_SHARE = "cyog_product_share";
	public static final String PRODUCT_ORDER = "cyog_product_order";
	public static final String PRODUCT_ADD_CART = "cyog_product_addcart";
	
	public static final String NEWS_VIEW = "cyog_news_view";
	public static final String NEWS_NOTIFY = "cyog_news_product";
    public static final String NEWS_WEB = "cyog_news_web";
	public static final String ORDER_SEND_SE = "cyog_order_send_se";
    
}
