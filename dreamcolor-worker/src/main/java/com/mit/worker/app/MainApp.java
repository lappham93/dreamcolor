package com.mit.worker.app;

import com.mit.kafka.consumer.email.ConsumerEmailSimple;
import com.mit.luv.kafka.consumer.ConsumerQueueService;

public class MainApp {

	public static void main(String[] args) {
		ConsumerQueueService queueSerVice = new ConsumerQueueService();
//		queueSerVice.add(new ConsumerPromotionShare());
//		queueSerVice.add(new ConsumerPromotionUse());
//		queueSerVice.add(new ConsumerPromotionPush());
//		queueSerVice.add(new ConsumerPromotionLike());
//		queueSerVice.add(new ConsumerPromotionView());
//		queueSerVice.add(new ConsumerRecPromotionView());
//		queueSerVice.add(new AvatarURLUploadHandler());
//		queueSerVice.add(new UpdateUserLocation());
//		queueSerVice.add(new ConsumerPromotionSuggester());
//		queueSerVice.add(new ConsumerBizInit());
        queueSerVice.add(new ConsumerEmailSimple());
		queueSerVice.start();
	}
}
