package com.mit.kafka.consumer.promotion;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.worker.worker.PromotionWorker;

public class ConsumerPromotionPush extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerPromotionPush.class);
	private final static String topic = ProducerTopic.PROMOTION_NEW;

	public ConsumerPromotionPush() {
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
				if(dataArr.length >= 2) {
					int promotionId = NumberUtils.toInt(dataArr[1]);

					if(promotionId > 0) {
						try {
						PromotionWorker.publishPromotion(promotionId);
						_logger.debug("process " + msg);
						} catch (Exception e) {
							_logger.error("error ", e);
						}
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
