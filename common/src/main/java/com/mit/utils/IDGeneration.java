package com.mit.utils;

import org.redisson.Redisson;
import org.redisson.core.RAtomicLong;

import com.mit.utils.IDGeneration;
import com.mit.redis.RedisClient;

public class IDGeneration {
	private final Redisson redisson;

	public static IDGeneration Instance = new IDGeneration();

	private IDGeneration() {
		redisson = RedisClient.getInstance("autoGeneration").getConnect();
		if(redisson == null) {
			System.out.println("Don't create connect to redis");
		}
	}

	public long generateId(String key) {
		RAtomicLong idGen = redisson.getAtomicLong(genKey(key));
		return idGen.incrementAndGet();
	}

	public long setStartId(String key, int val) {
		RAtomicLong idGen = redisson.getAtomicLong(genKey(key));
		return idGen.addAndGet(val);
	}

	public String genKey(String key) {
		return "luv_" + key;
	}
}
