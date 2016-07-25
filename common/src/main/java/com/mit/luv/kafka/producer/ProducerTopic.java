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

	public static final String NEW_COLOR = "dmau_new_color";
	public static final String VIEW_NEWS = "dmau_news_view";
    
}
