package com.mit.kafka.consumer.news;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.utils.StringUtils;
import com.mit.worker.worker.NewsWorker;

public class ConsumerNewsView extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerNewsView.class);
	private final static String topic = ProducerTopic.VIEW_NEWS;

	public ConsumerNewsView() {
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
				if (dataArr.length >= 3) {
					String imei = dataArr[1];
					List<Long> newsIds = StringUtils.stringToLongList(dataArr[2], true);
					NewsWorker.viewNews(imei, newsIds);
					_logger.debug("view news: " + msg);
				} else {
					_logger.error("Data format error", msg);
				}
			}
		} catch (Exception e) {
			_logger.error("consumer error " + topic, e);
		}
	}
}
