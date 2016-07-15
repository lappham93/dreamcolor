package com.mit.kafka.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.common.conts.Device;
import com.mit.dao.user.DeviceTokenDAO;
import com.mit.entities.notification.MultiDestNotificationItem;
import com.mit.entities.user.DeviceToken;
import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.pns.apple.PnsSenderFactory;
import com.mit.utils.JsonUtils;
import com.relayrides.pushy.apns.util.TokenUtil;

public class ApplePnsMultiDestNotificationHandler extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ApplePnsMultiDestNotificationHandler.class);
	private final static String topic = ProducerTopic.NOTIFICATION_IOS_MULTI_DEST;

	public ApplePnsMultiDestNotificationHandler() {
		super(topic);
	}

	@Override
	public String getTopic() {
		return topic;
	}

	@Override
	public void execute(byte[] data) {
		try {
//			NotificationItem item = JsonUtils.Instance.getObject(NotificationItem.class, data);
			Map<String, Object> itemObj = JsonUtils.Instance.getMapObject(data);
			MultiDestNotificationItem item = new MultiDestNotificationItem(itemObj.get("id"), (int)itemObj.get("srcId"), (List)itemObj.get("destIds"), 
					(int)itemObj.get("appId"), (int)itemObj.get("type"), (String)itemObj.get("msg"));
//			List<DeviceToken> tokens = DeviceTokenDAO.getInstance().getByUserListAndDevice(item.getDestIds(), item.getAppId(), Device.IOS);
			List<DeviceToken> tokens = DeviceTokenDAO.getInstance().getByListIds(item.getDestIds(), Device.IOS);

			if(tokens != null && tokens.size() > 0) {
				tokens = TokenUtil.removeDuplicate(tokens);

				Map<String, Object> contentData = new HashMap<String, Object>();
				contentData.put("id", item.getId());
				contentData.put("type", item.getType());
				String message = JsonUtils.Instance.toJson(contentData);
				String alertBody = item.getMsg();
				String sound = "";//"checkin.wav";

				for (DeviceToken token : tokens) {
					try {
						PnsSenderFactory.getInstance().getPnsSender(item.getAppId()).send(token.getDeviceToken(), alertBody, message, sound, 1);
					} catch (Exception e) {
						_logger.error("pns error type=" + item.getType(), e);
					}
				}
			}
		} catch (Exception e) {
			_logger.error("consumer error " + topic, e);
		}
	}
}
