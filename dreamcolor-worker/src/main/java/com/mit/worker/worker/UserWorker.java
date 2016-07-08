package com.mit.worker.worker;

import java.util.LinkedList;
import java.util.List;

import com.mit.dao.notification.NewsDAO;
import com.mit.dao.notification.UserNewsDAO;
import com.mit.entities.notification.News;
import com.mit.entities.notification.NotificationItem;
import com.mit.entities.notification.UserNews;
import com.mit.luv.kafka.producer.ProducerPush;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.utils.JsonUtils;

public class UserWorker {
	public static void userRegister(int userId) {
		List<News> news = NewsDAO.getInstance().getByEvent(News.EVENT_REGISTER);
		
		if (news != null && news.size() > 0) {
			List<UserNews> userNews = new LinkedList<UserNews>();
			for (News newz: news) {
				UserNews userNewz = new UserNews(newz.getuId(), false, 0);
				userNews.add(userNewz);
			}
			
			UserNewsDAO.getInstance().addItems(userId, userNews);
			
			for (News newz: news) {
				NotificationItem notiItem = newz.buildNotificationItem(userId);
				byte[] msg = JsonUtils.Instance.toByteJson(notiItem);
				ProducerPush.send(ProducerTopic.NOTIFICATION_ANDROID, msg);
				ProducerPush.send(ProducerTopic.NOTIFICATION_IOS, msg);
			}
		}
	}
}
