package com.mit.worker.worker;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.constants.PromotionStatsFactor;
import com.mit.dao.biz.BizInfoDAO;
import com.mit.dao.facebook.FacebookUserDAO;
import com.mit.dao.mid.MIdGenDAO;
import com.mit.dao.notification.RecPromotionDAO;
import com.mit.dao.promotion.PromotionStatsDAO;
import com.mit.dao.promotion.PromotionTargetDAO;
import com.mit.dao.promotion.PromotionUserDAO;
import com.mit.dao.promotion.UserFavouriteDAO;
import com.mit.dao.service.BizCategoryDAO;
import com.mit.dao.service.BizSubCategoryDAO;
import com.mit.dao.service.ServiceCategoryDAO;
import com.mit.dao.service.ServiceMenuDAO;
import com.mit.entities.biz.BizInfo;
import com.mit.entities.facebook.FBUser;
import com.mit.entities.promotion.Promotion;
import com.mit.entities.promotion.PromotionContent;
import com.mit.entities.promotion.PromotionLocation;
import com.mit.entities.promotion.PromotionStats;
import com.mit.entities.promotion.PromotionUser;
import com.mit.es.PromotionController;
import com.mit.es.PromotionSuggester;
import com.mit.models.FacebookModel;
import com.mit.promotion.utils.PromotionNoise;
import com.mit.qr.thrift.QRData;
import com.mit.qr.thrift.QRItem;
import com.mit.qr.thrift.QRType;
import com.mit.qr.thrift.wrapper.QRReadServiceClient;
import com.mit.qr.thrift.wrapper.QRWriteServiceClient;
import com.mit.utils.ConfigUtils;
import com.mit.utils.JsonUtils;

public class PromotionWorker {
	private static final Logger _logger = LoggerFactory.getLogger(PromotionWorker.class);

	private static final String _defaultAvatar = ConfigUtils.getConfig().getString("avatar.default");

	private static final String _publicDomain = ConfigUtils.getConfig().getString("domain.public");
	private static final String _staticUri = ConfigUtils.getConfig().getString("uri.static");
	public static final List<Integer> _DoBNotification = Arrays.asList(14, 10, 7, 5, 3, 2, 1);


	public static void publishPromotion(int promotionId) {
		Promotion promotion = PromotionTargetDAO.getInstance().getById(promotionId);
		if(promotion != null) {
			publishPromotion(promotion);
		}
	}

	public static void publishPromotion(Promotion promotion) {
		if (promotion == null) {
			return;
		}
		
		PromotionContent content = JsonUtils.Instance.getObject(PromotionContent.class, promotion.getContent());
		List<Integer> serviceIds = content.getServices();
		List<Integer> categoryIds;
		if (serviceIds.size() == 0) {
			categoryIds = ServiceMenuDAO.getInstance().getCategoryByBizId(promotion.getBizId());
		} else {
			categoryIds = ServiceMenuDAO.getInstance().getCategoryByListId(serviceIds);
		}
		List<Integer> bizSubCategoryIds = ServiceCategoryDAO.getInstance().getTypeByListId(categoryIds);
		List<Integer> bizCategoryIds = BizSubCategoryDAO.getInstance().getBizCategoryByListId(bizSubCategoryIds);
		List<Integer> bizTypeIds = BizCategoryDAO.getInstance().getBizTypeByListId(bizCategoryIds);
		
		BizInfo biz = BizInfoDAO.getInstance().getById(promotion.getBizId());
		
		List<String> serviceNames;
		if (content.getServices().size() == 0) {
			serviceNames = ServiceMenuDAO.getInstance().getListNameByBizId(promotion.getBizId());
		} else {
			serviceNames = ServiceMenuDAO.getInstance().getListNameByListId(content.getServices());
		}
		
		double statsScore = 0;
		List<PromotionStats> promoStatses = PromotionStatsDAO.getInstance().getByPromotionId(promotion.getId(), Arrays.asList(PromotionStats.VIEW, PromotionStats.LIKE, PromotionStats.USED));

		for(PromotionStats stats : promoStatses) {
			statsScore += PromotionStatsFactor.getInstance().getFactor(stats.getType()) * stats.getCount();
		}
		
		PromotionLocation promoLoc = new PromotionLocation();
		promoLoc.setId(promotion.getId());
		promoLoc.setServiceNames(serviceNames);
		promoLoc.setBizSubCategories(bizSubCategoryIds);
		promoLoc.setBizCategories(bizCategoryIds);
		promoLoc.setBizTypes(bizTypeIds);
		if (biz != null) {
			promoLoc.setBizName(biz.getName());
			promoLoc.setCity(biz.getCity().toLowerCase());
			promoLoc.setState(biz.getState().toLowerCase());
			promoLoc.setLat(biz.getLat());
			promoLoc.setLon(biz.getLon());
		} else {
			promoLoc.setBizName("");
			promoLoc.setCity("");
			promoLoc.setState("");
		}
		promoLoc.setExpireDate(promotion.getExpireDate().getTime());
		promoLoc.setCreateTime(promotion.getCreateTime());
		promoLoc.setStats(statsScore);
		PromotionController.Instance.index(promoLoc);
		
		if (biz != null) {
			serviceNames.add(biz.getName());
		}
		PromotionSuggester.Instance.bulk(serviceNames);

//		PromotionUser proCus = new PromotionUser(promotion.getId(), userId, promotion.getExpireDate(), status, 0);
//		int rs = PromotionUserDAO.getInstance().updateStatus(promotion.getId(), userId,status);
	}

	public static void promotionView(int userId, int promotionId) {
//		int rs = PromotionUserDAO.getInstance().updateStatus(promotionId, userId, PromotionUser.IS_VIEW);
//		if(rs > 0) {
			PromotionStats stats = new PromotionStats(promotionId, PromotionStats.VIEW, 1);
			PromotionStatsDAO.getInstance().insert(stats);
			PromotionController.Instance.updateStats(promotionId, 0.25);
//		}
	}

	public static void promotionLike(int userId, int promotionId) {
//		int rs = PromotionUserDAO.getInstance().updateStatus(promotionId, userId, PromotionUser.IS_LIKED);
//		if(rs > 0) {
			PromotionStats stats = new PromotionStats(promotionId, PromotionStats.LIKE, 1);
			PromotionStatsDAO.getInstance().insert(stats);
			PromotionController.Instance.updateStats(promotionId, 0.5);
//			Date expireDate = PromotionTargetDAO.getInstance().getExpireDateById(promotionId);
//			UserFavouriteDAO.getInstance().addPromotion(userId, promotionId, expireDate);
//		}
	}

	public static void promotionUnLike(int userId, int promotionId) {
//		int rs = PromotionUserDAO.getInstance().removeStatus(promotionId, userId, PromotionUser.IS_LIKED);
//		if(rs > 0) {
			PromotionStats stats = new PromotionStats(promotionId, PromotionStats.LIKE, -1);
			PromotionStatsDAO.getInstance().updateCounter(stats);
			PromotionController.Instance.updateStats(promotionId, -0.5);
			UserFavouriteDAO.getInstance().removePromotion(userId, promotionId);
//		}
	}

	public static void promotionUse(int userId, int promotionId) {
//		int rs = PromotionUserDAO.getInstance().updateStatus(promotionId, userId, PromotionUser.IS_USED);
//		if(rs > 0) {
			PromotionStats stats = new PromotionStats(promotionId, PromotionStats.USED, 1);
			PromotionStatsDAO.getInstance().insert(stats);
			PromotionController.Instance.updateStats(promotionId, 1);
//		}
	}
	
	public static void sharePromotion(int userId, int promotionId, int bizId, int templateId,
			String message, int platformId) {
		String encryptId = PromotionNoise.encrypt(promotionId);
		int avtVer = BizInfoDAO.getInstance().getAvtVerById(bizId);
		String link = _publicDomain + "/loop/img/promo/share/" + encryptId + "/" + avtVer;

//		String prTemplateFile = "";
//		if (templateId > 0) {
//			PromotionTemplate prTemplate = PromotionTemplateDAO.getInstance().getById(templateId);
//			if(prTemplate != null) {
//				prTemplateFile = "1440/" + prTemplate.getImage();
//			}
//		}
		QRItem item = new QRItem("", promotionId, "", QRType.PROMOTION_SHARE, link, templateId + "", bizId + "/" + avtVer);
		String qrId = QRWriteServiceClient.Instance.genShopQR(item);
		QRData qrData = QRReadServiceClient.Instance.getData(qrId);

		if (platformId == 1) {
			FBUser fbUser = FacebookUserDAO.getInstance().getByUserId(userId);

			if (fbUser != null) {
				String result = FacebookModel.Instance.publishFeed(fbUser.getFacebookId(),
						message, link);

				_logger.debug("Shared link " + link + " on " + fbUser.getFacebookId());

				int rs = PromotionUserDAO.getInstance().updateStatus(promotionId, userId, PromotionUser.IS_SHARED);
				if(rs > 0) {
					PromotionStats stats = new PromotionStats(promotionId, PromotionStats.FB_SHARE_USER, 1);
					PromotionStatsDAO.getInstance().insert(stats);
				}

				PromotionStats stats = new PromotionStats(promotionId, PromotionStats.FB_SHARE_BY_APP, 1);
				PromotionStatsDAO.getInstance().insert(stats);
			}
		}
	}
	
	public static void recPromotionView(int userId, List<Integer> promotionIds) {
		RecPromotionDAO.getInstance().updateView(userId, promotionIds);
	}
	
	public static void main(String[] args) {
//		PromotionController.Instance.create();
		PromotionSuggester.Instance.create();
		for (int i = 1; i <= MIdGenDAO.getInstance(PromotionTargetDAO.getInstance().TABLE_NAME).getMaxId(); i++) {
			publishPromotion(i);
		}
	}
}
