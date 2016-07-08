/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.dao.mid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.CommonDAO;
import com.mit.dao.MongoDBParse;
import com.mit.entities.mid.MIdGenLong;
import com.mongodb.client.model.FindOneAndUpdateOptions;

/**
 *
 * @author nghiatc
 * @since Aug 20, 2015
 */
public class MIdGenLongDAO extends CommonDAO {
    private final Logger _logger = LoggerFactory.getLogger(MIdGenLongDAO.class);
    private static Map<String, MIdGenLongDAO> mapMLID = new ConcurrentHashMap<String, MIdGenLongDAO>();
	private static Lock _lock = new ReentrantLock();
    private String name;

	private final String TABLE_NAME = "mlidgen";

	public static MIdGenLongDAO getInstance(String name) {
        if(name == null || name.isEmpty()){
            return null;
        }
        MIdGenLongDAO _instance = mapMLID.containsKey(name) ? mapMLID.get(name) : null;
		if(_instance == null) {
			_lock.lock();
			try {
                _instance = mapMLID.containsKey(name) ? mapMLID.get(name) : null;
				if(_instance == null) {
					_instance = new MIdGenLongDAO(name);
                    mapMLID.put(name, _instance);
				}
			} finally {
				_lock.unlock();
			}
		}

		return _instance;
	}

	private MIdGenLongDAO() {
	}
    
    private MIdGenLongDAO(String name) {
        this.name = name;
	}

    public String getName() {
        return name;
    }
    
    public long getNext(){
        long ret = -1;
        MIdGenLong mid = null;
        if(dbSource != null) {
            try {
                MongoMapper mapper = new MongoMapper();
                Document filter = new Document("_id", name);
                Document update = new Document("$inc", new Document("seq", 1L));
                FindOneAndUpdateOptions opt = new FindOneAndUpdateOptions().upsert(true);
                Document doc = dbSource.getCollection(TABLE_NAME).findOneAndUpdate(filter, update, opt);
                if(doc != null) {
					mid = mapper.parseObject(doc);
                    ret = mid.getSeq() + 1;
				} else{
                    ret = 1;
                }
            } catch(final Exception e) {
				_logger.error("getNext ", e);
			}
        }
        return ret;
    }
    
    public long getMaxId(){
        long ret = -1;
        MIdGenLong mid = null;
        if(dbSource != null) {
            try {
                MongoMapper mapper = new MongoMapper();
                Document filter = new Document("_id", name);
                Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
                if(doc != null) {
					mid = mapper.parseObject(doc);
				}
                ret = mid.getSeq();
            } catch(final Exception e) {
				_logger.error("getNext ", e);
			}
        }
        return ret;
    }
    
    public void reset(){
        if(dbSource != null) {
            try {
                Document filter = new Document("_id", name);
                Document update = new Document("$set", new Document("seq", 0L));
                FindOneAndUpdateOptions opt = new FindOneAndUpdateOptions().upsert(true);
                Document doc = dbSource.getCollection(TABLE_NAME).findOneAndUpdate(filter, update, opt);
            } catch(final Exception e) {
				_logger.error("reset ", e);
			}
        }
    }
    
    private class MongoMapper extends MongoDBParse<MIdGenLong> {

		@Override
		public MIdGenLong parseObject(Document doc) {
			MIdGenLong account = new MIdGenLong(doc.getString("_id"), doc.getLong("seq"));
			return account;
		}

		@Override
		public Document toDocument(MIdGenLong obj) {
			Document doc = new Document("_id", obj.getName()).append("seq", obj.getSeq());
			return doc;
		}
	}
    
//    public static void main(String[] args) {
//        MIdGenLongDAO nid = MIdGenLongDAO.getInstance("nghiaidLad");
//        long maxId = nid.getMaxId();
//        System.out.println("===============maxId: " + maxId);
//        
//        long nextId = nid.getNext();
//        System.out.println("===============nextId: " + nextId);
//        
////        long maxId = nid.getMaxId();
////        System.out.println("===============maxId: " + maxId);
//    }
}
