package com.mit.worker.worker;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.facebook.FacebookUserDAO;
import com.mit.dao.product.ProductImeiDAO;
import com.mit.dao.product.ProductStatsDAO;
import com.mit.dao.product.ProductUserDAO;
import com.mit.dao.product.SKUImeiDAO;
import com.mit.dao.product.SKUStatsDAO;
import com.mit.dao.product.SKUUserDAO;
import com.mit.entities.facebook.FBUser;
import com.mit.entities.service.ProductImei;
import com.mit.entities.service.ProductStats;
import com.mit.entities.service.ProductUser;
import com.mit.entities.service.SKUImei;
import com.mit.entities.service.SKUStats;
import com.mit.entities.service.SKUUser;
import com.mit.models.FacebookModel;
import com.mit.utils.LinkBuilder;

public class ProductWorker {
	private static final Logger _logger = LoggerFactory.getLogger(ProductWorker.class);

	public static void productView(int userId, long productId, long skuId, String imei) {
		if (userId > 0) {
			int rs = SKUUserDAO.getInstance().updateStatus(skuId, userId, SKUUser.IS_VIEW);
			if (rs > 0) {
				SKUStats skuStats = new SKUStats(skuId);
				skuStats.setViewUserCount(1);
				skuStats.setViewCount(1);
				SKUStatsDAO.getInstance().increaseCount(skuStats);
			}
			
			rs = ProductUserDAO.getInstance().updateStatus(productId, userId, ProductUser.IS_VIEW);
			if (rs > 0) {
				ProductStats productStats = new ProductStats(productId);
				productStats.setViewUserCount(1);
				productStats.setViewCount(1);
				ProductStatsDAO.getInstance().increaseCount(productStats);
			}
		} else if (imei != null && !imei.isEmpty()) {
			int rs = SKUImeiDAO.getInstance().updateStatus(skuId, imei, SKUImei.IS_VIEW);
			if (rs > 0) {
				SKUStats skuStats = new SKUStats(skuId);
				skuStats.setViewImeiCount(1);
				skuStats.setViewCount(1);
				SKUStatsDAO.getInstance().increaseCount(skuStats);
			}
			
			rs = ProductImeiDAO.getInstance().updateStatus(productId, imei, ProductImei.IS_VIEW);
			if (rs > 0) {
				ProductStats productStats = new ProductStats(productId);
				productStats.setViewImeiCount(1);
				productStats.setViewCount(1);
				ProductStatsDAO.getInstance().increaseCount(productStats);
			}
		}
	}

	public static void productWish(int userId, long productId, long skuId) {
//		int rs = SKUUserDAO.getInstance().updateStatus(skuId, userId, SKUUser.IS_WISHED);
//		if (rs > 0) {
			SKUStats skuStats = new SKUStats(skuId);
			skuStats.setWishListCount(1);
			SKUStatsDAO.getInstance().increaseCount(skuStats);
//		}
		
		int rs = ProductUserDAO.getInstance().updateStatus(productId, userId, ProductUser.IS_WISHED);
		if (rs > 0) {
			ProductStats productStats = new ProductStats(productId);
			productStats.setWishListCount(1);
			ProductStatsDAO.getInstance().increaseCount(productStats);
		}
	}

	public static void productUnWish(int userId, long productId, long skuId) {
//		int rs = SKUUserDAO.getInstance().removeStatus(skuId, userId, SKUUser.IS_WISHED);
//		if (rs > 0) {
			SKUStats skuStats = new SKUStats(skuId);
			skuStats.setWishListCount(-1);
			SKUStatsDAO.getInstance().increaseCount(skuStats);
//		}
		
		int rs = ProductUserDAO.getInstance().removeStatus(productId, userId, ProductUser.IS_WISHED);
		if (rs > 0) {
			ProductStats productStats = new ProductStats(productId);
			productStats.setWishListCount(-1);
			ProductStatsDAO.getInstance().increaseCount(productStats);
		}
	}
	
	public static void productAddToCart(int userId, long productId, long skuId, long quantity) {
		SKUStats skuStats = new SKUStats(skuId);
		skuStats.setAddToCartCount(quantity);
		SKUStatsDAO.getInstance().increaseCount(skuStats);
		
		ProductStats productStats = new ProductStats(productId);
		productStats.setAddToCartCount(quantity);
		ProductStatsDAO.getInstance().increaseCount(productStats);
	}

	public static void productOrder(int userId, long productId, long skuId, long quantity) {
		SKUStats skuStats = new SKUStats(skuId);
		skuStats.setOrderCount(quantity);
		SKUStatsDAO.getInstance().increaseCount(skuStats);
		
		ProductStats productStats = new ProductStats(productId);
		productStats.setOrderCount(quantity);
		ProductStatsDAO.getInstance().increaseCount(productStats);
	}
	
	public static void productShare(int userId, long productId, long skuId, String message, int platformId, String facebookId) {
		String link = LinkBuilder.buildProductShareLink(productId, skuId);

		if (platformId == 1) {
			if ((facebookId == null || facebookId.isEmpty()) && userId > 0) {
				FBUser fbUser = FacebookUserDAO.getInstance().getByUserId(userId);
				if (fbUser != null) {
					facebookId = fbUser.getFacebookId();
				}
			}

			if (facebookId != null && !facebookId.isEmpty()) {
				String result = FacebookModel.Instance.publishFeed(facebookId,
						message, link);
	
				_logger.debug("Shared link " + link + " on " + facebookId);
	
				SKUStats skuStats = new SKUStats(skuId);
				ProductStats productStats = new ProductStats(productId);
				
				if (userId > 0) {
					int rs = SKUUserDAO.getInstance().updateStatus(skuId, userId, SKUUser.IS_SHARED);
					if (rs > 0) {
						skuStats.setShareUserCount(1);
					}
					
					rs = ProductUserDAO.getInstance().updateStatus(productId, userId, ProductUser.IS_SHARED);
					if (rs > 0) {
						productStats.setShareUserCount(1);
					}
				}
				
				skuStats.setShareCount(1);
				SKUStatsDAO.getInstance().increaseCount(skuStats);
				
				productStats.setShareCount(1);
				ProductStatsDAO.getInstance().increaseCount(productStats);
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println(new Date(1453739271826L));
	}
}
