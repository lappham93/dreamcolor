package com.mit.kafka.consumer.order;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.worker.worker.OrderWorker;

public class ConsumerOrderSendSE extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerOrderSendSE.class);
	private final static String topic = ProducerTopic.ORDER_SEND_SE;

	public ConsumerOrderSendSE() {
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
					long orderId = NumberUtils.toLong(dataArr[1]);

					if(orderId > 0) {
						OrderWorker.sendOrderToShippingEasy(orderId);
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
