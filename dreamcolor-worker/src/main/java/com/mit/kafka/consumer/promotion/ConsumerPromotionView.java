package com.mit.kafka.consumer.promotion;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.worker.worker.PromotionWorker;

public class ConsumerPromotionView extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerPromotionView.class);
	private final static String topic = ProducerTopic.PROMOTION_UPDATE_VIEW;

	public ConsumerPromotionView() {
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
					int userId = NumberUtils.toInt(dataArr[1]);
					String[] promotionList = dataArr[2].split(",");

					if(userId > 0 && promotionList.length > 0) {
						for(String promo : promotionList) {
							int promoId = NumberUtils.toInt(promo);
							PromotionWorker.promotionView(userId, promoId);
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
