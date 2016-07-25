package com.mit.models;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mit.dao.notification.NewsDAO;
import com.mit.dao.notification.UserNewsDAO;
import com.mit.entities.notification.News;
import com.mit.entities.notification.UserNews;
import com.mit.luv.kafka.producer.ProducerPush;
import com.mit.luv.kafka.producer.ProducerTopic;

public class NewsModel {
	public static final NewsModel Instance = new NewsModel();

	private NewsModel() {
	}

	public Map<String, Object> getNewsList(int userId, int from, int size) {
		Map<String, Object> rs = new HashMap<String, Object>();

		List<UserNews> userNews = UserNewsDAO.getInstance().getNews(userId, from, size + 1);

		boolean hasMore = false;
		if (userNews.size() > size) {
			hasMore = true;
			userNews = userNews.subList(0, size);
		}

		List<Long> newsIds = new LinkedList<Long>();
		// List<Long> unviewNewsIds = new LinkedList<Long>();
		for (UserNews userNewz : userNews) {
			newsIds.add(userNewz.getuId());
		}

		Map<Long, News> news = NewsDAO.getInstance().getMapByListId(newsIds);
		List<UserNews.UserView> userNewsViews = new LinkedList<UserNews.UserView>();
		for (UserNews userNewz : userNews) {
			News newz = news.get(userNewz.getuId());
			if (newz != null) {
				userNewsViews.add(userNewz.buildUserView().setContent(newz.buildUserView()));
			}
		}
		UserNewsDAO.getInstance().resetCount(userId);

		rs.put("news", userNewsViews);
		rs.put("hasMore", hasMore);
		rs.put("newCount", UserNewsDAO.getInstance().countNewItems(userId));

		return rs;
	}
	
	public void viewNews(String imei, List<Long> newsIds) {
		ProducerPush.send(ProducerTopic.VIEW_NEWS, 
				System.currentTimeMillis() + "\t" + imei + "\t" + StringUtils.join(newsIds, ","));
	}


	public Map<String, Object> getNewsCount(int userId) {
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("newCount", UserNewsDAO.getInstance().countNewItems(userId));
		return rs;
	}

	public static void main(String[] args) {
	}
}
