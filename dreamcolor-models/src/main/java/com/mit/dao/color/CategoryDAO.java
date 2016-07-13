package com.mit.dao.color;


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
import com.mit.entities.color.Category;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.UpdateResult;

public class CategoryDAO extends CommonDAO {
	private final Logger _logger = LoggerFactory.getLogger(CategoryDAO.class);
	private static Lock _lock = new ReentrantLock();
	private static CategoryDAO _instance;

	private String TABLE_NAME = "color_category";

	public static CategoryDAO getInstance() {
		if(_instance == null) {
			_lock.lock();
			try {
				if(_instance == null) {
					_instance = new CategoryDAO();
				}
			} finally {
				_lock.unlock();
			}
		}

		return _instance;
	}
	
	public int totalAll() {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				Document filter = new Document("status", new Document("$gt", 0));
				rs = (int)dbSource.getCollection(TABLE_NAME).count(filter);
			} catch (final Exception e) {
				_logger.error("totalAll ", e);
			}
		}
		
		return rs;
	}
	
    public List<Category> getAll() {
		List<Category> cates = null;
		if(dbSource != null) {
			try {
				Document filter = new Document("status", new Document("$gt", 0));
				Document sort = new Document("createTime", -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(filter).sort(sort);
				if(doc != null) {
					cates = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("getAll ", e);
			}
		}

		return cates;
	}

	public List<Category> getAll(String fieldSort, boolean ascOrder) {
		List<Category> cates = null;
		if(dbSource != null) {
			try {
				Document filter = new Document("status", new Document("$gt", 0));
				Document sort = new Document("fieldSort", ascOrder ? 1 : -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(filter).sort(sort);
				if(doc != null) {
					cates = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("getAll ", e);
			}
		}

		return cates;
	}
    
	public List<Category> getAllIgnoreStatus(String fieldSort, boolean ascOrder) {
		List<Category> cates = null;
		if(dbSource != null) {
			try {
				Document filter = new Document();
				Document sort = new Document("fieldSort", ascOrder ? 1 : -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(filter).sort(sort);
				if(doc != null) {
					cates = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("getAllIgnoreStatus ", e);
			}
		}

		return cates;
	}
	
	public List<Category> getSlice(int count, int from, String fieldSort, boolean ascOrder) {
		List<Category> cates = null;
		if(dbSource != null) {
			try {
				Document filter = new Document("status", new Document("$gt", 0));
				Document sort = new Document("fieldSort", ascOrder ? 1 : -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(filter).sort(sort).skip(from).limit(count);
				if(doc != null) {
					cates = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("getSlice ", e);
			}
		}

		return cates;
	}
	
	public Category getById(int id) {
		Category cates = null;
		if (dbSource != null) {
			try {
				Document filter = new Document("_id", id);
				Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
				if (doc != null) {
					cates = new MongoMapper().parseObject(doc);
				}
			} catch (final Exception e) {
				_logger.error("getById ", e);
			}
		}
		
		return cates;
	}
    
	public int insert(Category cate) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				if (cate.getId() <= 0) {
					cate.setId(MIdGenDAO.getInstance(TABLE_NAME).getNext());
				}
				Document obj = mapper.toDocument(cate);
				obj = mapper.buildInsertTime(obj);
				dbSource.getCollection(TABLE_NAME).insertOne(obj);
				rs = MongoErrorCode.SUCCESS;
			} catch(final Exception e) {
				_logger.error("insert ", e);
			}
		}

		return rs;
	}
	
	public int update(Category cate) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document filter = new Document("_id", cate.getId());
				Document obj = new Document("$set", mapper.toDocument(cate));
				obj = mapper.buildUpdateTime(obj);
				UpdateResult qRs = dbSource.getCollection(TABLE_NAME).updateOne(filter, obj);
				rs = (int) qRs.getModifiedCount();
			} catch(final Exception e) {
				_logger.error("update ", e);
			}
		}

		return rs;
	}

	public int delete(int id) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document filter = new Document("_id", id);
				Document obj = new Document("$set", new Document("status", 0));
				obj = mapper.buildUpdateTime(obj);
				UpdateResult qRs = dbSource.getCollection(TABLE_NAME).updateOne(filter, obj);
				rs = (int) qRs.getModifiedCount();
			} catch(final Exception e) {
				_logger.error("update ", e);
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

	private class MongoMapper extends MongoDBParse<Category> {

		@Override
		public Category parseObject(Document doc) {
			Category cate = new Category(doc.getInteger("_id"), doc.getString("name"), doc.getString("desc"), doc.getLong("photo"),
					doc.getInteger("status"), doc.getDate("createTime").getTime(), doc.getDate("updateTime").getTime());
			return cate;
		}

		@Override
		public Document toDocument(Category obj) {
			Document doc = new Document("_id", obj.getId())
					.append("name", obj.getName())
					.append("desc", obj.getDescription())
					.append("photo", obj.getPhotoNum())
					.append("status", obj.getStatus());
			return doc;
		}

	}
	
	public static void main(String[] args) {
	}
}
