package com.mit.dao;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.constants.MongoErrorCode;
import com.mit.entities.app.AppKey;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.DeleteResult;

public class AppKeyDAO extends CommonDAO {
	private final Logger _logger = LoggerFactory.getLogger(AppKeyDAO.class);
	private static Lock _lock = new ReentrantLock();
	private static AppKeyDAO _instance;

	private final String TABLE_NAME = "app_key";

	public static AppKeyDAO getInstance() {
		if(_instance == null) {
			_lock.lock();
			try {
				if(_instance == null) {
					_instance = new AppKeyDAO();
				}
			} finally {
				_lock.unlock();
			}
		}

		return _instance;
	}

	private AppKeyDAO() {
		super("app");
		if(dbSource != null) {
			dbSource.getCollection(TABLE_NAME).createIndex(new Document("apiKey", "text"), new IndexOptions().unique(true));
		}
	}

	public AppKey getByApiKey(String apiKey) {
		AppKey appKey = null;
		Document objFinder = new Document("apiKey", apiKey);
		if(dbSource != null) {
			try {

				Document objData = dbSource.getCollection(TABLE_NAME).find(objFinder).first();
				if(objData != null) {
					// int appId, String appName, int groupId, String groupName,
					// String apiKey, String secretKey
					appKey = new AppKey(objData.getInteger("os"), objData.getInteger("appId"), objData.getString("appName"), objData.getInteger("groupId"), objData.getString("groupName"), objData.getString("apiKey"),
							objData.getString("secretKey"));
				}
			} catch(final Exception e) {
				_logger.error("getByApiKey ", e);
			}
		}
		return appKey;
	}

	public int insert(AppKey appKey) {
		int rs = MongoErrorCode.SUCCESS;
		Document objFinder = new Document("os", appKey.getOs()).append("appId", appKey.getAppId()).append("appName", appKey.getAppName()).append("groupId", appKey.getGroupId()).append("groupName", appKey.getGroupName())
				.append("apiKey", appKey.getApiKey()).append("secretKey", appKey.getSecretKey());
		if(dbSource != null) {
			try {

				dbSource.getCollection(TABLE_NAME).insertOne(objFinder);
			} catch(final Exception e) {
				_logger.error("insert ", e);
			}
		}
		return rs;
	}
	
	public int truncate() {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				Document filter = new Document();
				DeleteResult qRs = dbSource.getCollection(TABLE_NAME).deleteMany(filter);
				rs = (int) qRs.getDeletedCount();
			} catch(final Exception e) {
				_logger.error("truncate ", e);
			}
		}

		return rs;
	}
}