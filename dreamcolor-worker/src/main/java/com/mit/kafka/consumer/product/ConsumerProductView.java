package com.mit.kafka.consumer.product;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.worker.worker.ProductWorker;

public class ConsumerProductView extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerProductView.class);
	private final static String topic = ProducerTopic.PRODUCT_VIEW;

	public ConsumerProductView() {
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
				if (dataArr.length >= 4) {
					int userId = NumberUtils.toInt(dataArr[1]);
					String[] productList = dataArr[2].split(",");
					String[] skuList = dataArr[3].split(",");
					String imei = (dataArr.length >= 5) ? dataArr[4] : null;

					if (productList.length > 0 && productList.length == skuList.length) {		
						for (int i = 0; i < productList.length; i++) {
							long productId = NumberUtils.toLong(productList[i]);
							long skuId = NumberUtils.toLong(skuList[i]);
							
							if (productId > 0 && skuId > 0) {
								ProductWorker.productView(userId, productId, skuId, imei);
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
