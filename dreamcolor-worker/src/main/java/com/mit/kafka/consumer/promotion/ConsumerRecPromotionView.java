package com.mit.kafka.consumer.promotion;

import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.utils.StringUtils;
import com.mit.worker.worker.PromotionWorker;

public class ConsumerRecPromotionView extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerRecPromotionView.class);
	private final static String topic = ProducerTopic.REC_PROMOTION_UPDATE_VIEW;

	public ConsumerRecPromotionView() {
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
					List<Integer> promotionIds = StringUtils.stringToList(dataArr[2], true);

					if(userId > 0 && promotionIds.size() > 0) {
						PromotionWorker.recPromotionView(userId, promotionIds);
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
