package com.mit.dao.color;


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
import com.mit.dao.mid.MIdGenLongDAO;
import com.mit.entities.color.Color;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;

public class ColorDAO extends CommonDAO {
	private final Logger _logger = LoggerFactory.getLogger(ColorDAO.class);
	private static Lock _lock = new ReentrantLock();
	private static ColorDAO _instance;

	private String TABLE_NAME = "color";

	public static ColorDAO getInstance() {
		if(_instance == null) {
			_lock.lock();
			try {
				if(_instance == null) {
					_instance = new ColorDAO();
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
	
	public int totalAll(int categoryId) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				Document filter = new Document("categoryId", categoryId)
						.append("status", new Document("$gt", 0));
				rs = (int)dbSource.getCollection(TABLE_NAME).count(filter);
			} catch (final Exception e) {
				_logger.error("totalAll ", e);
			}
		}
		
		return rs;
	}
	
	public int totalAllIgnoreStatus() {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				Document filter = new Document();
				rs = (int)dbSource.getCollection(TABLE_NAME).count(filter);
			} catch (final Exception e) {
				_logger.error("totalAllIgnoreStatus ", e);
			}
		}
		
		return rs;
	}
	
	public List<Color> getSliceIgnoreStatus(int count, int from, String fieldSort, boolean ascOrder) {
		List<Color> colors = null;
		if(dbSource != null) {
			try {
				Document filter = new Document();
				Document sort = new Document(fieldSort, ascOrder ? 1 : -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(filter).sort(sort).skip(from).limit(count);
				if(doc != null) {
					colors = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("getSliceIgnoreStatus ", e);
			}
		}

		return colors;
	}
	
    public List<Color> getByCateId(int categoryId) {
		List<Color> colors = null;
		if(dbSource != null) {
			try {
				Document filter = new Document("categoryId", categoryId).append("status", new Document("$gt", 0));
				Document sort = new Document("createTime", -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(filter).sort(sort);
				if(doc != null) {
					colors = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("getByCateId ", e);
			}
		}

		return colors;
	}
    
	public List<Color> getSlice(int categoryId, int count, int from, String fieldSort, boolean ascOrder) {
		List<Color> colors = null;
		if(dbSource != null) {
			try {
				Document filter = new Document("categoryId", categoryId)
						.append("status", new Document("$gt", 0));
				Document sort = new Document(fieldSort, ascOrder ? 1 : -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(filter).sort(sort).skip(from).limit(count);
				if(doc != null) {
					colors = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("getSlice ", e);
			}
		}

		return colors;
	}
	
	public List<Color> getFeatureSlice(int count, int from, String fieldSort, boolean ascOrder) {
		List<Color> colors = null;
		if(dbSource != null) {
			try {
				Document filter = new Document("isFeature", true)
						.append("status", new Document("$gt", 0));
				Document sort = new Document(fieldSort, ascOrder ? 1 : -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(filter).sort(sort).skip(from).limit(count);
				if(doc != null) {
					colors = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("getFeatureSlice ", e);
			}
		}

		return colors;
	}
	
	public List<Color> getSearchSlice(String code, int count, int from, String fieldSort, boolean ascOrder) {
		List<Color> colors = null;
		if(dbSource != null) {
			try {
				Document filter = new Document("status", new Document("$gt", 0));
				if (code != null && !code.isEmpty()) {
					filter.append("code", new Document("$regex", code));
				}
				Document sort = new Document(fieldSort, ascOrder ? 1 : -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(filter).sort(sort).skip(from).limit(count);
				if(doc != null) {
					colors = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("getSearchSlice ", e);
			}
		}

		return colors;
	}
	
	public List<String> getSuggest(String code, int count) {
		List<String> words = null;
		if(dbSource != null) {
			try {
				Document filter = new Document("code", new Document("$regex", "^" + code).append("$options", "m"))
						.append("status", new Document("$gt", 0));
				Document sort = new Document("code", 1);
				FindIterable<Document> docs = dbSource.getCollection(TABLE_NAME).find(filter).sort(sort).limit(count);
				if(docs != null) {
					words = new ArrayList<String>();
					MongoCursor<Document> tmp = docs.iterator();
					while (tmp.hasNext()) {
						words.add(tmp.next().getString("code"));
					}
				}
			} catch(final Exception e) {
				_logger.error("getSuggest ", e);
			}
		}

		return words;
	}
	
	public Color getById(long id) {
		Color color = null;
		if (dbSource != null) {
			try {
				Document filter = new Document("_id", id);
				Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
				if (doc != null) {
					color = new MongoMapper().parseObject(doc);
				}
			} catch (final Exception e) {
				_logger.error("getById ", e);
			}
		}
		
		return color;
	}
	
	public int updateView(long id) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				Document filter = new Document("_id", id);
				Document update = new Document("$inc", new Document("views", 1));
				UpdateResult ur = dbSource.getCollection(TABLE_NAME).updateOne(filter, update);
				rs = (int)ur.getModifiedCount();
				//set isFeature
				if (rs > 0) {
					filter.append("isFeature", false)
						.append("views", new Document("$gt", Color.VIEWS_OF_FEATURE_DEFAUTL));
					update = new Document("$set", new Document("isFeature", true));
					dbSource.getCollection(TABLE_NAME).updateOne(filter, update);
				}
			} catch (final Exception e) {
				_logger.error("updateView ", e);
			}
		}
		
		return rs;
	}
    
	public int insert(Color color) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				if (color.getId() <= 0) {
					color.setId(MIdGenLongDAO.getInstance(TABLE_NAME).getNext());
				}
				Document obj = mapper.toDocument(color);
				obj = mapper.buildInsertTime(obj);
				dbSource.getCollection(TABLE_NAME).insertOne(obj);
				rs = MongoErrorCode.SUCCESS;
			} catch(final Exception e) {
				_logger.error("insert ", e);
			}
		}

		return rs;
	}
	
	public int update(Color color) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document filter = new Document("_id", color.getId());
				Document obj = new Document("$set", mapper.toDocument(color));
				obj = mapper.buildUpdateTime(obj);
				UpdateResult qRs = dbSource.getCollection(TABLE_NAME).updateOne(filter, obj);
				rs = (int) qRs.getModifiedCount();
			} catch(final Exception e) {
				_logger.error("update ", e);
			}
		}

		return rs;
	}

	public int delete(long id) {
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

	private class MongoMapper extends MongoDBParse<Color> {

		@Override
		public Color parseObject(Document doc) {
			Color cate = new Color(doc.getLong("_id"), doc.getInteger("categoryId"), doc.getString("code"), doc.getInteger("views"),
					doc.getLong("photo"), doc.getBoolean("isFeature"), doc.getInteger("status"), doc.getDate("createTime").getTime(), 
					doc.getDate("updateTime").getTime());
			return cate;
		}

		@Override
		public Document toDocument(Color obj) {
			Document doc = new Document("_id", obj.getId())
					.append("categoryId", obj.getCategoryId())
					.append("code", obj.getCode())
					.append("views", obj.getViews())
					.append("photo", obj.getPhoto())
					.append("isFeature", obj.getIsFeature())
					.append("status", obj.getStatus());
			return doc;
		}

	}
	
	public static void main(String[] args) {
	}
}
