package com.mit.sms.handler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.producer.ProducerPush;
import com.mit.luv.kafka.producer.ProducerTopic;

public class VoiceStatusHandler extends HandlerWrapper {
	private final Logger _logger = LoggerFactory
			.getLogger(VoiceStatusHandler.class);
	private final List<String> paramRequire = Arrays.asList("call-id", "call-price", "call-duration", "call-request", "call-start", "call-end", "status");

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (target.startsWith("/loop/pns/voice/status")) {
			String callId = request.getParameter("call-id");
			String price = request.getParameter("call-price");
			String duration = request.getParameter("call-duration");
			String requestTime = request.getParameter("call-request");
			String startTime = request.getParameter("call-start");
			String endTime = request.getParameter("call-end");
			String status = request.getParameter("status");

			String message = System.currentTimeMillis() + "\t" + callId + "\t" + price + "\t" + duration + "\t" + requestTime
					 + "\t" + startTime + "\t" + endTime + "\t" + status;
			ProducerPush.send(ProducerTopic.DELIVER_VOICE_STATUS, message);
			
			baseRequest.setHandled(true);
		}
	}
}
