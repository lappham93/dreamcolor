package com.mit.sms.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.kafka.producer.KafkaProducers;
import com.mit.luv.kafka.producer.ProducerTopic;

public class CallBackDeliverHandler extends HandlerWrapper {
	private final Logger _logger = LoggerFactory
			.getLogger(CallBackDeliverHandler.class);

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (target.startsWith("/loop/pns/sms/callback")) {
			String msgId = request.getParameter("messageId");
			String status = request.getParameter("err-code");

			if(msgId != null && status != null) {
				KafkaProducers.Instance.send(ProducerTopic.DELIVER_SMS, System.currentTimeMillis() + "\t" + msgId + "\t" + status);
			}
			baseRequest.setHandled(true);
		}
	}
}
