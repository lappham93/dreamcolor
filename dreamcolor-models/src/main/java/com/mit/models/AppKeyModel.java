package com.mit.models;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.mit.dao.AppKeyDAO;
import com.mit.entities.app.AppKey;


public class AppKeyModel {

	private static Lock _lock = new ReentrantLock();
	private static AppKeyModel _instance;

	private final ConcurrentMap<String, AppKey> appKeyData = new ConcurrentHashMap<String, AppKey>(16,0.9f,16);

	public static AppKeyModel getInstance() {
		if(_instance == null) {
			_lock.lock();
			try {
				if(_instance == null) {
					_instance = new AppKeyModel();
				}
			} finally {
				_lock.unlock();
			}
		}

		return _instance;
	}

	public AppKey getAppKey(String apiKey) {
		AppKey appKey = appKeyData.get(apiKey);
		if(appKey == null) {
			appKey = AppKeyDAO.getInstance().getByApiKey(apiKey);
			if(appKey != null) {
				appKeyData.put(apiKey, appKey);
			}
		}

		return appKey;
	}

}
