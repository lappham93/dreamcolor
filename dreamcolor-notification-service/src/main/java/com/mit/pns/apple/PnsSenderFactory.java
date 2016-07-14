package com.mit.pns.apple;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.mit.utils.ConfigUtils;

public class PnsSenderFactory {
	public enum PnsSenderType {
		CYOGEL(ConfigUtils.getConfig().getString("apns.cyogel.key.path"), ConfigUtils.getConfig().getString("apns.cyogel.key.password"));

		private String pathConfig;
		private String passConfig;

		private PnsSenderType(String pathConfig, String passConfig) {
			this.pathConfig = pathConfig;
			this.passConfig = passConfig;
		}

		public String getPathConfig() {
			return pathConfig;
		}

		public String getPassConfig() {
			return passConfig;
		}
		
		public static PnsSenderType getType(int index) {
			if (index > 0 && index <= PnsSenderType.values().length)
				return PnsSenderType.values()[index - 1];
			return null;
		}
	};

	private static Lock _lock = new ReentrantLock();
	private static PnsSenderFactory _instance;

	private final ConcurrentMap<PnsSenderType, PnsSender> pnsSenders = new ConcurrentHashMap<PnsSenderType, PnsSender>(16,0.9f,16);

	public static PnsSenderFactory getInstance() {
		if(_instance == null) {
			_lock.lock();
			try {
				if(_instance == null) {
					_instance = new PnsSenderFactory();
				}
			} finally {
				_lock.unlock();
			}
		}

		return _instance;
	}
	
	public PnsSender getPnsSender(int appId) {		
		return getPnsSender(PnsSenderType.getType(appId));
	}

	public PnsSender getPnsSender(PnsSenderType type) {		
		PnsSender pnsSender = pnsSenders.get(type);
		if(pnsSender == null || pnsSender.isShutDown()) {
			pnsSender = new PnsSender(type.getPathConfig(), type.getPassConfig());
			if(pnsSender != null) {
				pnsSenders.put(type, pnsSender);
			}
		}

		return pnsSender;
	}
}
