package com.mit.kafka.consumer.user;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.user.UserLocationDAO;
import com.mit.entities.user.UserLocation;
import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;

public class UpdateUserLocation extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(AvatarURLUploadHandler.class);
	private final static String topic = ProducerTopic.UPDATE_USERLOCATION;

	public UpdateUserLocation() {
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
				if (dataArr.length >= 3) {
					int userId = NumberUtils.toInt(dataArr[1]);
					double lat = NumberUtils.toDouble(dataArr[2]);
					double lon = NumberUtils.toDouble(dataArr[3]);
					
					UserLocation userLoc = new UserLocation(userId, lat, lon);
					UserLocationDAO.getInstance().updateLocation(userLoc);
				} else {
					_logger.error("Data format error", msg);
				}
			}
		} catch (Exception e) {
			_logger.error("consumer error " + topic, e);
		}
	}
}
