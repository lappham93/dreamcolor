package com.mit.dao.product;


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
import com.mit.entities.product.Product;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class ProductDAO extends CommonDAO {
	private final Logger _logger = LoggerFactory.getLogger(ProductDAO.class);
	private static Lock _lock = new ReentrantLock();
	private static ProductDAO _instance;

	private String TABLE_NAME = "product";

	public static ProductDAO getInstance() {
		if(_instance == null) {
			_lock.lock();
			try {
				if(_instance == null) {
					_instance = new ProductDAO();
				}
			} finally {
				_lock.unlock();
			}
		}

		return _instance;
	}
	
    public int totalAll() {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				Document filter = new Document("status", new Document("$gt", 0));
				rs = (int)dbSource.getCollection(TABLE_NAME).count(filter);
			} catch(final Exception e) {
				_logger.error("totalAll ", e);
			}
		}
		return rs;
	}
    
    public List<Product> getSlideAll(int offset, int count, String fieldSort, boolean ascOrder) {
		List<Product> products = null;
		if (dbSource != null) {
			try {
				Document objFinder = new Document("status", new Document("$gt", 0));
				Document sort = new Document(fieldSort, ascOrder ? 1 : -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder).sort(sort).skip(offset).limit(count);
				if (doc != null) {
					products = new MongoMapper().parseList(doc);
				}
			} catch (final Exception e) {
				_logger.error("getSlideAll ", e);
			}
		}
		return products;
	}
    
    public List<Product> getSlideByCategory(int categoryId, int offset, int count, String fieldSort, boolean ascOrder) {
		List<Product> products = null;
		if (dbSource != null) {
			try {
				Document objFinder = new Document("categoryId", categoryId)
						.append("status", new Document("$gt", 0));
				Document sort = new Document(fieldSort, ascOrder ? 1 : -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder).sort(sort).skip(offset).limit(count);
				if (doc != null) {
					products = new MongoMapper().parseList(doc);
				}
			} catch (final Exception e) {
				_logger.error("getSlideByCategory ", e);
			}
		}
		return products;
	}
    
    public List<Product> getListRelateProduct(Product pro, String fieldSort, boolean ascOrder) {
		List<Product> products = null;
		if (dbSource != null) {
			try {
				Document objFinder = new Document("categoryId", pro.getCategoryId())
						.append("manufacturer", pro.getManufacturer())
						.append("model", pro.getModel())
						.append("status", new Document("$gt", 0));
				Document sort = new Document(fieldSort, ascOrder ? 1 : -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder).sort(sort);
				if (doc != null) {
					products = new MongoMapper().parseList(doc);
				}
			} catch (final Exception e) {
				_logger.error("getListRelateProduct ", e);
			}
		}
		return products;
	}
    
	public Product getById(long id) {
		Product product= null;
		if(dbSource != null) {
			try {
				Document objFinder = new Document("_id", id);
				Document doc = dbSource.getCollection(TABLE_NAME).find(objFinder).first();
				if(doc != null) {
					product= new MongoMapper().parseObject(doc);
				}
			} catch(final Exception e) {
				_logger.error("getById ", e);
			}
		}

		return product;
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
	
	public int insert(Product product) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				if(product.getId() <= 0) {
					product.setId(MIdGenLongDAO.getInstance(TABLE_NAME).getNext());
				}
				Document obj = mapper.toDocument(product);
				obj = mapper.buildInsertTime(obj);
				dbSource.getCollection(TABLE_NAME).insertOne(obj);
				rs = MongoErrorCode.SUCCESS;
			} catch(final Exception e) {
				_logger.error("insert ", e);
			}
		}

		return rs;
	}

	public int update(Product product) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document filter = new Document("_id", product.getId());
				Document obj = new Document("$set", mapper.toDocument(product));
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
				_logger.error("delete ", e);
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
				
				MIdGenLongDAO.getInstance(TABLE_NAME).reset();
			} catch(final Exception e) {
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

	private class MongoMapper extends MongoDBParse<Product> {

		@Override
		public Product parseObject(Document doc) {
			Product account = new Product(doc.getLong("_id"), doc.getInteger("categoryId"),
					doc.getString("manufacturer"), doc.getString("model"), doc.getString("name"),
					doc.getString("desc"), doc.getLong("primaryPhoto"), (List<Long>)doc.get("photos"), doc.getInteger("views"), 
					doc.getInteger("status"), doc.getDate("createTime").getTime(), doc.getDate("updateTime").getTime());
			return account;
		}

		@Override
		public Document toDocument(Product obj) {
			Document doc = new Document("_id", obj.getId())
					.append("categoryId", obj.getCategoryId())
					.append("manufacturer", obj.getManufacturer())
					.append("model", obj.getModel())
					.append("name", obj.getName())
					.append("desc", obj.getDesc())
					.append("primaryPhoto", obj.getPrimaryPhoto())
					.append("photos", obj.getPhotos())
					.append("views", obj.getViews())
					.append("status", obj.getStatus());
			return doc;
		}

	}
    
    public static void main(String[] args) {
    }
}
