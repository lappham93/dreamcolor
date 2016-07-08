package com.mit.dao;

import com.mit.connect.MDBConnect;
import com.mit.utils.ConfigUtils;
import com.mongodb.client.MongoDatabase;


public abstract class CommonDAO {
	private final MDBConnect connect;
	protected MongoDatabase dbSource;

	public CommonDAO() {
		String configName = "luv";
		connect = MDBConnect.getInstance(configName);
		if(connect != null) {
			dbSource = getConnect().getClient().getDatabase(ConfigUtils.getConfig().getString(configName + ".mongodb.db"));
		}
	}

	public CommonDAO(String configName) {
		connect = MDBConnect.getInstance(configName);
		if(connect != null) {
			dbSource = getConnect().getClient().getDatabase(ConfigUtils.getConfig().getString(configName + ".mongodb.db"));
		}
	}

	public MDBConnect getConnect() {
		return connect;
	}

	public MongoDatabase getDbSource() {
		return dbSource;
	}
	
	public static void main(String[] args) {
		System.out.println(ConfigUtils.getConfig().getString("luv" + ".mongodb.db"));
	}
}
