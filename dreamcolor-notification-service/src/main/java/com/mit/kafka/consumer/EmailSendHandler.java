package com.mit.kafka.consumer;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.utils.ConfigUtils;

public class EmailSendHandler extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(EmailSendHandler.class);
	
	private final String _smtpHost = ConfigUtils.getConfig().getString("email.smtp.host");
	private final int _smtpPort = ConfigUtils.getConfig().getInt("email.smtp.port");
	private final String _defaultEmailFrom = ConfigUtils.getConfig().getString("email.default.from");
	private final String _defaultEmailPass = ConfigUtils.getConfig().getString("email.default.password");

	public EmailSendHandler() {
		super(ProducerTopic.SEND_EMAIL);
	}

	@Override
	public void execute(byte[] data) {
		String msg = new String(data);
		try {
			if(msg != null && !msg.isEmpty()) {
				String[] dataArr = msg.split("\t");
				if(dataArr.length >= 6) {
					String emailTos = dataArr[1];
					String emailName = dataArr[2];
					String emailCCs = dataArr[3];
					String subject = dataArr[4];
					String message = dataArr[5];

					if(emailTos != null && !emailTos.isEmpty()) {
						try {
							Email sender = new SimpleEmail();
							sender.setHostName(_smtpHost);
							sender.setSmtpPort(_smtpPort);
							sender.setAuthenticator(new DefaultAuthenticator(_defaultEmailFrom, _defaultEmailPass));
							sender.setStartTLSRequired(true);
							sender.setStartTLSEnabled(true);
							sender.setFrom(_defaultEmailFrom, emailName);
							sender.setSubject(subject);
							sender.setMsg(message);
							for (String emailTo: emailTos.split(",")) {
								sender.addTo(emailTo);
							}
							for (String emailCC: emailCCs.split(",")) {
								sender.addCc(emailCC);
							}
							String rs = sender.send();
							_logger.debug(msg + "\t" + rs);
						} catch (EmailException e) {
							_logger.error("Send mail error ", e);
						}
					}
				} else {
	        		_logger.error("Data format error " + msg);
	        	}
			}
		} catch (Exception e) {
			_logger.error("consumer error " + super.getTopic(), e);
		}
	}
}
