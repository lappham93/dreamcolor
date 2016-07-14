package com.mit.pns.app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;

import com.mit.kafka.consumer.AndroidPnsMultiDestNotificationHandler;
import com.mit.kafka.consumer.AndroidPnsNotificationHandler;
import com.mit.kafka.consumer.ApplePnsMultiDestNotificationHandler;
import com.mit.kafka.consumer.ApplePnsNotificationHandler;
import com.mit.luv.kafka.consumer.ConsumerQueueService;
import com.mit.sms.handler.CallBackDeliverHandler;
import com.mit.sms.handler.VoiceStatusHandler;
import com.mit.utils.ConfigUtils;

public class MainApp {

	public static void main(String[] args) {
		ConsumerQueueService androidQueueService = new ConsumerQueueService(ConfigUtils.getConfig().getString("kafka.consumer.group.android"));
		androidQueueService.add(new AndroidPnsNotificationHandler());
		androidQueueService.add(new AndroidPnsMultiDestNotificationHandler());
		androidQueueService.start();

		ConsumerQueueService appleQueueService = new ConsumerQueueService(ConfigUtils.getConfig().getString("kafka.consumer.group.apple"));
		appleQueueService.add(new ApplePnsNotificationHandler());
		appleQueueService.add(new ApplePnsMultiDestNotificationHandler());
		appleQueueService.start();
//		
//		ConsumerQueueService queue = new ConsumerQueueService();
//		queue.add(new SMSSendHandler());
//		queue.add(new SMSReceiveHandler());
//		queue.add(new VoiceSendHandler());
//		queue.add(new VoiceStatusDeliverHandler());
//		queue.start();
		int port = ConfigUtils.getConfig().getInt("webserver.port");
		System.out.println("Web config port " + port);
		Server server = new Server(port);

		HandlerList handlers = new HandlerList();
		handlers.addHandler(new CallBackDeliverHandler());
		handlers.addHandler(new VoiceStatusHandler());
//		handlers.addHandler(new AuthorizeRelay());
		server.setHandler(handlers);

		try {
			server.start();
			System.out.println("Web service is started with  port " + port);
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
