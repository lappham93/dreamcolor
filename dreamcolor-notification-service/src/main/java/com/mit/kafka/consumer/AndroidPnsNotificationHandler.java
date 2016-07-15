package com.mit.kafka.consumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.common.conts.Device;
import com.mit.dao.user.DeviceTokenDAO;
import com.mit.entities.notification.NotificationItem;
import com.mit.entities.user.DeviceToken;
import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.pns.android.AndroidGCMSender;
import com.mit.pns.android.entities.GCMContent;
import com.mit.pns.android.entities.GCMResult;
import com.mit.pns.utils.TokenUtil;
import com.mit.utils.JsonUtils;

public class AndroidPnsNotificationHandler extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(AndroidPnsNotificationHandler.class);
	private final static String topic = ProducerTopic.NOTIFICATION_ANDROID;

	public AndroidPnsNotificationHandler() {
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
			NotificationItem item = new NotificationItem(itemObj.get("id"), (int)itemObj.get("srcId"), (int)itemObj.get("destId"), 
					(int)itemObj.get("appId"), (int)itemObj.get("type"), (String)itemObj.get("msg"));
//			List<DeviceToken> tokens = DeviceTokenDAO.getInstance().getByUserAndDevice(item.getDestId(), item.getAppId(), Device.ANDROID);
			List<DeviceToken> tokens = DeviceTokenDAO.getInstance().getByListIds(Arrays.asList(item.getDestId()), Device.ANDROID);

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
	
	public static void main(String[] args) {
//		UserAccount acc = UserAccountDAO.getInstance().getByUserNameAndRole("elvydang87@gmail.com", 1);
//		int userId = 0;
//		if (acc != null) {
//			userId = acc.getId();
//		}
//		
//		if (userId > 0) { 			
//			NotificationItem item = ExampleNewsFactory.Instance.createProductItem(userId);
//
//			List<DeviceToken> tokens = DeviceTokenDAO.getInstance().getByUserAndDevice(item.getDestId(), item.getAppId(), Device.ANDROID);
//	
//			if(tokens != null && tokens.size() > 0) {
//				tokens = TokenUtil.removeDuplicate(tokens);
//	
//				List<String> registrationIds = new LinkedList<String>();
//				for (DeviceToken token: tokens) {
//					registrationIds.add(token.getDeviceToken());
//				}
//				
//				GCMContent content = new GCMContent();
//				//content.setDryRun(true);
//				Map<String, Object> contentData = new HashMap<String, Object>();
//				contentData.put("id", item.getId());
//				contentData.put("type", item.getType());
//				contentData.put("content", item.getMsg());
//				content.setData(contentData);
//				content.setRegistrationIds(registrationIds);
//				try {
//					String jsonContent = JsonUtils.Instance.toJson(content);
//					System.out.println(jsonContent);
//					GCMResult rs = AndroidGCMSender.send(
//							JsonUtils.Instance.toJson(content), tokens);
//					System.out.println(JsonUtils.Instance.toJson(rs));
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}
}
