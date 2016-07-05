package com.mit.worker.app;

import com.mit.kafka.consumer.email.ConsumerEmailOrder;
import com.mit.kafka.consumer.email.ConsumerEmailSimple;
import com.mit.kafka.consumer.order.ConsumerOrderSendSE;
import com.mit.kafka.consumer.product.ConsumerNewsNotify;
import com.mit.kafka.consumer.product.ConsumerNewsView;
import com.mit.kafka.consumer.product.ConsumerProductAddCart;
import com.mit.kafka.consumer.product.ConsumerProductOrder;
import com.mit.kafka.consumer.product.ConsumerProductShare;
import com.mit.kafka.consumer.product.ConsumerProductView;
import com.mit.kafka.consumer.product.ConsumerProductWish;
import com.mit.kafka.consumer.user.AvatarURLUploadHandler;
import com.mit.kafka.consumer.user.ConsumerUserRegister;
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

		queueSerVice.add(new ConsumerProductView());
		queueSerVice.add(new ConsumerProductWish());
		queueSerVice.add(new ConsumerProductShare());
		queueSerVice.add(new ConsumerProductOrder());
		queueSerVice.add(new ConsumerProductAddCart());
		queueSerVice.add(new ConsumerNewsView());
		queueSerVice.add(new AvatarURLUploadHandler());
        queueSerVice.add(new ConsumerEmailSimple());
        queueSerVice.add(new ConsumerEmailOrder());
        queueSerVice.add(new ConsumerUserRegister());
        queueSerVice.add(new ConsumerOrderSendSE());
        queueSerVice.add(new ConsumerNewsNotify());
		queueSerVice.start();
	}
}
