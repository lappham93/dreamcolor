package com.mit.utils;

import com.mit.common.conts.Common;
import com.mit.midutil.MIdNoise;


public class LinkBuilder {
	public static final String _default = "http://usavall.com/statics/images/default-user.png";
	public static final String _defaultAvatar = "http://usavall.com/statics/images/default-user.png";
	public static final String _defaultBizAvatar = "http://usavall.com/statics/images/default-biz.png";
	public static final String _defaultCover = "http://usavall.com/statics/images/default-cover.jpg";
	public static final String _staticDomain = ConfigUtils.getConfig().getString("static.domain", "dev.spakonect.com");
	
	public static String buildColorPhotoLink(long id) {
		String link = _default;
		if (id > 0) {
			String idNoise = MIdNoise.enNoiseLId(id);
			link = Common.DOMAIN_FILE + "/dreamau/load/color/photo?p=" + idNoise;
		}
		return link;
	}
	
	public static String buildProductPhotoLink(long id) {
    	String link = _default;
    	if (id > 0) {
			String idNoise = MIdNoise.enNoiseLId(id);
	        link = Common.DOMAIN_FILE + "/dreamau/load/pro/photo?p=" + idNoise;
    	}
    	return link;
	}
	
	public static String buildDistributorPhotoLink(long id) {
    	String link = _default;
    	if (id > 0) {
			String idNoise = MIdNoise.enNoiseLId(id);
	        link = Common.DOMAIN_FILE + "/dreamau/load/distributor/photo?p=" + idNoise;
    	}
    	return link;
	}
	
	public static String buildBannerThumbLink(long id) {
    	String link = "https://api.cyogel.com/statics/welcom_cyogel@2x.png";
    	if (id > 0) {
			String idNoise = MIdNoise.enNoiseLId(id);
	        link = Common.DOMAIN_FILE + "/dreamau/load/bn/photo?p=" + idNoise;
    	}
    	return link;
	}
	 
	public static String buildVideoThumbLink(long id) {
    	String link = "https://api.cyogel.com/statics/welcom_cyogel@2x.png";
    	if (id > 0) {
			String idNoise = MIdNoise.enNoiseLId(id);
	        link = Common.DOMAIN_FILE + "/dreamau/load/video/photo?p=" + idNoise;
    	}
    	return link;
	}

	public static String buildAvatarLink(int uid, int avtVer, long tmpId, int size) {
		//String noiseString = NoiseIdUtils.encryptString(System.currentTimeMillis() + "");
		String link = _defaultAvatar;
		if(avtVer > 0) {
			String noiseData = NoiseIdUtils.encryptString(uid + "," + avtVer + "," + tmpId);
			link = "http://" + _staticDomain + "/cyog/img/avt/" + noiseData;
		}
		return link;
	}
	
	public static String buildBizAvatarLink(int uid, int avtVer, long tmpId, int size) {
		//String noiseString = NoiseIdUtils.encryptString(System.currentTimeMillis() + "");
		String link = _defaultBizAvatar;
		if(avtVer > 0) {
			String noiseData = NoiseIdUtils.encryptString(uid + "," + avtVer + "," + tmpId);
			link = "http://" + _staticDomain + "/cyog/img/avt/" + noiseData;
		}
		return link;
	}

	public static String buildCoverLink(int uid, int covVer, long tmpId, int size) {
		//String noiseString = NoiseIdUtils.encryptString(System.currentTimeMillis() + "");
		String link = _defaultCover;
		if(covVer > 0) {
			String noiseData = NoiseIdUtils.encryptString(uid + "," + covVer + "," + tmpId);
			link = "http://" + _staticDomain + "/cyog/img/cov/" + noiseData;
		}
		return link;
	}

	public static String buildChatLink(int uid, long tmpId, int size) {
		//String noiseString = NoiseIdUtils.encryptString(System.currentTimeMillis() + "");
		String noiseData = NoiseIdUtils.encryptString(tmpId + "," + uid + "," + System.currentTimeMillis());
		String link = "http://" + _staticDomain + "/cyog/img/c/" + noiseData;

		return link;
	}

	public static String buildVTemplateLink(int id, int tmlVer, int size) {
		String noiseData = NoiseIdUtils.encryptString(id + "," + tmlVer);
		String link = "http://" + _staticDomain + "/cyog/img/tml/v/" + noiseData;
		return link;
	}

	public static String buildHTemplateLink(int id, int tmlVer, int size) {
		String noiseData = NoiseIdUtils.encryptString(id + "," + tmlVer);
		String link = "http://" + _staticDomain + "/cyog/img/tml/h/" + noiseData;
		return link;
	}
	
	public static String buildVideoLink(long id) {
		String idNoise = MIdNoise.enNoiseLId(id);
        return Common.DOMAIN_APP + Common.MEDIA_LOAD_SERV_PATH + "?v=" + idNoise;
	}
    
    public static String buildPhotoLink(long id) {
    	String link = "https://cyogel.com/cyogel/cmsstatic/product/10400/Gel%20Base%20%202oz.jpg";
    	if (id > 0) {
			String idNoise = MIdNoise.enNoiseLId(id);
	        link = Common.DOMAIN_APP + Common.PHOTO_LOAD_SERV_PATH + "?p=" + idNoise;
    	}
    	return link;
	}
    
    public static String buildUserPhotoLink(long id) {
    	String link = "";
    	if (id > 0) {
			String idNoise = MIdNoise.enNoiseLId(id);
	        link = Common.DOMAIN_FILE + "/cyog/load/user/photo?p=" + idNoise;
    	}
    	return link;
	}
    
    public static String buildNewsThumbLink(long id) {
    	String link = "https://api.cyogel.com/statics/welcom_cyogel@2x.png";
    	if (id > 0) {
			String idNoise = MIdNoise.enNoiseLId(id);
	        link = Common.DOMAIN_FILE + "/cyog/load/bn/photo?p=" + idNoise;
    	}
    	return link;
	}
    
    public static String buildSaleRepPhotoLink(long id) {
    	String link = "https://api.cyogel.com/statics/icon.png";
    	if (id > 0) {
			String idNoise = MIdNoise.enNoiseLId(id);
	        link = Common.DOMAIN_FILE + "/cyog/load/user/photo?p=" + idNoise;
    	}
    	return link;
	}
    
    public static String buildStreamMediaLink(long id){
        String v = MIdNoise.enNoiseLId(id);
        return Common.DOMAIN_APP + ":" + Common.PORT_RTMP + "/media_server/" + v + "/" + v + ".m3u8";
    }
    
    public static String buildProductShareLink(long productId, long skuId) {
    	String productIdNoise = MIdNoise.enNoiseLId(productId);
    	String skuIdNoise = MIdNoise.enNoiseLId(skuId);
    	return Common.DOMAIN_FILE + "/cyog/load/product/share?pid=" + productIdNoise + "&sid=" + skuIdNoise;
    }
    
    public static String buildEmailPhotoLink(String namePhoto) {
    	String link = "";
    	if (namePhoto  != null && !namePhoto.isEmpty()) {
	        link = Common.DOMAIN_STATIC_ADMIN + "/pdb/email/"+ namePhoto;
    	}
    	return link;
	}
    
    public static String buildEmailLink(long id) {
    	String link = "";
    	if (id > 0) {
			String idNoise = MIdNoise.enNoiseLId(id);
	        link = Common.DOMAIN_ADMIN + "/web/admin/load/email?e=" + idNoise;
    	}
    	return link;
	}
    
}
