package com.mit.kafka.consumer.product;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.worker.worker.ProductWorker;

public class ConsumerProductAddCart extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerProductAddCart.class);
	private final static String topic = ProducerTopic.PRODUCT_ADD_CART;

	public ConsumerProductAddCart() {
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
				if(dataArr.length >= 5) {
					int userId = NumberUtils.toInt(dataArr[1]);
					long productId = NumberUtils.toLong(dataArr[2]);
					long skuId = NumberUtils.toLong(dataArr[3]);
					long quantity = NumberUtils.toLong(dataArr[4]);

					if(userId > 0 && productId > 0 && skuId > 0) {
						ProductWorker.productAddToCart(userId, productId, skuId, quantity);
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
