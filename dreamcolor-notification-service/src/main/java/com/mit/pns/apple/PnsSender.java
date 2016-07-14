package com.mit.pns.apple;

import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLHandshakeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.user.DeviceTokenDAO;
import com.mit.utils.ConfigUtils;
import com.relayrides.pushy.apns.ApnsEnvironment;
import com.relayrides.pushy.apns.ExpiredToken;
import com.relayrides.pushy.apns.ExpiredTokenListener;
import com.relayrides.pushy.apns.FailedConnectionListener;
import com.relayrides.pushy.apns.PushManager;
import com.relayrides.pushy.apns.PushManagerConfiguration;
import com.relayrides.pushy.apns.RejectedNotificationListener;
import com.relayrides.pushy.apns.RejectedNotificationReason;
import com.relayrides.pushy.apns.util.CustomApnsPayloadBuilder;
import com.relayrides.pushy.apns.util.SSLContextUtil;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
import com.relayrides.pushy.apns.util.TokenUtil;

public class PnsSender {
	private static Logger _logger = LoggerFactory.getLogger(PnsSender.class);
	private static final String pathToKey = ConfigUtils.getConfig().getString("apns.key.path");
	private static final String password = ConfigUtils.getConfig().getString("apns.key.password");

	private PushManager<SimpleApnsPushNotification> pushManager;
	private static Lock _lock = new ReentrantLock();

	public static PnsSender _instance;

	public static PnsSender getInstance() {
		if(_instance == null) {
			_lock.lock();
			if(_instance == null) {
				_instance = new PnsSender();
			}
			_lock.unlock();
		}

		return _instance;
	}

	public PnsSender() {
		this(pathToKey, password);
	}

	public PnsSender(String pathToKey, String password) {
		try {
			ApnsEnvironment APNS_ENV = ApnsEnvironment.getProductionEnvironment();
//			if("development".equals(System.getProperty("app_env"))) {
//				APNS_ENV = ApnsEnvironment.getSandboxEnvironment();
//			}

			pushManager =
				    new PushManager<SimpleApnsPushNotification>(
				    	APNS_ENV,
				        SSLContextUtil.createDefaultSSLContext(pathToKey, password),
				        null, null, null, new PushManagerConfiguration(), "ExamplePushManager");

			pushManager.start();
			pushManager.registerExpiredTokenListener(new HandleExpireToken());
			pushManager.registerFailedConnectionListener(new HandleFailedConnection());
			pushManager.registerRejectedNotificationListener(new HandleRejectedNotification());
		} catch (Exception e) {
			_logger.error("init pushManage error ", e);
		}
	}

	public void send(String tokenString, String alertBody, String message, int badgeNumber) throws Exception {
		 byte[] token = TokenUtil.tokenStringToByteArray(tokenString);
		CustomApnsPayloadBuilder payloadBuilder = new CustomApnsPayloadBuilder();

		payloadBuilder.setAlertBody(alertBody);
		payloadBuilder.setBadgeNumber(badgeNumber);
		payloadBuilder.setMessage(message);
		//payloadBuilder.setCategoryName("hello");

		String payload = payloadBuilder.buildWithDefaultMaximumLength();

		pushManager.getQueue().put(new SimpleApnsPushNotification(token, payload));
	}

	public void send(String tokenString, String alertBody, String message, String sound, int badgeNumber) throws Exception {
		 byte[] token = TokenUtil.tokenStringToByteArray(tokenString);
		CustomApnsPayloadBuilder payloadBuilder = new CustomApnsPayloadBuilder();

		payloadBuilder.setAlertBody(alertBody);
		payloadBuilder.setSoundFileName(sound);
		payloadBuilder.setBadgeNumber(badgeNumber);
		payloadBuilder.setMessage(message);
		payloadBuilder.setContentAvailable(true);
		//payloadBuilder.setCategoryName("hello");

		String payload = payloadBuilder.buildWithDefaultMaximumLength();

		pushManager.getQueue().put(new SimpleApnsPushNotification(token, payload));
	}

	public void shutdown() throws InterruptedException {
		pushManager.shutdown();
	}

	public boolean isShutDown() {
		return pushManager.isShutDown();
	}

	private class HandleRejectedNotification implements RejectedNotificationListener<SimpleApnsPushNotification> {

	    @Override
	    public void handleRejectedNotification(
	            final PushManager<? extends SimpleApnsPushNotification> pushManager,
	            final SimpleApnsPushNotification notification,
	            final RejectedNotificationReason reason) {

	    	String token = TokenUtil.tokenBytesToString(notification.getToken());
	        _logger.error("Notification Reject " + token + " error: " + reason);
	    }
	}

	private class HandleFailedConnection implements FailedConnectionListener<SimpleApnsPushNotification> {

	    @Override
	    public void handleFailedConnection(
	            final PushManager<? extends SimpleApnsPushNotification> pushManager,
	            final Throwable cause) {

	    	_logger.error("Notification Failed " , cause);
	        if (cause instanceof SSLHandshakeException) {
	            try {
					shutdown();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//	            Instance = null;
	        }
	    }
	}

	private class HandleExpireToken implements ExpiredTokenListener<SimpleApnsPushNotification> {

	    @Override
	    public void handleExpiredTokens(
	            final PushManager<? extends SimpleApnsPushNotification> pushManager,
	            final Collection<ExpiredToken> expiredTokens) {

	        for (final ExpiredToken expiredToken : expiredTokens) {
	        	String token = TokenUtil.tokenBytesToString(expiredToken.getToken());
//	        	DeviceTokenDAO.getInstance().deleteByToken(token);
	        }
	    }
	}
}
