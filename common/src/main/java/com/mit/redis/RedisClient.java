package com.mit.redis;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.redisson.Config;
import org.redisson.Redisson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.utils.ConfigUtils;

public class RedisClient {
	private final Logger _logger = LoggerFactory.getLogger(RedisClient.class);
	private static final ConcurrentHashMap<String, RedisClient> _instanceMap = new ConcurrentHashMap<String, RedisClient>(16, 0.9f, 16);

	private static Lock _lock = new ReentrantLock();

	private final Redisson redisson;

	public static RedisClient getInstance(String configName) {
		String identify = ConfigUtils.getConfig().getString(ConfigUtils.genKey(configName, "host"));
		RedisClient instance = _instanceMap.get(identify);
		if(instance == null) {
			_lock.lock();
			try {
				instance = _instanceMap.get(identify);
				if(instance == null) {
					instance = new RedisClient(configName);
					_instanceMap.put(identify, instance);
				}
			} finally {
				_lock.unlock();
			}
		}

		return instance;
	}

	public RedisClient(String configName) {
		Config config = new Config();
		config.useSingleServer().setAddress(ConfigUtils.getConfig().getString(ConfigUtils.genKey(configName, "host"), "127.0.0.1:6379"));

		redisson = Redisson.create(config);
		if (redisson == null) {
			System.out.println("don't create redis session");
		}
	}

	public Redisson getConnect() {
		return redisson;
	}

	public void shutdown(Connection conn) {
		redisson.shutdown();
	}

}
