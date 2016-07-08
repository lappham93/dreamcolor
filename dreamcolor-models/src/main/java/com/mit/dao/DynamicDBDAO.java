package com.mit.dao;

import java.util.LinkedHashMap;

import com.mit.connect.MDBConnect;
import com.mongodb.client.MongoDatabase;

public abstract class DynamicDBDAO {
	private final MDBConnect connect;
	private LinkedHashMap<String, MongoDatabase> dbSourceMap = new LinkedHashMap<String, MongoDatabase>(55) {
		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<String, MongoDatabase> eldest) {
			return this.size() > 50;
		};
	};

	public DynamicDBDAO() {
		String configName = "";
		connect = MDBConnect.getInstance(configName);
	}

	public MDBConnect getConnect() {
		return connect;
	}

	public MongoDatabase getDbSource(String dbName) {
		if(connect == null) {
			return null;
		}

		if(dbSourceMap.get(dbName) == null) {
			synchronized (dbSourceMap) {
				if(dbSourceMap.get(dbName) == null) {
					MongoDatabase db = getConnect().getClient().getDatabase(dbName);
					dbSourceMap.put(dbName, db);
				}
			}
		}

		return dbSourceMap.get(dbName);
	}
}
