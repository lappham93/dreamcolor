package com.mit.connect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.utils.ConfigUtils;
import com.mit.utils.SSLContextUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

public class MDBConnect {
	private static final Logger _logger = LoggerFactory.getLogger(MDBConnect.class);
	private static final ConcurrentHashMap<String, MDBConnect> _instanceMap = new ConcurrentHashMap<String, MDBConnect>(16, 0.9f, 16);

	private static Lock _lock = new ReentrantLock();

	private MongoClient client;

	public static MDBConnect getInstance(String configName) {
		MDBConnect instance = _instanceMap.get(configName);
		if(instance == null) {
			_lock.lock();
			try {
				instance = _instanceMap.get(configName);
				if(instance == null) {
					try {
						instance = new MDBConnect(configName);
						_instanceMap.put(configName, instance);
					} catch(Exception e) {
						_logger.error("error ", e);
					}
				}
			} finally {
				_lock.unlock();
			}
		}

		return instance;
	}

	private MDBConnect() {
		client = null;
	}

	private MDBConnect(String configName) throws Exception {
		client = null;
		List<MongoCredential> credential = new ArrayList<MongoCredential>();
		List<ServerAddress> servers = new ArrayList<ServerAddress>();
		final String hosts = ConfigUtils.getConfig().getString(configName + ".mongodb.host");
		if(hosts != null && !hosts.isEmpty()) {
			String[] hostArr = hosts.split(",");
			for(String host : hostArr) {
				String[] hostPort = host.split(":");
				if(hostPort.length >= 2) {
					servers.add(new ServerAddress(hostPort[0], NumberUtils.toInt(hostPort[1])));
				}
			}
		}

		if(!servers.isEmpty()) {
			String mongCredential = ConfigUtils.getConfig().getString(configName + ".mongodb.users");
			String[] cred = mongCredential.split(";");
			for(String c: cred) {
				String[] usp = c.split(":");
				if(usp.length >= 3) {
					credential.add(MongoCredential.createCredential(usp[1], usp[0], usp[2].toCharArray()));
				}
			}

			MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder().connectionsPerHost(10).maxConnectionIdleTime(60000).connectTimeout(60000).sslEnabled(false);
			
			String keyFile = ConfigUtils.getConfig().getString("mongodb.keyfile");
			String keyPass = ConfigUtils.getConfig().getString("mongodb.keypass");

			if (keyFile != null && keyPass != null) {
				optionsBuilder.sslEnabled(true).socketFactory(SSLContextUtil.createDefaultSSLContext(keyFile, keyPass).getSocketFactory());
			}
			
			MongoClientOptions options = optionsBuilder.build();
			client = new MongoClient(servers, credential, options);
			client.setWriteConcern(WriteConcern.JOURNALED);
			client.setReadPreference(ReadPreference.secondaryPreferred());
		}

		if(client == null) {
			_logger.error("Don't found mongoDB");
		}
	}

	public MongoClient getClient() {
		return client;
	}
}
