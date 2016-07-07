package com.mit.dao.photo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.constants.MongoErrorCode;
import com.mit.dao.CommonDAO;
import com.mit.dao.MongoDBParse;
import com.mit.dao.mid.MIdGenLongDAO;
import com.mit.entities.photo.PhotoInfo;
import com.mongodb.client.result.UpdateResult;

public class PhotoInfoDAO extends CommonDAO {
	private final Logger _logger = LoggerFactory.getLogger(PhotoInfoDAO.class);
	private static Lock _lock = new ReentrantLock();
	private static PhotoInfoDAO _instance;

	private String TABLE_NAME = "photo_info";

	public static PhotoInfoDAO getInstance() {
		if (_instance == null) {
			_lock.lock();
			try {
				if (_instance == null) {
					_instance = new PhotoInfoDAO();
				}
			} finally {
				_lock.unlock();
			}
		}

		return _instance;
	}

	private PhotoInfoDAO() {
	}

	public String getTableName() {
		return TABLE_NAME;
	}

	public PhotoInfo getById(long id) {
		PhotoInfo photoInfo = null;
		if (dbSource != null) {
			try {
				Document objFinder = new Document("_id", id);
				Document doc = dbSource.getCollection(TABLE_NAME).find(objFinder).first();
				if (doc != null) {
					photoInfo = new MongoMapper().parseObject(doc);
				}
			} catch (final Exception e) {
				_logger.error("getById ", e);
			}
		}

		return photoInfo;
	}
	
	public PhotoInfo getByRefIdAndType(long refId, int type) {
		PhotoInfo photoInfo = null;
		if (dbSource != null) {
			try {
				Document objFinder = new Document("refId", refId)
						.append("type", type);
				Document doc = dbSource.getCollection(TABLE_NAME).find(objFinder).first();
				if (doc != null) {
					photoInfo = new MongoMapper().parseObject(doc);
				}
			} catch (final Exception e) {
				_logger.error("getByRefIdAndType ", e);
			}
		}

		return photoInfo;
	}
	
	public int insert(PhotoInfo msg) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				if (msg.getId() <= 0) {
					msg.setId(MIdGenLongDAO.getInstance(TABLE_NAME).getNext());
				}
				Document obj = mapper.toDocument(msg);
				obj = mapper.buildInsertTime(obj);
				dbSource.getCollection(TABLE_NAME).insertOne(obj);
				rs = MongoErrorCode.SUCCESS;
			} catch (final Exception e) {
				_logger.error("insert ", e);
			}
		}

		return rs;
	}
	
	public int addField(String field, Object defaultValue) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				Document filter = new Document();
				Document obj = new Document("$set", new Document(field, defaultValue));
				UpdateResult qRs = dbSource.getCollection(TABLE_NAME).updateMany(filter, obj);
				rs = (int) qRs.getModifiedCount();
			} catch (final Exception e) {
				_logger.error("addField ", e);
			}
		}

		return rs;
	}
	
	private class MongoMapper extends MongoDBParse<PhotoInfo> {

		@Override
		public PhotoInfo parseObject(Document doc) {
			PhotoInfo photoInfo = new PhotoInfo(doc.getLong("_id"), doc.getLong("refId"), 
					doc.getInteger("type"), doc.getInteger("height"), doc.getInteger("width"));
			return photoInfo;
		}

		@Override
		public Document toDocument(PhotoInfo obj) {
			Document doc = new Document("_id", obj.getId())
					.append("refId", obj.getRefId())
					.append("width", obj.getWidth())
					.append("height", obj.getHeight())
					.append("type", obj.getType());
			return doc;
		}

	}
}
