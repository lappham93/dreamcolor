package com.mit.kafka.consumer.promotion;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.worker.worker.PromotionWorker;

public class ConsumerPromotionShare extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerPromotionShare.class);
	private final static String topic = ProducerTopic.PROMOTION_SHARE;

	public ConsumerPromotionShare() {
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

				if (dataArr.length >= 7) {
					int userId = NumberUtils.toInt(dataArr[1]);
					int promotionId = NumberUtils.toInt(dataArr[2]);
					int shopId = NumberUtils.toInt(dataArr[3]);
					int templateId = NumberUtils.toInt(dataArr[4]);
					String message = dataArr[5];
					int platformId = NumberUtils.toInt(dataArr[6]);
					
					if (userId > 0 && platformId > 0) {
						PromotionWorker.sharePromotion(userId, promotionId, shopId, templateId, message, platformId);
					} else {
						_logger.error("Param invalid", msg);
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
