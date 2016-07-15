package com.mit.dao.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.constants.MongoErrorCode;
import com.mit.dao.CommonDAO;
import com.mit.dao.MongoDBParse;
import com.mit.dao.mid.MIdGenLongDAO;
import com.mit.entities.notification.ColorNews;
import com.mit.entities.notification.News;
import com.mit.entities.notification.NewsType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class NewsDAO extends CommonDAO {
	private final Logger _logger = LoggerFactory.getLogger(NewsDAO.class);
	private static Lock _lock = new ReentrantLock();
	private static NewsDAO _instance;

	private String TABLE_NAME = "news";

	public static NewsDAO getInstance() {
		if (_instance == null) {
			_lock.lock();
			try {
				if (_instance == null) {
					_instance = new NewsDAO();
				}
			} finally {
				_lock.unlock();
			}
		}

		return _instance;
	}

	public List<News> listAll() {
		List<News> news = null;
		if (dbSource != null) {
			try {
				Document objFinder = new Document();
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder);
				if (doc != null) {
					news = new MongoMapper().parseList(doc);
				}
			} catch (final Exception e) {
				_logger.error("getSlideAll ", e);
			}
		}
		return news;
	}

	public List<News> getByListId(List<Long> ids) {
		List<News> news = null;
		if (dbSource != null) {
			try {
				Document objFinder = new Document("_id", new Document("$in", ids)).append("status",
						new Document("$gt", 0));
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder);
				if (doc != null) {
					news = new MongoMapper().parseList(doc);
				}
			} catch (final Exception e) {
				_logger.error("getByListId ", e);
			}
		}

		return news;
	}

	public Map<Long, News> getMapByListId(List<Long> ids) {
		Map<Long, News> news = new HashMap<Long, News>();
		if (dbSource != null) {
			try {
				Document objFinder = new Document("_id", new Document("$in", ids)).append("status",
						new Document("$gt", 0));
				FindIterable<Document> docs = dbSource.getCollection(TABLE_NAME).find(objFinder);
				if (docs != null) {
					MongoMapper mapper = new MongoMapper();
					Document doc = null;
					MongoCursor<Document> cursor = docs.iterator();
					while (cursor != null && cursor.hasNext() && (doc = cursor.next()) != null) {
						News newz = mapper.parseObject(doc);
						news.put(newz.getuId(), newz);
					}
				}
			} catch (final Exception e) {
				_logger.error("getMapByListId ", e);
			}
		}

		return news;
	}

	public List<News> getByEvent(int event) {
		List<News> news = null;
		if (dbSource != null) {
			try {
				Document objFinder = new Document("event", event).append("status", new Document("$gt", 0));
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder);
				if (doc != null) {
					news = new MongoMapper().parseList(doc);
				}
			} catch (final Exception e) {
				_logger.error("getByEvent ", e);
			}
		}
		return news;
	}

	public News getById(long id) {
		News product = null;
		if (dbSource != null) {
			try {
				Document objFinder = new Document("_id", id);
				Document doc = dbSource.getCollection(TABLE_NAME).find(objFinder).first();
				if (doc != null) {
					product = new MongoMapper().parseObject(doc);
				}
			} catch (final Exception e) {
				_logger.error("getById ", e);
			}
		}

		return product;
	}

	public List<News> getAllListByType(int type) {
		List<News> newses = null;
		if (dbSource != null) {
			try {
				Document objFinder = new Document("type", type);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder);
				if (doc != null) {
					newses = new MongoMapper().parseList(doc);
				}
			} catch (final Exception e) {
				_logger.error("getByListId ", e);
			}
		}
		return newses;
	}

	public int insert(News product) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				if (product.getuId() <= 0) {
					product.setuId(MIdGenLongDAO.getInstance(TABLE_NAME).getNext());
				}
				Document obj = mapper.toDocument(product);
				obj = mapper.buildInsertTime(obj);
				dbSource.getCollection(TABLE_NAME).insertOne(obj);
				rs = MongoErrorCode.SUCCESS;
			} catch (final Exception e) {
				_logger.error("insert ", e);
			}
		}

		return rs;
	}

	public int update(News product) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document filter = new Document("_id", product.getuId());
				Document obj = new Document("$set", mapper.toDocument(product));
				obj = mapper.buildUpdateTime(obj);
				UpdateResult qRs = dbSource.getCollection(TABLE_NAME).updateOne(filter, obj);
				rs = (int) qRs.getModifiedCount();
			} catch (final Exception e) {
				_logger.error("update ", e);
			}
		}

		return rs;
	}

	public int delete(long id) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document filter = new Document("_id", id);
				Document obj = new Document("$set", new Document("status", 0));
				obj = mapper.buildUpdateTime(obj);
				UpdateResult qRs = dbSource.getCollection(TABLE_NAME).updateOne(filter, obj);
				rs = (int) qRs.getModifiedCount();
			} catch (final Exception e) {
				_logger.error("delete ", e);
			}
		}

		return rs;
	}

	public int truncate() {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				Document filter = new Document("event", new Document("$ne", News.EVENT_REGISTER));
				DeleteResult qRs = dbSource.getCollection(TABLE_NAME).deleteMany(filter);
				rs = (int) qRs.getDeletedCount();
			} catch (final Exception e) {
				_logger.error("truncate ", e);
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
	
	public long removeFromUser(long fromUserId, long feedId, long photoId, int childType) {
		long rs = 0;
		if (dbSource != null) {
			try {
				Document filter = new Document("feedId", feedId)
						.append("photoId", photoId)
						.append("childType", childType);
				Document update = new Document("$pull", new Document("fromUserIds", fromUserId));
				UpdateResult ur = dbSource.getCollection(TABLE_NAME).updateMany(filter, update);
				rs = ur.getModifiedCount();
				
			} catch (final Exception e) {
				_logger.debug("removeFromUser ", e);
			}
		}
		
		return rs;
	}
	
	public long addFromUser(long fromUserId, long feedId, long photoId, int childType) {
		long rs = 0;
		if (dbSource != null) {
			try {
				Document filter = new Document("feedId", feedId)
						.append("photoId", photoId)
						.append("childType", childType);
				Document update = new Document("$push", new Document("fromUserIds", fromUserId));
				UpdateResult ur = dbSource.getCollection(TABLE_NAME).updateMany(filter, update);
				rs = ur.getModifiedCount();
				
			} catch (final Exception e) {
				_logger.debug("addFromUser ", e);
			}
		}
		
		return rs;
	}
	
	private class MongoMapper extends MongoDBParse<News> {

		@Override
		public News parseObject(Document doc) {
			long uId = doc.getLong("_id");
			int type = doc.getInteger("type");
			int event = doc.getInteger("event");
			int status = doc.getInteger("status");

			News news = null;
			if (type == NewsType.COLOR.getValue()) {
				news = new ColorNews(uId, (List<Long>) doc.get("colorIds"), doc.getString("msg"), doc.getLong("thumb"), event, status);
			}

			return news;
		}

		@Override
		public Document toDocument(News obj) {
			Document doc = new Document("_id", obj.getuId()).append("type", obj.getType())
					.append("event", obj.getEvent()).append("status", obj.getStatus());

			if (obj instanceof ColorNews) {
				ColorNews colorNews = (ColorNews) obj;
				doc.append("colorIds", colorNews.getColorIds()).append("msg", colorNews.getMsg()).append("thumb", colorNews.getThumb());
			}

			return doc;
		}

	}
}
