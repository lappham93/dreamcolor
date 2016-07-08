package com.mit.kafka.consumer.product;

import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.utils.StringUtils;
import com.mit.worker.worker.NewsWorker;

public class ConsumerNewsNotify extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerNewsNotify.class);
	private final static String topic = ProducerTopic.NEWS_NOTIFY;

	public ConsumerNewsNotify() {
		super(topic);
	}

	@Override
	public String getTopic() {
		return topic;
	}

	@Override
	public void execute(byte[] data) {
		String msg = new String(data);		
		try {
			if (msg != null && !msg.isEmpty()) {
				String[] dataArr = msg.split("\t");
				if(dataArr.length >= 3) {
					long newsId = NumberUtils.toLong(dataArr[1]);
					List<Integer> userIds = StringUtils.stringToList(dataArr[2], true);

					if(newsId > 0 && userIds.size() > 0) {
						NewsWorker.notifyNews(newsId, userIds);
					}
				} else {
					_logger.error("Data format error", msg);
				}
			}
		} catch (Exception e) {
			_logger.error("consumer error " + topic, e);
		}
	}
}
