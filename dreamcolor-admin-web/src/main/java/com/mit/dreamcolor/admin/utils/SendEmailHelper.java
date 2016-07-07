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

package com.mit.dreamcolor.admin.utils;

import com.mit.utils.ConfigUtils;
import java.util.List;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Apr 12, 2016
 */
public class SendEmailHelper {
    private final Logger logger = LoggerFactory.getLogger(SendEmailHelper.class);
    
    private final String _smtpHost = ConfigUtils.getConfig().getString("email3.smtp.host");
	private final int _smtpPort = ConfigUtils.getConfig().getInt("email3.smtp.port");
	private final String _emailUserName = ConfigUtils.getConfig().getString("email3.username");
	private final String _emailPassword = ConfigUtils.getConfig().getString("email3.password");

    private final String _smtpHost2 = ConfigUtils.getConfig().getString("email4.smtp.host");
	private final int _smtpPort2 = ConfigUtils.getConfig().getInt("email4.smtp.port");
	private final String _emailUserName2 = ConfigUtils.getConfig().getString("email4.username");
	private final String _emailPassword2 = ConfigUtils.getConfig().getString("email4.password");
    
    public static SendEmailHelper instance = new SendEmailHelper();
    
    public boolean sendListEmail(List<String> listEmailTos, String emailName, String emailBCCs, String subject, String message){
        try {
            String emailTos = "";
            if(listEmailTos != null && !listEmailTos.isEmpty()){
                int i = 0;
                for(String email : listEmailTos){
                    if(email != null && !email.isEmpty()){
                        if(i == 0){
                            emailTos += email;
                        } else{
                            emailTos += "," + email;
                        }
                        i++;
                    }
                }
            }
            if(emailTos != null && !emailTos.isEmpty() && message != null && !message.isEmpty()){
                boolean success = sendEmail(emailTos, emailName, emailBCCs, subject, message, _smtpHost, _smtpPort, _emailUserName, _emailPassword);
                if (!success) {
                    success = sendEmail(emailTos, emailName, emailBCCs, subject, message, _smtpHost2, _smtpPort2, _emailUserName2, _emailPassword2);
                    if (!success) {
                        logger.error("Send mail error " + message);
                        return false;
                    }
                    return true;
                }
                return true;
            } else{
                logger.error("SendEmailHelper sendListEmail error parametor");
            }
        } catch (Exception e) {
			logger.error("SendEmailHelper sendListEmail: ", e);
		}
        return false;
    }
    
    public boolean sendEmail(String emailTos, String emailName, String emailBCCs, String subject, String message){
        try {
            if(emailTos != null && !emailTos.isEmpty() && message != null && !message.isEmpty()){
                boolean success = sendEmail(emailTos, emailName, emailBCCs, subject, message, _smtpHost, _smtpPort, _emailUserName, _emailPassword);
                if (!success) {
                    success = sendEmail(emailTos, emailName, emailBCCs, subject, message, _smtpHost2, _smtpPort2, _emailUserName2, _emailPassword2);
                    if (!success) {
                        logger.error("Send mail error " + message);
                        return false;
                    }
                    return true;
                }
                return true;
            } else{
                logger.error("SendEmailHelper sendEmail error parametor");
            }
        } catch (Exception e) {
			logger.error("SendEmailHelper sendEmail: ", e);
		}
        return false;
    }
    
    private boolean sendEmail(String emailTos, String emailName, String emailBCCs, String subject, String message,
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
			if (!emailBCCs.isEmpty()) {
				for (String emailBCC: emailBCCs.split(",")) {
					sender.addBcc(emailBCC);
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
