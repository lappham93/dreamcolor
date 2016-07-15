package com.mit.kafka.consumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
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
import com.mit.pns.android.AndroidGCMSender;
import com.mit.pns.android.entities.GCMContent;
import com.mit.pns.android.entities.GCMResult;
import com.mit.pns.utils.TokenUtil;
import com.mit.utils.JsonUtils;

public class AndroidPnsMultiDestNotificationHandler extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(AndroidPnsMultiDestNotificationHandler.class);
	private final static String topic = ProducerTopic.NOTIFICATION_ANDROID_MULTI_DEST;

	public AndroidPnsMultiDestNotificationHandler() {
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
			List<DeviceToken> tokens = DeviceTokenDAO.getInstance().getByListIds(item.getDestIds(), Device.ANDROID);
			
			_logger.debug("tokens size " + tokens.size());
			if(tokens != null && tokens.size() > 0) {
				tokens = TokenUtil.removeDuplicate(tokens);

				List<String> registrationIds = new LinkedList<String>();
				for (DeviceToken token: tokens) {
					registrationIds.add(token.getDeviceToken());
				}

				GCMContent content = new GCMContent();
				//content.setDryRun(true);
				Map<String, Object> contentData = new HashMap<String, Object>();
				contentData.put("id", item.getId());
				contentData.put("type", item.getType());
				contentData.put("content", item.getMsg());
				content.setData(contentData);
				content.setRegistrationIds(registrationIds);
				try {
					GCMResult rs = AndroidGCMSender.send(
							JsonUtils.Instance.toJson(content), tokens);
					System.out.println(JsonUtils.Instance.toJson(rs));
				} catch (IOException e) {
					_logger.error("pns error type=" + item.getType(), e);
				}
			}
		} catch (Exception e) {
			_logger.error("consumer error " + topic, e);
		}
	}
}
