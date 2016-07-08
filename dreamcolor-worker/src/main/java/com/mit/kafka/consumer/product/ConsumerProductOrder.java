package com.mit.kafka.consumer.product;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.worker.worker.ProductWorker;

public class ConsumerProductOrder extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerProductOrder.class);
	private final static String topic = ProducerTopic.PRODUCT_ORDER;

	public ConsumerProductOrder() {
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
					String[] productList = dataArr[2].split(",");
					String[] skuList = dataArr[3].split(",");
					String[] quantityList = dataArr[4].split(",");

					if(userId > 0 && productList.length > 0 && productList.length == skuList.length && productList.length == quantityList.length) {
						for (int i = 0; i < productList.length; i++) {
							long productId = NumberUtils.toLong(productList[i]);
							long skuId = NumberUtils.toLong(skuList[i]);
							long quantity = NumberUtils.toLong(quantityList[i]);
							
							if (productId > 0 && skuId > 0 && quantity > 0) {
								ProductWorker.productOrder(userId, productId, skuId, quantity);
							}
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
