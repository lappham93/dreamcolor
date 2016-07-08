package com.mit.dao.product;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.constants.MongoErrorCode;
import com.mit.dao.CommonDAO;
import com.mit.dao.MongoDBParse;
import com.mit.dao.mid.MIdGenDAO;
import com.mit.entities.product.Category;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class CategoryDAO extends CommonDAO {
	private final Logger _logger = LoggerFactory.getLogger(CategoryDAO.class);
	private static Lock _lock = new ReentrantLock();
	private static CategoryDAO _instance;

	public final String TABLE_NAME = "product_category";

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

	private CategoryDAO() {
	}
	
	 public List<Category> listAllIgnoreStatus() {		
			List<Category> Categorys = Collections.emptyList();
			if(dbSource != null) {
				try {
					Document objFinder = new Document();
					Document sortBy = new Document("position", -1).append("updateTime", -1);
					FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder).sort(sortBy);
					if(doc != null) {
						Categorys = new MongoMapper().parseList(doc);
					}
				} catch(final Exception e) {
					_logger.error("listAllIgnoreStatus ", e);
				}
			}

			return Categorys;
		}

    public List<Category> listAll() {		
		List<Category> Categorys = Collections.emptyList();
		if(dbSource != null) {
			try {
				Document objFinder = new Document("status", new Document("$gt", 0));
				Document sortBy = new Document("position", -1).append("createTime", -1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder).sort(sortBy);
				if(doc != null) {
					Categorys = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("listAll ", e);
			}
		}

		return Categorys;
	}
    
	public List<Category> listSubCategories(String path, boolean isRoot) {		
		List<Category> Categorys = Collections.emptyList();
		if(dbSource != null) {
			try {
				Document objFinder = new Document("path", (isRoot ? "/^,": "/,") + path + ",/").append("status", new Document("$gt", 0));
				Document sortBy = new Document("position", 1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder).sort(sortBy);
				if(doc != null) {
					Categorys = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("listSubCategories ", e);
			}
		}

		return Categorys;
	}
    
    public List<Category> listSubCategoriesAll(String path, boolean isRoot) {		
		List<Category> Categorys = Collections.emptyList();
		if(dbSource != null) {
			try {
				Document objFinder = new Document("path", (isRoot ? "/^,": "/,") + path + ",/").append("status", new Document("$gt", 0));
				Document sortBy = new Document("position", 1);
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder).sort(sortBy);
				if(doc != null) {
					Categorys = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("listSubCategories ", e);
			}
		}

		return Categorys;
	}
	
	public List<Integer> listSubCategoryIds(String path, boolean isRoot) {		
		List<Integer> categories = new LinkedList<Integer>();
		if(dbSource != null) {
			try {
				Document objFinder = new Document("path", Pattern.compile((isRoot ? "^": "") + path)).append("status", new Document("$gt", 0));
				Document sortBy = new Document("position", 1);
				Document projection = new Document("_id", 1);
				FindIterable<Document> docs = dbSource.getCollection(TABLE_NAME).find(objFinder).sort(sortBy).projection(projection);
				if(docs != null) {
					categories = new LinkedList<Integer>();
					Document doc;
					MongoCursor<Document> cursor = docs.iterator();
					while(cursor != null && cursor.hasNext() && (doc = cursor.next()) != null) {
						categories.add(doc.getInteger("_id"));
					}
				}
			} catch(final Exception e) {
				_logger.error("listSubCategoryIds", e);
			}
		}

		return categories;
	}
	
	public Category getById(int id) {
		Category category = null;
		if(dbSource != null) {
			try {
				Document objFinder = new Document("_id", id);
				Document doc = dbSource.getCollection(TABLE_NAME).find(objFinder).first();
				if(doc != null) {
					category = new MongoMapper().parseObject(doc);
				}
			} catch(final Exception e) {
				_logger.error("getById ", e);
			}
		}

		return category;
	}
	
	public Category getByName(String name) {
		Category category = null;
		if(dbSource != null) {
			try {
				Document objFinder = new Document("name", name);
				Document doc = dbSource.getCollection(TABLE_NAME).find(objFinder).first();
				if(doc != null) {
					category = new MongoMapper().parseObject(doc);
				}
			} catch(final Exception e) {
				_logger.error("getByName ", e);
			}
		}

		return category;
	}

	public int insert(Category category) {	
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				if(category.getId() <= 0) {
					category.setId(MIdGenDAO.getInstance(TABLE_NAME).getNext());
				}
				Document obj = mapper.toDocument(category);
				obj = mapper.buildInsertTime(obj);
				dbSource.getCollection(TABLE_NAME).insertOne(obj);
				rs = MongoErrorCode.SUCCESS;
			} catch(final Exception e) {
				_logger.error("insert ", e);
			}
		}

		return rs;
	}

	public int update(Category Category) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document filter = new Document("_id", Category.getId());
				Document obj = new Document("$set", mapper.toDocument(Category));
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
				
				MIdGenDAO.getInstance(TABLE_NAME).reset();
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
    
	private class MongoMapper extends MongoDBParse<Category> {

		@Override
		public Category parseObject(Document doc) {
			Category account = new Category(doc.getInteger("_id"),
					doc.getString("name"), doc.getString("path"), 
					doc.getInteger("parentId"), doc.getInteger("position"),
					doc.getInteger("status"),
					doc.getDate("createTime").getTime(), 
					doc.getDate("updateTime").getTime());
			return account;
		}

		@Override
		public Document toDocument(Category obj) {
			Document doc = new Document("_id", obj.getId())
					.append("name", obj.getName())
					.append("path", obj.getPath())
					.append("parentId", obj.getParentId())
					.append("position", obj.getPosition())
					.append("status", obj.getStatus());
			return doc;
		}

        
	}
    
    public static void main(String[] args) {
    }
}
