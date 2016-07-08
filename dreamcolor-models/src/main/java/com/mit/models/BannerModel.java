package com.mit.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mit.dao.banner.BannerDAO;
import com.mit.entities.banner.Banner;

public class BannerModel {
	public static final BannerModel Instance = new BannerModel();
	
	private BannerModel() {}
	
	public Map<String, Object> getListBanner(int from, int count) {
		Map<String, Object> rs = new HashMap<String, Object>();
		int err = ModelError.SUCCESS;
		boolean hasMore = false;
		List<Banner> banners = BannerDAO.getInstance().getSlice(from, count+1);
		if (banners != null && banners.size() > count) {
			hasMore = true;
			banners = banners.subList(0, count);
		}
		List<Banner.UserView> bannerViews = new LinkedList<Banner.UserView>();
		for (Banner banner: banners) {
			bannerViews.add(banner.buildUserView());
		}
		rs.put("err", err);
		rs.put("banners", bannerViews);
		rs.put("hasMore", hasMore);
		
		return rs;
	}
	
}
