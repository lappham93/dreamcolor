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
import com.mit.entities.mid.MIdGen;
import com.mongodb.client.model.FindOneAndUpdateOptions;

/**
 *
 * @author nghiatc
 * @since Aug 11, 2015
 */
public class MIdGenDAO extends CommonDAO {
    private final Logger _logger = LoggerFactory.getLogger(MIdGenDAO.class);
    private static Map<String, MIdGenDAO> mapMID = new ConcurrentHashMap<String, MIdGenDAO>();
	private static Lock _lock = new ReentrantLock();
    private String name;

	private final String TABLE_NAME = "midgen";

	public static MIdGenDAO getInstance(String name) {
        if(name == null || name.isEmpty()){
            return null;
        }
        MIdGenDAO _instance = mapMID.containsKey(name) ? mapMID.get(name) : null;
		if(_instance == null) {
			_lock.lock();
			try {
                _instance = mapMID.containsKey(name) ? mapMID.get(name) : null;
				if(_instance == null) {
					_instance = new MIdGenDAO(name);
                    mapMID.put(name, _instance);
				}
			} finally {
				_lock.unlock();
			}
		}

		return _instance;
	}

	private MIdGenDAO() {
	}
    
    private MIdGenDAO(String name) {
        this.name = name;
	}

    public String getName() {
        return name;
    }
    
    public int getNext(){
        int ret = -1;
        MIdGen mid = null;
        if(dbSource != null) {
            try {
                MongoMapper mapper = new MongoMapper();
                Document filter = new Document("_id", name);
                Document update = new Document("$inc", new Document("seq", 1));
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
    
    public int getMaxId(){
        int ret = -1;
        MIdGen mid = null;
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
                Document update = new Document("$set", new Document("seq", 0));
                FindOneAndUpdateOptions opt = new FindOneAndUpdateOptions().upsert(true);
                Document doc = dbSource.getCollection(TABLE_NAME).findOneAndUpdate(filter, update, opt);
            } catch(final Exception e) {
				_logger.error("reset ", e);
			}
        }
    }
    
    private class MongoMapper extends MongoDBParse<MIdGen> {

		@Override
		public MIdGen parseObject(Document doc) {
			MIdGen account = new MIdGen(doc.getString("_id"), doc.getInteger("seq"));
			return account;
		}

		@Override
		public Document toDocument(MIdGen obj) {
			Document doc = new Document("_id", obj.getName()).append("seq", obj.getSeq());
			return doc;
		}
	}
    
//    public static void main(String[] args) {
//        MIdGenDAO nid = MIdGenDAO.getInstance("nghiaabc");
//        int maxId = nid.getMaxId();
//        System.out.println("===============maxId: " + maxId);
//        
//        int nextId = nid.getNext();
//        System.out.println("===============nextId: " + nextId);
//    }
}
