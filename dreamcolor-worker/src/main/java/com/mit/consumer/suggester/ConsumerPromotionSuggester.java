package com.mit.consumer.suggester;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.es.PromotionSuggester;
import com.mit.kafka.consumer.promotion.ConsumerPromotionLike;
import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;

public class ConsumerPromotionSuggester extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerPromotionLike.class);
	private final static String topic = ProducerTopic.SUGGESTER_PROMOTION_UPDATE;

	public ConsumerPromotionSuggester() {
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
					String keyword = dataArr[1];
					int weight = NumberUtils.toInt(dataArr[2]);

					PromotionSuggester.Instance.incWeight(keyword.toLowerCase(), weight);
				} else {
					_logger.error("Data format error", msg);
				}
			}
		} catch (Exception e) {
			_logger.error("consumer error " + topic, e);
		}
	}
}
