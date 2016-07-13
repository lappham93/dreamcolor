package com.mit.luv.kafka.producer;

public class ProducerTopic {
	public static final String SEND_SMS = "dmau_sms_send";
	public static final String DELIVER_SMS = "dmau_sms_deliver";
	
	public static final String SEND_VOICE = "dmau_voice_send";
	public static final String DELIVER_VOICE_STATUS = "dmau_voice_status_deliver";
	
	public static final String SEND_EMAIL = "dmau_email_send";
    public static final String SEND_EMAIL_ORDER = "dmau_email_order";
    //public static final String SEND_EMAIL_REGISTER = "dmau_email_register";
    //public static final String SEND_EMAIL_RESET_PASS = "dmau_email_reset_pass";

	public static final String NOTIFICATION_ANDROID = "dmau_notif_android";
	public static final String NOTIFICATION_IOS = "dmau_notif_ios";

	public static final String NOTIFICATION_ANDROID_MULTI_DEST = "dmau_notif_android_multi_dest";
	public static final String NOTIFICATION_IOS_MULTI_DEST = "dmau_notif_ios_multi_dest";

	public static final String UPLOAD_CHAT = "dmau_chat_upload";
	public static final String ADD_LOCATION = "dmau_biz_location";

	public static final String UPLOAD_AVATAR_FROM_URL = "dmau_avt_url_upload";

	public static final String UPDATE_USERINFO = "dmau_update_info";
	public static final String UPDATE_USERLOCATION = "dmau_update_user_location";	

	public static final String USER_REGISTER = "dmau_user_register";

	public static final String PROMOTION_NEW= "dmau_promotion_addnew";
	public static final String PROMOTION_UPDATE_VIEW = "dmau_promotion_update_view";
	public static final String PROMOTION_UPDATE_LIKE = "dmau_promotion_update_like";
	public static final String REC_PROMOTION_UPDATE_VIEW = "dmau_rec_promotion_update_view";
	public static final String FACEBOOK_PUBLISH_FEED = "dmau_facebook_publish_feed";
	public static final String PROMOTION_SHARE = "dmau_promotion_share";
	public static final String PROMOTION_UPDATE_USE = "dmau_promotion_update_use";
	public static final String BIZ_INIT = "dmau_biz_init";
	public static final String BIZ_UPDATE_INFO = "dmau_biz_update";
    
    public static final String UPLOAD_MMEDIA = "dmau_mmedia";
    

	public static final String SUGGESTER_PROMOTION_UPDATE = "dmau_suggester_promotion_update";
	
	public static final String PRODUCT_VIEW = "dmau_product_view";
	public static final String PRODUCT_LIKE = "dmau_product_like";
	public static final String PRODUCT_WISH = "dmau_product_wish";
	public static final String PRODUCT_SHARE = "dmau_product_share";
	public static final String PRODUCT_ORDER = "dmau_product_order";
	public static final String PRODUCT_ADD_CART = "dmau_product_addcart";
	
	public static final String NEWS_VIEW = "dmau_news_view";
	public static final String NEWS_NOTIFY = "dmau_news_product";
    public static final String NEWS_WEB = "dmau_news_web";
	public static final String ORDER_SEND_SE = "dmau_order_send_se";
    
}
