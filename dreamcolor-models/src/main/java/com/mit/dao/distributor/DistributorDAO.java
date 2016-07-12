package com.mit.dao.distributor;


import java.util.LinkedList;
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
import com.mit.entities.distributor.Distributor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class DistributorDAO extends CommonDAO {
	private final Logger _logger = LoggerFactory.getLogger(DistributorDAO.class);
	private static Lock _lock = new ReentrantLock();
	private static DistributorDAO _instance;

	private String TABLE_NAME = "distributor";

	public static DistributorDAO getInstance() {
		if(_instance == null) {
			_lock.lock();
			try {
				if(_instance == null) {
					_instance = new DistributorDAO();
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
				rs = (int) dbSource.getCollection(TABLE_NAME).count(filter);
			} catch (Exception e) {
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
				rs = (int) dbSource.getCollection(TABLE_NAME).count(filter);
			} catch (Exception e) {
				_logger.error("totalAllIgnoreStatus ", e);
			}
		}
		
		return rs;
	}
	
	public List<Distributor> getSliceByState(String state, String countryCode, int from, int size, String fieldSort, boolean ascOrder) {
		List<Distributor> distributors = null;
		if(dbSource != null) {
			try {
				Document objFinder = new Document("status", new Document("$gt", 0));
				
				if (!countryCode.isEmpty()) {
					objFinder = objFinder.append("countryCode", countryCode);
				}
				if (!state.isEmpty()) {
					objFinder = objFinder.append("state", state);
				}				
				Document sort = new Document(fieldSort, ascOrder ? 1 : -1);
				
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder).sort(sort).skip(from).limit(size);
				if(doc != null) {
					distributors = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("getSliceByState ", e);
			}
		}

		return distributors;
	}
	
	public List<Distributor> getSliceIgnoreStatus(String state, String countryCode, int from, int size, String fieldSort, boolean ascOrder) {
		List<Distributor> distributors = null;
		if(dbSource != null) {
			try {
				Document objFinder = new Document();
				
				if (!countryCode.isEmpty()) {
					objFinder = objFinder.append("countryCode", countryCode);
				}
				if (!state.isEmpty()) {
					objFinder = objFinder.append("state", state);
				}				
				Document sort = new Document(fieldSort, ascOrder ? 1 : -1);
				
				FindIterable<Document> doc = dbSource.getCollection(TABLE_NAME).find(objFinder).sort(sort).skip(from).limit(size);
				if(doc != null) {
					distributors = new MongoMapper().parseList(doc);
				}
			} catch(final Exception e) {
				_logger.error("getSliceIgnoreStatus ", e);
			}
		}

		return distributors;
	}
	
	public Distributor getById(int id) {
		Distributor productOption = null;
		if(dbSource != null) {
			try {
				Document objFinder = new Document("_id", id);
				Document doc = dbSource.getCollection(TABLE_NAME).find(objFinder).first();
				if(doc != null) {
					productOption = new MongoMapper().parseObject(doc);
				}
			} catch(final Exception e) {
				_logger.error("getById ", e);
			}
		}

		return productOption;
	}
    
	public int insert(Distributor distributor) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				if(distributor.getId() <= 0) {
					distributor.setId(MIdGenDAO.getInstance(TABLE_NAME).getNext());
				}
				Document obj = mapper.toDocument(distributor);
				obj = mapper.buildInsertTime(obj);
				dbSource.getCollection(TABLE_NAME).insertOne(obj);
				rs = MongoErrorCode.SUCCESS;
			} catch(final Exception e) {
				_logger.error("insert ", e);
			}
		}

		return rs;
	}
	
	public int insertBatch(List<Distributor> distributors) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				List<Document> objs = new LinkedList<Document>();
				for (Distributor Distributor: distributors) {
					if(Distributor.getId() <= 0) {
						Distributor.setId(MIdGenDAO.getInstance(TABLE_NAME).getNext());
					}
					Document obj = mapper.toDocument(Distributor);
					obj = mapper.buildInsertTime(obj);
					objs.add(obj);
				}
				dbSource.getCollection(TABLE_NAME).insertMany(objs);
				rs = MongoErrorCode.SUCCESS;
			} catch(final Exception e) {
				_logger.error("insertBatch ", e);
			}
		}

		return rs;
	}

	public int update(Distributor distributor) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				MongoMapper mapper = new MongoMapper();
				Document filter = new Document("_id", distributor.getId());
				Document obj = new Document("$set", mapper.toDocument(distributor));
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

	private class MongoMapper extends MongoDBParse<Distributor> {

		@Override
		public Distributor parseObject(Document doc) {
			Distributor account = new Distributor(doc.getInteger("_id"), doc.getString("name"),
					doc.getString("contactName"), doc.getString("addressLine1"), 
					doc.getString("addressLine2"), doc.getString("city"), doc.getString("state"), 
					doc.getString("country"), doc.getString("zipCode"), doc.getString("phone"), doc.getLong("photo"), doc.getString("webLink"), 
					doc.getString("countryCode"), doc.getInteger("status"), doc.getDate("createTime").getTime(), doc.getDate("updateTime").getTime());
			return account;
		}

		@Override
		public Document toDocument(Distributor obj) {
			Document doc = new Document("_id", obj.getId())
					.append("name", obj.getName())
					.append("contactName", obj.getContactName())
					.append("addressLine1", obj.getAddressLine1())
					.append("addressLine2", obj.getAddressLine2())
					.append("city", obj.getCity())
					.append("state", obj.getState())
					.append("country", obj.getCountry())
					.append("zipCode", obj.getZipCode())
					.append("phone", obj.getPhone())
					.append("photo", obj.getPhoto())
					.append("webLink", obj.getWebLink())
					.append("countryCode", obj.getCountryCode())
					.append("status", obj.getStatus());
			return doc;
		}

	}
}
