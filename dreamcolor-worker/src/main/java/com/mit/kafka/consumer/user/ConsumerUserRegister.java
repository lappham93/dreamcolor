package com.mit.kafka.consumer.user;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.worker.worker.UserWorker;

public class ConsumerUserRegister extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerUserRegister.class);
	private final static String topic = ProducerTopic.USER_REGISTER;

	public ConsumerUserRegister() {
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
				if (dataArr.length >= 2) {
					int userId = NumberUtils.toInt(dataArr[1]);
					
					if (userId > 0) {						
						UserWorker.userRegister(userId);
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
