package com.mit.kafka.consumer.product;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.worker.worker.ProductWorker;

public class ConsumerProductShare extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerProductShare.class);
	private final static String topic = ProducerTopic.PRODUCT_SHARE;

	public ConsumerProductShare() {
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

				if (dataArr.length >= 6) {
					int userId = NumberUtils.toInt(dataArr[1]);
					long productId = NumberUtils.toLong(dataArr[2]);
					long skuId = NumberUtils.toLong(dataArr[3]);
					String message = dataArr[4];
					int platformId = NumberUtils.toInt(dataArr[5]);
					String facebookId = (dataArr.length >= 7) ? dataArr[6] : null;
					
					if (productId > 0 && skuId > 0) {
						ProductWorker.productShare(userId, productId, skuId, message, platformId, facebookId);
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
