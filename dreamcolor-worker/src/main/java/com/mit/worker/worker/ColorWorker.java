package com.mit.worker.worker;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.notification.NewsDAO;
import com.mit.dao.notification.UserNewsDAO;
import com.mit.entities.notification.ColorNews;
import com.mit.entities.notification.MultiDestNotificationItem;
import com.mit.entities.notification.News;
import com.mit.entities.notification.UserNews;
import com.mit.luv.kafka.producer.ProducerPush;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.utils.JsonUtils;

public class ColorWorker {

	private static final Logger _logger = LoggerFactory.getLogger(ColorWorker.class);

	public static void notifyColor(List<Long> colorIds, List<Integer> destIds, String msgSend, long thumb) {
		ColorNews news = new ColorNews(0, colorIds, msgSend, thumb, News.EVENT_NONE, News.ACTIVE);
		if (NewsDAO.getInstance().insert(news) >= 0) {
			UserNews userNews = new UserNews(news.getuId(), false, 0);
			UserNewsDAO.getInstance().addItemToUserList(destIds, userNews);
			MultiDestNotificationItem notiItem = news.buildMultiDestNotificationItem(destIds);
			byte[] msg = JsonUtils.Instance.toByteJson(notiItem);
			ProducerPush.send(ProducerTopic.NOTIFICATION_ANDROID_MULTI_DEST, msg);
			ProducerPush.send(ProducerTopic.NOTIFICATION_IOS_MULTI_DEST, msg);
		} else {
			_logger.debug("Can't insert colornews into db");
		}
	}

}
