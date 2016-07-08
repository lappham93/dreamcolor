package com.mit.worker.worker;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.notification.NewsDAO;
import com.mit.dao.notification.UserNewsDAO;
import com.mit.entities.notification.MultiDestNotificationItem;
import com.mit.entities.notification.News;
import com.mit.entities.notification.UserNews;
import com.mit.luv.kafka.producer.ProducerPush;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.utils.JsonUtils;

public class NewsWorker {
	private static final Logger _logger = LoggerFactory.getLogger(NewsWorker.class);
	
	public static void newsView(int userId, List<Long> newsIds) {
		UserNewsDAO.getInstance().updateView(userId, newsIds);
	}
	
	public static void notifyNews(long newsId, List<Integer> userIds) {
		News news = NewsDAO.getInstance().getById(newsId);
		
		if (news != null) {
			UserNews userNews = new UserNews(news.getuId(), false, 0);
			UserNewsDAO.getInstance().addItemToUserList(userIds, userNews);
	
			MultiDestNotificationItem notiItem = news.buildMultiDestNotificationItem(userIds);
			byte[] msg = JsonUtils.Instance.toByteJson(notiItem);
			ProducerPush.send(ProducerTopic.NOTIFICATION_ANDROID_MULTI_DEST, msg);
			ProducerPush.send(ProducerTopic.NOTIFICATION_IOS_MULTI_DEST, msg);
		}
	}
}
