package com.mit.dao.user;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.constants.MongoErrorCode;
import com.mit.dao.CommonDAO;
import com.mit.dao.MongoDBParse;
import com.mit.dao.mid.MIdGenDAO;
import com.mit.entities.user.DeviceToken;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class DeviceTokenDAO extends CommonDAO {
	private final Logger _logger = LoggerFactory.getLogger(DeviceTokenDAO.class);
	private static Lock _lock = new ReentrantLock();
	private static DeviceTokenDAO _instance;

	private final String TABLE_NAME = "account_device_pns";

	public static DeviceTokenDAO getInstance() {
		if(_instance == null) {
			_lock.lock();
			try {
				if(_instance == null) {
					_instance = new DeviceTokenDAO();
				}
			} finally {
				_lock.unlock();
			}
		}

		return _instance;
	}

	private DeviceTokenDAO() {}
	
	public List<Integer> getAll() {
		List<Integer> rs = null;
		if (dbSource != null) {
			try {
				Document filter = new Document("status", new Document("$gt", 0));
				FindIterable<Document> docs = dbSource.getCollection(TABLE_NAME).find(filter);
				if (docs != null) {
					rs = new ArrayList<Integer>();
					MongoCursor<Document> tmps = docs.iterator();
					while(tmps.hasNext()) {
						rs.add(tmps.next().getInteger("_id"));
					}
				}
			} catch (Exception e) {
				_logger.error("getAll ", e);
			}
		}
		
		return rs;
	}
	
	public List<DeviceToken> getByDevice(int deviceId) {
		List<DeviceToken> rs = null;
		if (dbSource != null) {
			try {
				Document filter = new Document("deviceId", deviceId)
						.append("status", new Document("$gt", 0));
				FindIterable<Document> docs = dbSource.getCollection(TABLE_NAME).find(filter);
				if (docs != null) {
					rs = new MongoMapper().parseList(docs);
				}
			} catch (Exception e) {
				_logger.error("getAll ", e);
			}
		}
		
		return rs;
	}
	
	public List<DeviceToken> getByListIds(List<Integer> ids, int deviceId) {
		List<DeviceToken> rs = null;
		if (dbSource != null) {
			try {
				Document filter = new Document("_id", new Document("$in", ids))
						.append("deviceId", deviceId)
						.append("status", new Document("$gt", 0));
				FindIterable<Document> docs = dbSource.getCollection(TABLE_NAME).find(filter);
				if (docs != null) {
					rs = new MongoMapper().parseList(docs);
				}
			} catch (Exception e) {
				_logger.error("getByListIds ", e);
			}
		}
		
		return rs;
	}

	public DeviceToken getByToken(String deviceToken) {
		DeviceToken device = null;
		if (dbSource != null) {
			try {
				Document filter = new Document("deviceToken", deviceToken);
				Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
				if (doc != null) { 
					device = new MongoMapper().parseObject(doc);
				}
			} catch (Exception e) {
				_logger.error("getByDeviceId ", e);
			}
		}
		
		return device;
	}
	
	public DeviceToken getByImei(String imei) {
		DeviceToken device = null;
		if (dbSource != null) {
			try {
				Document filter = new Document("imei", imei);
				Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
				if (doc != null) { 
					device = new MongoMapper().parseObject(doc);
				}
			} catch (Exception e) {
				_logger.error("getByImei ", e);
			}
		}
		
		return device;
	}
	
	public int insert(DeviceToken obj) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				if (obj.getId() <= 0) {
					obj.setId(MIdGenDAO.getInstance(TABLE_NAME).getNext());
				}
				Document doc = mapper.toDocument(obj);
				doc = mapper.buildInsertTime(doc);
				dbSource.getCollection(TABLE_NAME).insertOne(doc);
				rs = MongoErrorCode.SUCCESS;
			} catch (Exception e) {
				_logger.error("insert ", e);
			}
		}
		
		return rs;
	}
	
	public int update(DeviceToken token) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document filter = new Document("_id", token.getId());
				Document update = new Document("$set", mapper.toDocument(token));
				update = mapper.buildUpdateTime(update);
				UpdateResult ur = dbSource.getCollection(TABLE_NAME).updateOne(filter, update);
				rs = (int)ur.getModifiedCount();
			} catch (final Exception e) {
				_logger.error("update ", e);
			}
		}

		return rs;
	}

	public int deleteByImei(String imei) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				Document objFinder = new Document("imei", imei);
				Document update = new Document("$set", new Document("status", 0));
				UpdateResult doc = dbSource.getCollection(TABLE_NAME).updateOne(objFinder, update);
				rs = (int)doc.getModifiedCount();
			} catch(Exception e) {
				_logger.error("deleteByImei ", e);
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

	private class MongoMapper extends MongoDBParse<DeviceToken> {

		@Override
		public DeviceToken parseObject(Document doc) {
			DeviceToken deviceToken = new DeviceToken(doc.getInteger("_id"), 
					doc.getInteger("appId"), 
					doc.getInteger("deviceId"), 
					doc.getString ("imei"), 
					doc.getString("deviceToken"),
					doc.getInteger("status"));
			return deviceToken;
		}

		@Override
		public Document toDocument(DeviceToken obj) {
			Document doc = new Document("_id", obj.getId())
				.append("appId", obj.getAppId())
				.append("deviceId", obj.getDeviceId())
				.append("imei", obj.getImei())
				.append("deviceToken", obj.getDeviceToken())
				.append("status", obj.getStatus());
			return doc;
		}

	}
}
