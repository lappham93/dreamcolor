package com.mit.kafka.consumer.promotion;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.worker.worker.PromotionWorker;

public class ConsumerPromotionLike extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerPromotionLike.class);
	private final static String topic = ProducerTopic.PROMOTION_UPDATE_LIKE;

	public ConsumerPromotionLike() {
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
				if(dataArr.length >= 4) {
					int customerId = NumberUtils.toInt(dataArr[1]);
					int promotionId = NumberUtils.toInt(dataArr[2]);
					int type = NumberUtils.toInt(dataArr[3]);

					if(customerId > 0 && promotionId > 0) {
						if(type == 0) {
							PromotionWorker.promotionLike(customerId, promotionId);
						} else if (type == 1) {
							PromotionWorker.promotionUnLike(customerId, promotionId);
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
