package com.mit.models;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mit.dao.user.DeviceTokenDAO;
import com.mit.luv.kafka.producer.ProducerPush;
import com.mit.luv.kafka.producer.ProducerTopic;

public class NotificationModel {
	
	public static final NotificationModel Instance = new NotificationModel();
	
	private NotificationModel(){};
	
	public void notifyColor(List<Long> colorIds, long thumb, String msg) {
		List<Integer> destIds = DeviceTokenDAO.getInstance().getAll();
		if (destIds != null && !destIds.isEmpty()) {
			int maxSize = 10000;
			for (int i = 0; i < destIds.size(); i += maxSize) {
				List<Integer> part = destIds.subList(i, Math.min(destIds.size(), i + maxSize));
				ProducerPush.send(ProducerTopic.NEW_COLOR,
						System.currentTimeMillis() + "\t" + msg + "\t" + thumb + "\t" + colorIds + "\t" + StringUtils.join(part, ","));
			}
		}
	}
}
