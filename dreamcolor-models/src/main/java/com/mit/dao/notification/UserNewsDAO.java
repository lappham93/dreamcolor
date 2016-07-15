package com.mit.dao.notification;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
import com.mit.entities.notification.UserNews;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.UpdateResult;

/*
 * _id : id cua DeviceToken
 */
public class UserNewsDAO extends CommonDAO {
	private final Logger _logger = LoggerFactory.getLogger(UserNewsDAO.class);
	private static Lock _lock = new ReentrantLock();
	private static UserNewsDAO _instance;

	public final String TABLE_NAME = "user_news";

	public static UserNewsDAO getInstance() {
		if(_instance == null) {
			_lock.lock();
			try {
				if(_instance == null) {
					_instance = new UserNewsDAO();
				}
			} finally {
				_lock.unlock();
			}
		}

		return _instance;
	}

	private UserNewsDAO() {
	}
	
	public long addItem(int userId, UserNews item) {
		long rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {				
				Document objFinder = new Document("_id", userId);
				Document update = new Document("$push", new Document("newsItems", new Document("$each", Arrays.asList(new MongoMapper().toDocument(item)))
					.append("$position", 0))).append("$inc", new Document("newCount", 1L));
				UpdateResult doc = dbSource.getCollection(TABLE_NAME).updateOne(objFinder, update, new UpdateOptions().upsert(true));
				rs = MongoErrorCode.SUCCESS;
//				rs = (int)doc.getModifiedCount() + (doc.getUpsertedId() != null ? 1 : 0);
//				
//				if(rs > 0) {
//					update = new Document("$inc", new Document("newCount", rs));
//					doc = dbSource.getCollection(TABLE_NAME).updateOne(objFinder, update);
//				}
			} catch(final Exception e) {
				_logger.error("addItem ", e);
			}
		}
		return rs;
	}
	
	public long addItemSocial(int userId, UserNews item) {
		long rs = clearItem(userId, item.getuId());
		if (rs >= 0) {
			rs = addItem(userId, item);
		}
		
		return rs;
	}

	
	public long addItems(int userId, List<UserNews> items) {
		long rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {				
				Document objFinder = new Document("_id", userId);
				List<Document> objs = new LinkedList<Document>();
				MongoMapper mapper = new MongoMapper();
				for (UserNews item: items) {
					objs.add(mapper.toDocument(item));
				}
				Document update = new Document("$push", new Document("newsItems", new Document("$each", objs)
					.append("$position", 0))).append("$inc", new Document("newCount", (long)items.size()));
				UpdateResult doc = dbSource.getCollection(TABLE_NAME).updateOne(objFinder, update, new UpdateOptions().upsert(true));

				rs = MongoErrorCode.SUCCESS;
			} catch(final Exception e) {
				_logger.error("addItems ", e);
			}
		}
		return rs;
	}
	
	public long addItemToUserList(List<Integer> userIds, UserNews item) {
		long rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {				
				List<WriteModel<Document>> updateModels = new LinkedList<WriteModel<Document>>();

				Document update = new Document("$push", new Document("newsItems", new Document("$each", Arrays.asList(new MongoMapper().toDocument(item)))
					.append("$position", 0))).append("$inc", new Document("newCount", 1L));
				UpdateOptions updateOptions = new UpdateOptions().upsert(true);
				for (int userId: userIds) {
					Document objFinder = new Document("_id", userId);
					updateModels.add(new UpdateOneModel<Document>(objFinder, update, updateOptions));
				}
				
				BulkWriteResult doc = dbSource.getCollection(TABLE_NAME).bulkWrite(updateModels);
				
				rs = MongoErrorCode.SUCCESS;
//				rs = (int)doc.getModifiedCount() + (doc.getUpsertedId() != null ? 1 : 0);
//				
//				if(rs > 0) {
//					update = new Document("$inc", new Document("newCount", rs));
//					doc = dbSource.getCollection(TABLE_NAME).updateOne(objFinder, update);
//				}
			} catch(final Exception e) {
				_logger.error("addItemToUserList ", e);
			}
		}
		return rs;
	}
	
//	public int getNextUId(int userId){
//        int ret = -1;
//        if(dbSource != null) {
//            try {
//                Document filter = new Document("_id", userId);
//                Document update = new Document("$inc", new Document("maxUId", 1));
//                FindOneAndUpdateOptions opt = new FindOneAndUpdateOptions().upsert(true);
//                Document doc = dbSource.getCollection(TABLE_NAME).findOneAndUpdate(filter, update, opt);
//                if(doc != null) {
//                    ret = doc.getInteger("maxUId", 0) + 1;
//				} else{
//                    ret = 1;
//                }
//            } catch(final Exception e) {
//				_logger.error("getNext ", e);
//			}
//        }
//        return ret;
//    }
	
	public List<UserNews> getNews(int userId, int from, int count) {
		List<UserNews> newsItems = Collections.emptyList();
		if(dbSource != null) {
			try {
				Document objFinder = new Document("_id", userId);
				Document projection = new Document("newsItems", new Document("$slice", Arrays.asList(from, count)));
				Document doc = dbSource.getCollection(TABLE_NAME).find(objFinder).projection(projection).first();
				if(doc != null && !doc.isEmpty()) {					
					newsItems = new MongoMapper().parseList((List)doc.get("newsItems"));
				}
			} catch(final Exception e) {
				_logger.error("getNews ", e);
			}
		}
		return newsItems;
	}
	
	public List<Integer> getUsers(long newsId) {
		List<Integer> newsItems = Collections.emptyList();
		if(dbSource != null) {
			try {
				Document objFinder = new Document("newsItems._id", newsId);
				Document projection = new Document("_id", 1);
				FindIterable<Document> docs = dbSource.getCollection(TABLE_NAME).find(objFinder).projection(projection);
				if(docs != null) {					
					Document doc = null;
					MongoCursor<Document> cursor = docs.iterator();
					newsItems = new LinkedList<Integer>();
					while(cursor != null && cursor.hasNext() && (doc = cursor.next()) != null) {
						newsItems.add(doc.getLong("_id").intValue());
					}
				}
			} catch(final Exception e) {
				_logger.error("getUsers", e);
			}
		}
		return newsItems;
	}
	
	public long countNewItems(int userId) {
		long total = 0;
		if(dbSource != null) {
			try {
				Document objFinder = new Document("_id", userId);
				Document doc = dbSource.getCollection(TABLE_NAME).find(objFinder).first();
				if(doc != null && !doc.isEmpty()) {
					total = doc.getLong("newCount");
				}
			} catch(final Exception e) {
				_logger.error("countNewItems ", e);
			}
		}
		return total;
	}
	
//	public long countNewItems2(int userId) {
//		long total = 0;
//		if(dbSource != null) {
//			try {
//				Document objFinder = new Document("_id", userId);
//				Document doc = dbSource.getCollection(TABLE_NAME).find(objFinder).first();
//				if(doc != null && !doc.isEmpty()) {
//					List<Document> uns = (List<Document>) doc.get("newsItems");
//					if (uns != null) {
//						for (Document un : uns) {
//							if (!un.getBoolean("viewed")) {
//								total ++;
//							}
//						}
//					}
//				}
//			} catch(final Exception e) {
//				_logger.error("countNewsItems2 ", e);
//			}
//		}
//		return total;
//	}
	
	public int resetCount(int userId) {
		int total = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				Document objFinder = new Document("_id", userId);
				Document update = new Document("$set", new Document("newCount", 0L));
				UpdateResult ur = dbSource.getCollection(TABLE_NAME).updateOne(objFinder, update);
				total = (int)ur.getModifiedCount();
			} catch(final Exception e) {
				_logger.error("resetCount ", e);
			}
		}
		return total;
	}
	
	public int clearItems(long fromTime) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				Document objFinder = new Document();
				Document update = new Document("$pull", new Document("newsItems", new Document("createTime", new Document("$lt", new Date(fromTime)))));
				UpdateResult doc = dbSource.getCollection(TABLE_NAME).updateMany(objFinder, update);

				rs = (int)doc.getModifiedCount();
			} catch(final Exception e) {
				_logger.error("removeItems ", e);
			}
		}
		return rs;
	}
	
	public int clearItem(long id, long itemId) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				Document objFinder = new Document("_id", id);
				Document update = new Document("$pull", new Document("newsItems", new Document("_id", itemId)));
				UpdateResult doc = dbSource.getCollection(TABLE_NAME).updateOne(objFinder, update);

				rs = (int)doc.getModifiedCount();
			} catch(final Exception e) {
				_logger.error("removeItems ", e);
			}
		}
		return rs;
	}
	
	public int updateView(int userId, List<Long> ids) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if(dbSource != null) {
			try {
				List<WriteModel<Document>> updateModels = new LinkedList<WriteModel<Document>>();

				Document update = new Document("$set", new Document("newsItems.$.viewed", true));
				for (long id: ids) {
					Document objFinder = new Document("_id", userId).append("newsItems._id", id);
					updateModels.add(new UpdateOneModel<Document>(objFinder, update));
				}
				
				BulkWriteResult doc = dbSource.getCollection(TABLE_NAME).bulkWrite(updateModels);

				rs = doc.getModifiedCount();
				
				if(rs > 0) {
					Document objFinder = new Document("_id", userId)
							.append("newCount", new Document("$gt", 0));
					update = new Document("$inc", new Document("newCount", -rs));
					dbSource.getCollection(TABLE_NAME).updateOne(objFinder, update);
				}
			} catch(final Exception e) {
				_logger.error("updateView ", e);
			}
		}
		return rs;
	}
	
	public int unReceiveNotif(int userId, boolean unReceive) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			try {
				Document filter = new Document("_id", userId);
				Document update = new Document("$set", new Document("unRecvNotif", unReceive));
				UpdateResult ur = dbSource.getCollection(TABLE_NAME).updateOne(filter, update, new UpdateOptions().upsert(true));
				rs = (int) ur.getModifiedCount();
			} catch (final Exception e) {
				_logger.error("unRecvNotif ", e);
			}
		}
		
		return rs;
	}
	
	/**
	 * 
	 * @param userId
	 * @return: -1 : server error
	 * 			 0 : false
	 * 			 1 : true
	 */
	public int isReceiveNotif(int userId) {
		int rs = MongoErrorCode.NOT_CONNECT;
		if (dbSource != null) {
			rs = 1;
			Document filter = new Document("_id", userId);
			Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
			if (doc != null) {
				boolean unRecv = doc.getBoolean("unRecvNotif", false);
				if (unRecv) {
					rs = 0;
				}
			}
		}
		
		return rs;
	}
	
	private class MongoMapper extends MongoDBParse<UserNews> {
		@Override
		public UserNews parseObject(Document doc) {
			UserNews userNews = new UserNews(doc.getLong("_id"),
					doc.getBoolean("viewed", false),
					doc.getDate("createTime").getTime());
			return userNews;
		}

		@Override
		public Document toDocument(UserNews obj) {
			Document doc = new Document("_id", obj.getuId())
					.append("viewed", obj.isViewed())
					.append("createTime", new Date());
			return doc;
		}
	}
	
	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		UserNewsDAO.getInstance().clearItems(cal.getTimeInMillis());
	}
}
