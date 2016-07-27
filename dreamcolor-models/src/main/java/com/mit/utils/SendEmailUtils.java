package com.mit.utils;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.producer.ProducerPush;
import com.mit.luv.kafka.producer.ProducerTopic;
import org.slf4j.helpers.MessageFormatter;

public class SendEmailUtils {
	public static final SendEmailUtils Instance = new SendEmailUtils();

	// private final String _feedbackEmailTo =
	// ConfigUtils.getConfig().getString("feedback.email.to");
	// private final String _feedbackEmailCCs =
	// ConfigUtils.getConfig().getString("feedback.email.cc");
	private final String _feedbackSubject = "Message from %s";
	private final String _feedbackEmailName = "DreaMau Feedback";

	// private final String _resetPassSubject = "Reset password";
	// private final String _resetPassEmailName = "bConnect";
	// private final String _resetPassMessage = "Your reset password is {}";

	private final String _contactEmailTo = ConfigUtils.getConfig().getString("contact.email.to");
	private final String _contactEmailCCs = ConfigUtils.getConfig().getString("contact.email.cc");
	 private final String _contactSubject = "Message from {}";
	 private final String _contactEmailName = "DreaMau Contact";
	private final String _contactMassage = "Name: {}<br/>Email: {}<br/>Phone: {}<br/>Message: {}";
	//
	// private final String _orderSubject = "Your order with bConnect app";
	// private final String _adminEmails =
	// ConfigUtils.getConfig().getString("admin.emails");
	// private final String _adminUSEmails =
	// ConfigUtils.getConfig().getString("admin.us.emails");
	// private final String _adminIntlEmails =
	// ConfigUtils.getConfig().getString("admin.intl.emails");
	// private final String _orderEmailName = "Order bConnect";
	//
	// private final String _orderCODSubject = "Your order with bConnect app
	// (Bank transfer)";
	// private final String _orderPaymentSubject = "Payment completed!";
	// private final String _orderCancellationSubject = "Order cancellation";
	//
	// private final String _registerEmailName = "bConnect";
	// private final String _registerSubject = "Welcome to bConnect";

	// public boolean validateEmail(String email) {
	// boolean result = true;
	// try {
	// InternetAddress emailAddr = new InternetAddress(email);
	// emailAddr.validate();
	// } catch (AddressException ex) {
	// result = false;
	// }
	// return result;
	// }
	private final Logger logger = LoggerFactory.getLogger(SendEmailUtils.class);

	public void sendFeedback(String name, String email, String phone, String message) {
		String subject = String.format(_feedbackSubject, name);
		String contactMessage = String.format(_contactMassage, name, email, phone, message);
		String msg = StringUtils.join(Arrays.asList(System.currentTimeMillis(), _contactEmailTo, _feedbackEmailName,
				_contactEmailCCs, subject, contactMessage), "\t\t");
		logger.debug("lap " + msg);
		ProducerPush.send(ProducerTopic.SEND_EMAIL_CONTACT, msg);
	}

	// public void sendResetPassword(String emailTo, String password) {
	// String message = MessageFormatter.format(_resetPassMessage,
	// password).getMessage();
	// String msg = StringUtils.join(Arrays.asList(System.currentTimeMillis(),
	// emailTo, _resetPassEmailName, "", _resetPassSubject, message), "\t");
	// ProducerPush.send(ProducerTopic.SEND_EMAIL, msg);
	// }
	
    public void sendContact(String name, String email, String phone, String message) {
        try {
        String subject = MessageFormatter.arrayFormat(_contactSubject, new Object[] {name}).getMessage();
        String contactMessage = MessageFormatter.arrayFormat(_contactMassage, new Object[] {name, email, phone, message}).getMessage();
        String msg = StringUtils.join(Arrays.asList(System.currentTimeMillis(), _contactEmailTo, _contactEmailName, _contactEmailCCs, subject, contactMessage), "\t\t");

        	ProducerPush.send(ProducerTopic.SEND_EMAIL_CONTACT, msg);
        	logger.debug("send contact ok");
        } catch (Exception e) {
        	logger.error("send contact error", e);
        }
    }

	// private String getEmailCCs(boolean isDomestic) {
	// String emailCCs =
	// AppConstantManager.Instance.getString(AppConstantKey.ADMIN_EMAILS);
	// if (isDomestic) {
	// emailCCs += "," +
	// AppConstantManager.Instance.getString(AppConstantKey.ADMIN_US_EMAILS);
	// } else {
	// emailCCs += "," +
	// AppConstantManager.Instance.getString(AppConstantKey.ADMIN_INTL_EMAILS);
	// }
	//
	// return emailCCs;
	// }

	// public void sendOrder(String oidNoise, String email, boolean isDomestic)
	// {
	// String emailCCs = getEmailCCs(isDomestic);
	// String msg = StringUtils.join(Arrays.asList(System.currentTimeMillis(),
	// email, _orderEmailName, emailCCs, _orderSubject, oidNoise), "\t");
	// ProducerPush.send(ProducerTopic.SEND_EMAIL_ORDER, msg);
	// }
	//
	// public void sendCODOrder(String oidNoise, String email, boolean
	// isDomestic) {
	// String emailCCs = getEmailCCs(isDomestic);
	// String msg = StringUtils.join(Arrays.asList(System.currentTimeMillis(),
	// email, _orderEmailName, emailCCs, _orderCODSubject, oidNoise), "\t");
	// ProducerPush.send(ProducerTopic.SEND_EMAIL_ORDER, msg);
	// }
	//
	// public void sendOrderPayment(String oidNoise, String email, boolean
	// isDomestic) {
	// String emailCCs = getEmailCCs(isDomestic);
	// String msg = StringUtils.join(Arrays.asList(System.currentTimeMillis(),
	// email, _orderEmailName, emailCCs, _orderPaymentSubject, oidNoise), "\t");
	// ProducerPush.send(ProducerTopic.SEND_EMAIL_ORDER, msg);
	// }
	//
	// public void sendOrderCancellation(String oidNoise, String email, boolean
	// isDomestic) {
	// String emailCCs = getEmailCCs(isDomestic);
	// String msg = StringUtils.join(Arrays.asList(System.currentTimeMillis(),
	// email, _orderEmailName, emailCCs, _orderCancellationSubject, oidNoise),
	// "\t");
	// ProducerPush.send(ProducerTopic.SEND_EMAIL_ORDER, msg);
	// }
	//
	// public void sendRegister(String oidNoise, String email) {
	// String msg = StringUtils.join(Arrays.asList(System.currentTimeMillis(),
	// email, _registerEmailName, "", _registerSubject, oidNoise), "\t");
	// ProducerPush.send(ProducerTopic.SEND_EMAIL_REGISTER, msg);
	// }
	//
	public static void main(String[] args) {
		String _contactMassage = "Name: {}	Email: {}	Phone: {}	Message: {}";
		String name = "abc";
		String email = "abc";
		String phone = "abc";
		String message = "abc";
		String contactMessage = MessageFormatter.arrayFormat(_contactMassage, new Object[] {name, email, phone, message}).getMessage();
		System.out.println(contactMessage);
	}
}
