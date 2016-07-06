package com.mit.dao.video;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.constants.MongoErrorCode;
import com.mit.dao.CommonDAO;
import com.mit.dao.MongoDBParse;
import com.mit.dao.mid.MIdGenLongDAO;
import com.mit.entities.video.Video;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.UpdateResult;

public class VideoDAO extends CommonDAO {
	private final Logger _logger = LoggerFactory.getLogger(VideoDAO.class);
	private static Lock _lock = new ReentrantLock();
	private static VideoDAO _instance;

	private String TABLE_NAME = "video";

	public static VideoDAO getInstance() {
		if (_instance == null) {
			_lock.lock();
			try {
				if (_instance == null) {
					_instance = new VideoDAO();
				}
			} finally {
				_lock.unlock();
			}
		}

		return _instance;
	}

	private VideoDAO() {
	}

	public String getTableName() {
		return TABLE_NAME;
	}
	
	public int totalAll() {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				Document filter = new Document("status", new Document("$gt", 0));
				rs = (int) dbSource.getCollection(TABLE_NAME).count(filter);
			} catch (final Exception e) {
				_logger.error("totalAll ", e);
			}
		}
		
		return rs;
	}
	
	public List<Video> getSlice(int count, int from, String fieldSort, boolean ascOrder) {
		List<Video> rs = null;
		if (dbSource != null) {
			try {
				Document filter = new Document("status", new Document("$gt", 0));
				Document sort = new Document(fieldSort, ascOrder ? 1 : -1);
				FindIterable<Document> docs = dbSource.getCollection(TABLE_NAME).find(filter).sort(sort).skip(from).limit(count);
				if (docs != null) {
					rs = new MongoMapper().parseList(docs);
				}
			} catch (final Exception e) {
				_logger.error("getSlice ", e);
			}
		}
		
		return rs;
	}
	
	public Video getById(long id) {
		Video rs = null;
		if (dbSource != null) {
			try {
				Document filter = new Document("_id", id);
				Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
				if (doc != null) {
					rs = new MongoMapper().parseObject(doc);
				}
			} catch (final Exception e) {
				_logger.error("getById ", e);
			}
		}
		
		return rs;
	}
	
	public Video getByLink(String link) {
		Video rs = null;
		if (dbSource != null) {
			try {
				Document filter = new Document("link", link);
				Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
				if (doc != null) {
					rs = new MongoMapper().parseObject(doc);
				}
			} catch (final Exception e) {
				_logger.error("getByLink ", e);
			}
		}
		
		return rs;
	}
	
	public int updateView(long id) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				Document filter = new Document("_id", id);
				Document update = new Document("$inc", new Document("views", 1));
				UpdateResult ur = dbSource.getCollection(TABLE_NAME).updateOne(filter, update);
				rs = (int)ur.getModifiedCount();
			} catch (final Exception e) {
				_logger.error("updateView ", e);
			}
		}
		
		return rs;
	}

	public int insert(Video msg) {
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

	public int update(Video msg) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document filter = new Document("_id", msg.getId());
				Document obj = new Document("$set", mapper.toDocument(msg));
				obj = mapper.buildUpdateTime(obj);
				UpdateResult qRs = dbSource.getCollection(TABLE_NAME).updateOne(filter, obj);
				rs = (int) qRs.getModifiedCount();
			} catch (final Exception e) {
				_logger.error("update ", e);
			}
		}

		return rs;
	}

	private class MongoMapper extends MongoDBParse<Video> {

		@Override
		public Video parseObject(Document doc) {
			Video sv = new Video(doc.getLong("_id"), doc.getString("link"), doc.getString("site"),
					doc.getString("title"), doc.getString("desc"), doc.getLong("thumbnail"), doc.getInteger("views"),
					doc.getInteger("status"), doc.getDate("createTime").getTime(), doc.getDate("updateTime").getTime());

			return sv;
		}

		@Override
		public Document toDocument(Video obj) {
			Document doc = new Document("_id", obj.getId())
					.append("link", obj.getLink())
					.append("site", obj.getSite())
					.append("title", obj.getTitle())
					.append("desc", obj.getDesc())
					.append("thumbnail", obj.getThumbnailNum())
					.append("views", obj.getViews())
					.append("status", obj.getStatus());

			return doc;
		}

	}
}
