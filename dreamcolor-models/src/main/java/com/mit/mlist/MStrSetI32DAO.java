/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.mlist;

import com.mit.dao.CommonDAO;
import com.mit.dao.MongoDBParse;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Aug 27, 2015
 */
public class MStrSetI32DAO extends CommonDAO {
    private final Logger _logger = LoggerFactory.getLogger(MStrSetI32DAO.class);
    private final String suffix = "strseti32_";

	private String TABLE_NAME = "";

	private MStrSetI32DAO() {
	}
    
    public MStrSetI32DAO(String tablename) {
        if(tablename == null || tablename.isEmpty()){
            throw new ExceptionInInitializerError("tablename must not null or empty.");
        }
        this.TABLE_NAME = tablename;
	}

    public String getTableName() {
        return TABLE_NAME;
    }
    
    public int add(String id, int value){
        int ret = -1;
        if(dbSource != null) {
            try {
                Document filter = new Document("_id", suffix + id);
                Document update = new Document("$addToSet", new Document("data", value));
                UpdateOptions opt = new UpdateOptions().upsert(true);
                UpdateResult rs = dbSource.getCollection(TABLE_NAME).updateOne(filter, update, opt);
                //System.out.println("======================rs: " + rs);
                if(rs != null && rs.getMatchedCount() > 0) {
                    //rs: AcknowledgedUpdateResult{matchedCount=1, modifiedCount=0, upsertedId=null}
					ret = 0;
				} else{
                    // xử lý trư�?ng hợp khi insert lần đầu tiên.
                    if(rs != null && rs.getUpsertedId() != null && rs.getUpsertedId().isString()){
                        //rs: AcknowledgedUpdateResult{matchedCount=0, modifiedCount=0, upsertedId=BsonString{value='strseti32_3'}}
                        ret = 0;
                    }
                }
            } catch(final Exception e) {
				_logger.error("MStrSetI32DAO.add ", e);
			}
        }
        return ret;
    }
    
    public int addAll(String id, Set<Integer> list){
        int ret = -1;
        if(dbSource != null) {
            try {
                Document filter = new Document("_id", suffix + id);
                Document update = new Document("$addToSet", new Document("data", new Document("$each", list)));
                UpdateOptions opt = new UpdateOptions().upsert(true);
                UpdateResult rs = dbSource.getCollection(TABLE_NAME).updateOne(filter, update, opt);
                //System.out.println("=======================rs: " + rs);
                if(rs != null && rs.getMatchedCount() > 0) {
                    //rs: AcknowledgedUpdateResult{matchedCount=1, modifiedCount=0, upsertedId=null}
					ret = 0;
				} else{
                    // xử lý trư�?ng hợp khi insert lần đầu tiên.
                    if(rs != null && rs.getUpsertedId() != null && rs.getUpsertedId().isString()){
                        //rs: AcknowledgedUpdateResult{matchedCount=0, modifiedCount=0, upsertedId=BsonString{value='strseti32_3'}}
                        ret = 0;
                    }
                }
            } catch(final Exception e) {
				_logger.error("MStrSetI32DAO.addAll ", e);
			}
        }
        return ret;
    }
    
    public Set<Integer> get(String id){
        Set<Integer> ret = new HashSet<Integer>();
        if(dbSource != null) {
            try {
                MongoMapper mapper = new MongoMapper();
                Document filter = new Document("_id", suffix + id);
                Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
                if(doc != null) {
					StrSetI32 sli = mapper.parseObject(doc);
                    ret = sli.getData();
				}
            } catch(final Exception e) {
				_logger.error("MStrSetI32DAO.get ", e);
			}
        }
        return ret;
    }
    
    public int removeValue(String id, int value){
        int ret = -1;
        if(dbSource != null) {
            try {
                Document filter = new Document("_id", suffix + id);
                Document update = new Document("$pull", new Document("data", value));
                UpdateResult rs = dbSource.getCollection(TABLE_NAME).updateOne(filter, update);
                if(rs != null && rs.getModifiedCount() > 0) {
					ret = 0;
				}
            } catch(final Exception e) {
				_logger.error("MStrSetI32DAO.removeValue ", e);
			}
        }
        return ret;
    }
    
    public int removeAll(String id, Set<Integer> list){
        int ret = -1;
        if(dbSource != null) {
            try {
                Document filter = new Document("_id", suffix + id);
                Document update = new Document("$pullAll", new Document("data", list));
                UpdateResult rs = dbSource.getCollection(TABLE_NAME).updateOne(filter, update);
                if(rs != null && rs.getModifiedCount() > 0) {
					ret = 0;
				}
            } catch(final Exception e) {
				_logger.error("MStrSetI32DAO.removeAll ", e);
			}
        }
        return ret;
    }
    
    public int delete(String id){
        int ret = -1;
        if(dbSource != null) {
            try {
                Document filter = new Document("_id", suffix + id);
                DeleteResult rs = dbSource.getCollection(TABLE_NAME).deleteOne(filter);
                if(rs != null && rs.getDeletedCount() > 0) {
					ret = 0;
				}
            } catch(final Exception e) {
				_logger.error("MStrSetI32DAO.delete ", e);
			}
        }
        return ret;
    }
    
    public class StrSetI32 {
        private String id;
        private Set<Integer> data;

        public StrSetI32(String id, Set<Integer> data) {
            this.id = id;
            this.data = data;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Set<Integer> getData() {
            return data;
        }

        public void setData(Set<Integer> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "StrSetI32{" + "id=" + id + ", data=" + data + '}';
        }
    }
    
    private class MongoMapper extends MongoDBParse<StrSetI32> {

		@Override
		public StrSetI32 parseObject(Document doc) {
            String id = doc.getString("_id");
            Set<Integer> data = new HashSet<Integer>((ArrayList<Integer>) doc.get("data"));
			StrSetI32 list = new StrSetI32(id, data);
			return list;
		}

		@Override
		public Document toDocument(StrSetI32 list) {
			Document doc = new Document("_id", list.getId()).append("data", list.getData());
			return doc;
		}
	}
    
    public static void main(String[] args) {
        //1.
        //test1();
        
        //2.
        //test2();
        
        //3.
        //test3();
        
        
    }
    
    public static void test3(){
        String tablename = "TestStrSetI32";
        MStrSetI32DAO si32dao = new MStrSetI32DAO(tablename);
        String id = "3";
        
        Random rd = new Random();
        Set<Integer> list0 = new HashSet<Integer>();
        for(int i = 1; i < 11; i++){
            list0.add(rd.nextInt(11));
        }
        System.out.println("===============Input list0: " + list0);
        int err = si32dao.addAll(id, list0);
        System.out.println("===============addAll err: " + err);
        
        Set<Integer> list1 = si32dao.get(id);
        System.out.println("===============get list1: " + list1);
        
        int err2 = si32dao.delete(id);
        System.out.println("===============delete err2: " + err2);
        
        Set<Integer> list3 = si32dao.get(id);
        System.out.println("===============get list3: " + list3);
    }
    
    public static void test2(){
        String tablename = "TestStrSetI32";
        MStrSetI32DAO si32dao = new MStrSetI32DAO(tablename);
        Random rd = new Random();
        String id = "2";
        Set<Integer> list0 = new HashSet<Integer>();
        for(int i = 1; i < 11; i++){
            list0.add(rd.nextInt(11));
        }
        System.out.println("===============Input list0: " + list0);
        int err = si32dao.addAll(id, list0);
        System.out.println("===============addAll err: " + err);
        
        Set<Integer> list1 = si32dao.get(id);
        System.out.println("===============get list1: " + list1);
        
        Set<Integer> list2 = new HashSet<Integer>();
        for(int i = 1; i < 6; i++){
            list2.add(i);
        }
        int err2 = si32dao.removeAll(id, list2);
        System.out.println("===============removeAll err2: " + err2);
        
        Set<Integer> list3 = si32dao.get(id);
        System.out.println("===============get list3: " + list3);
    }
    
    public static void test1(){
        String tablename = "TestStrSetI32";
        MStrSetI32DAO si32dao = new MStrSetI32DAO(tablename);
        String id = "1";
        int value = 1;
        int err = si32dao.add(id, value);
        System.out.println("===============add err: " + err);
        err = si32dao.add(id, value);
        System.out.println("===============add err: " + err);
        err = si32dao.add(id, 5);
        err = si32dao.add(id, 2);
        err = si32dao.add(id, 5);
        err = si32dao.add(id, 2);
        System.out.println("===============add err: " + err);
        
        Set<Integer> list = si32dao.get(id);
        System.out.println("===============get list: " + list);
        
        int err2 = si32dao.removeValue(id, value);
        System.out.println("===============removeValue err2: " + err2);
        
        Set<Integer> list2 = si32dao.get(id);
        System.out.println("===============get list2: " + list2);
    }
}
