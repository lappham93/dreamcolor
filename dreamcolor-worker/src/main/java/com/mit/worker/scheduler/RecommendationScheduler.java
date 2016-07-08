package com.mit.worker.scheduler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.mit.dao.notification.RecPromotionDAO;
import com.mit.dao.promotion.PromotionTargetDAO;
import com.mit.dao.service.ServiceMenuDAO;
import com.mit.entities.app.AppKey;
import com.mit.entities.notification.NotificaitonType;
import com.mit.entities.notification.NotificationItem;
import com.mit.entities.notification.RecPromotion;
import com.mit.entities.promotion.PromotionContent;
import com.mit.entities.promotion.PromotionTypeEnum;
import com.mit.entities.service.ServiceMenu;
import com.mit.luv.kafka.producer.ProducerPush;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.utils.JsonUtils;

public class RecommendationScheduler {
	public static void main(String[] args) {
		int userId = 5;
		int promotionId = 50;
		
		String strFormat = "Discount $xxx OFF when purchase";
		int maxServiceNum = 5;
		int maxServiceLen = 140 - strFormat.length() - ",...".length();

		Map<Integer, String> promoContents = PromotionTargetDAO.getInstance().getContentByListId(Arrays.asList(promotionId));
		
		for (int id: promoContents.keySet()) {
			PromotionContent content = JsonUtils.Instance.getObject(PromotionContent.class, promoContents.get(id));
			StringBuilder serviceName = new StringBuilder();
			
			if (content.getType() == PromotionTypeEnum.FREE.getValue()) {
				serviceName.append("Free ");
			} else if (content.getType() == PromotionTypeEnum.PERCENT.getValue()) {
				serviceName.append("Discount " + content.getDiscount() + " ");
			} else if (content.getType() == PromotionTypeEnum.AMOUNT.getValue()) {
				serviceName.append("Discount " + content.getDiscount() + " ");
			}
			
			if (content.getServices().size() == 0) {
				serviceName.append(ServiceMenu.ANY_SERVICE);
			} else {
				List<Integer> services = content.getServices();
				if  (services.size() > maxServiceNum) {
					services = services.subList(0, maxServiceNum);
				}
				Map<Integer, String> srvNames = ServiceMenuDAO.getInstance().getNameByListId(services);
				int serviceCount = 0;
				for (int serviceId: srvNames.keySet()) {
					if (serviceCount == 0 || serviceName.length() + srvNames.get(serviceId).length() <= maxServiceLen) {
						if (serviceCount > 0) {
							serviceName.append(", ");
						}
							
						serviceName.append(srvNames.get(serviceId));
						serviceCount++;
					} else {
						break;
					}
				}
				
				if (serviceName.length() > maxServiceLen) {
					serviceName.delete(maxServiceLen - 3, serviceName.length()).append("...");
				}
				
				if (serviceCount < content.getServices().size()) {
					serviceName.append(",...");
				}				
			}
			
			serviceName.append(" when purchase");
			promoContents.put(id, serviceName.toString());
		}
		
		System.out.println(String.format(strFormat, promoContents.get(promotionId)));
		
		RecPromotion recPromo = new RecPromotion(promotionId);
		RecPromotionDAO.getInstance().addItem(userId, recPromo);
		NotificationItem notiObj = new NotificationItem(0, 0, userId,
				AppKey.CYOGEL, NotificaitonType.NEWS.getValue(),
				Collections.<Long>emptyList(), System.currentTimeMillis());
		notiObj.setMsg(promoContents.get(promotionId));
		byte[] msg = JsonUtils.Instance.toByteJson(notiObj);
		ProducerPush.send(ProducerTopic.NOTIFICATION_ANDROID, msg);
		ProducerPush.send(ProducerTopic.NOTIFICATION_IOS, msg);
	}
}
