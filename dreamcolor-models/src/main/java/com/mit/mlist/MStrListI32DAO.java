/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.mlist;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.dao.CommonDAO;
import com.mit.dao.MongoDBParse;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

/**
 *
 * @author nghiatc
 * @since Aug 25, 2015
 */
public class MStrListI32DAO extends CommonDAO {
    private final Logger _logger = LoggerFactory.getLogger(MStrListI32DAO.class);
    private final String suffix = "strlisti32_";

	private String TABLE_NAME = "";
    
    public MStrListI32DAO(String tablename) {
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
                Document update = new Document("$push", new Document("data", value));
                UpdateOptions opt = new UpdateOptions().upsert(true);
                UpdateResult rs = dbSource.getCollection(TABLE_NAME).updateOne(filter, update, opt);
                if(rs != null && rs.getModifiedCount() > 0) {
                    //rs: AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}
					ret = 0;
				} else{
                    // xử lý trư�?ng hợp khi insert lần đầu tiên.
                    if(rs != null && rs.getUpsertedId() != null && rs.getUpsertedId().isString()){
                        //rs: AcknowledgedUpdateResult{matchedCount=0, modifiedCount=0, upsertedId=BsonString{value='strlisti32_3'}}
                        ret = 0;
                    }
                }
            } catch(final Exception e) {
				_logger.error("MStrListI32DAO.add ", e);
			}
        }
        return ret;
    }
    
    public int addAll(String id, List<Integer> list){
        int ret = -1;
        if(dbSource != null) {
            try {
                Document filter = new Document("_id", suffix + id);
                Document update = new Document("$push", new Document("data", new Document("$each", list)));
                UpdateOptions opt = new UpdateOptions().upsert(true);
                UpdateResult rs = dbSource.getCollection(TABLE_NAME).updateOne(filter, update, opt);
                if(rs != null && rs.getModifiedCount() > 0) {
                    //rs: AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}
					ret = 0;
				} else{
                    // xử lý trư�?ng hợp khi insert lần đầu tiên.
                    if(rs != null && rs.getUpsertedId() != null && rs.getUpsertedId().isString()){
                        //rs: AcknowledgedUpdateResult{matchedCount=0, modifiedCount=0, upsertedId=BsonString{value='strlisti32_3'}}
                        ret = 0;
                    }
                }
            } catch(final Exception e) {
				_logger.error("MStrListI32DAO.addAll ", e);
			}
        }
        return ret;
    }
    
    public List<Integer> get(String id){
        List<Integer> ret = new ArrayList<Integer>();
        if(dbSource != null) {
            try {
                MongoMapper mapper = new MongoMapper();
                Document filter = new Document("_id", suffix + id);
                Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
                if(doc != null) {
					StrListI32 sli = mapper.parseObject(doc);
                    ret = sli.getData();
				}
            } catch(final Exception e) {
				_logger.error("MStrListI32DAO.get ", e);
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
				_logger.error("MStrListI32DAO.removeValue ", e);
			}
        }
        return ret;
    }
    
    public int removeAll(String id, List<Integer> list){
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
				_logger.error("MStrListI32DAO.removeAll ", e);
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
				_logger.error("MStrListI32DAO.delete ", e);
			}
        }
        return ret;
    }
    
    public class StrListI32 {
        private String id;
        private List<Integer> data;

        public StrListI32(String id, List<Integer> data) {
            this.id = id;
            this.data = data;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Integer> getData() {
            return data;
        }

        public void setData(List<Integer> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "StrListI32{" + "id=" + id + ", data=" + data + '}';
        }
    }
    
    private class MongoMapper extends MongoDBParse<StrListI32> {

		@Override
		public StrListI32 parseObject(Document doc) {
			StrListI32 list = new StrListI32(doc.getString("_id"), (List<Integer>) doc.get("data"));
			return list;
		}

		@Override
		public Document toDocument(StrListI32 list) {
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
        String tablename = "TestStrListI32";
        MStrListI32DAO li32dao = new MStrListI32DAO(tablename);
        String id = "6";
        
        List<Integer> list1 = li32dao.get(id);
        System.out.println("===============list1: " + list1);
        
        int err2 = li32dao.delete(id);
        System.out.println("===============err2: " + err2);
        
        List<Integer> list3 = li32dao.get(id);
        System.out.println("===============list3: " + list3);
    }
    
    public static void test2(){
        String tablename = "TestStrListI32";
        MStrListI32DAO li32dao = new MStrListI32DAO(tablename);
        String id = "5";
//        List<Integer> list0 = new ArrayList<Integer>();
//        for(int i = 1; i < 11; i++){
//            list0.add(i);
//        }
//        int err = li32dao.addAll(id, list0);
//        System.out.println("===============err: " + err);
        
        List<Integer> list1 = li32dao.get(id);
        System.out.println("===============list1: " + list1);
        
        List<Integer> list2 = new ArrayList<Integer>();
        for(int i = 1; i < 6; i++){
            list2.add(i);
        }
        int err2 = li32dao.removeAll(id, list2);
        System.out.println("===============err2: " + err2);
        
        List<Integer> list3 = li32dao.get(id);
        System.out.println("===============list3: " + list3);
    }
    
    public static void test1(){
        String tablename = "TestStrListI32";
        MStrListI32DAO li32dao = new MStrListI32DAO(tablename);
        String id = "4";
        int value = 1;
        int err = li32dao.add(id, value);
        err = li32dao.add(id, value);
        err = li32dao.add(id, value);
        err = li32dao.add(id, value);
        System.out.println("===============err: " + err);
        
        List<Integer> list = li32dao.get(id);
        System.out.println("===============list: " + list);
        
        int err2 = li32dao.removeValue(id, value);
        System.out.println("===============err2: " + err2);
        
        List<Integer> list2 = li32dao.get(id);
        System.out.println("===============list2: " + list2);
    }
    
}
