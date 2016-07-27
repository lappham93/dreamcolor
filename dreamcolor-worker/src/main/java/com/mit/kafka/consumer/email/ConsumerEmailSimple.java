/*
 * Copyright 2016 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mit.kafka.consumer.email;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.utils.ConfigUtils;

/**
 *
 * @author nghiatc
 * @since Jan 27, 2016
 */
public class ConsumerEmailSimple extends ConsumerService {
    private final Logger logger = LoggerFactory.getLogger(ConsumerEmailSimple.class);
	private final static String topic = ProducerTopic.SEND_EMAIL_CONTACT;

    private final String _smtpHost = ConfigUtils.getConfig().getString("email.smtp.host");
	private final int _smtpPort = ConfigUtils.getConfig().getInt("email.smtp.port");
	private final String _emailUserName = ConfigUtils.getConfig().getString("email.username");
	private final String _emailPassword = ConfigUtils.getConfig().getString("email.password");

    private final String _smtpHost2 = ConfigUtils.getConfig().getString("email2.smtp.host");
	private final int _smtpPort2 = ConfigUtils.getConfig().getInt("email2.smtp.port");
	private final String _emailUserName2 = ConfigUtils.getConfig().getString("email2.username");
	private final String _emailPassword2 = ConfigUtils.getConfig().getString("email2.password");
    
	public ConsumerEmailSimple() {
		super(topic);
	}

	@Override
	public String getTopic() {
		return topic;
	}

    @Override
    public void execute(byte[] data) {
        String msg = new String(data);
		try {
			if (msg != null && !msg.isEmpty()) {
				String[] dataArr = msg.split("\t\t");
				if(dataArr.length >= 6) {
					String emailTos = dataArr[1];
					String emailName = dataArr[2];
					String emailCCs = dataArr[3];
					String subject = dataArr[4];
					String message = dataArr[5];
					
					boolean success = sendEmail(emailTos, emailName, emailCCs, subject, message, _smtpHost, 
							_smtpPort, _emailUserName, _emailPassword);

					if (!success) {
						success = sendEmail(emailTos, emailName, emailCCs, subject, message, _smtpHost2, 
								_smtpPort2, _emailUserName2, _emailPassword2);
						
						if (!success) {
							logger.error("Send mail error " + msg);
						}
					}
					logger.debug("Send mail success " + msg);
				} else {
	        		logger.error("Data format error " + msg);
	        	}
			}
		} catch (Exception e) {
			logger.error("consumer error " + topic, e);
		}
    }
    
    private boolean sendEmail(String emailTos, String emailName, String emailCCs, String subject, String message,
    		String smtpHost, int smtpPort, String emailUserName, String emailPassword) {
    	try {
	    	HtmlEmail sender = new HtmlEmail();
			sender.setHostName(smtpHost);
			sender.setSmtpPort(smtpPort);
			sender.setAuthenticator(new DefaultAuthenticator(emailUserName, emailPassword));
			sender.setStartTLSRequired(true);
			sender.setStartTLSEnabled(true);
			sender.setFrom(emailUserName, emailName);
			sender.setSubject(subject);
			sender.setHtmlMsg(message);
			for (String emailTo: emailTos.split(",")) {
				sender.addTo(emailTo);
			}
			if (!emailCCs.isEmpty()) {
				for (String emailCC: emailCCs.split(",")) {
					sender.addCc(emailCC);
				}
			}
						
			for (int i = 0; i < 3; i++) {
				try {
					String rs = sender.send();
					logger.debug(emailTos + "\t" + rs);
					return true;
				} catch (EmailException e) {
					logger.error("Send mail error " + emailTos, e);
				}
			}
    	} catch (Exception e) {
    		logger.error("Send mail error ", e);
    	}
    	
    	return false;
    }
}
