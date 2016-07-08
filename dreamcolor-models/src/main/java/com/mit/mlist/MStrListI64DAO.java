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
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Aug 25, 2015
 */
public class MStrListI64DAO extends CommonDAO {
    private final Logger _logger = LoggerFactory.getLogger(MStrListI64DAO.class);
    private final String suffix = "strlisti64_";

	private String TABLE_NAME = "";

	private MStrListI64DAO() {
	}
    
    public MStrListI64DAO(String tablename) {
        if(tablename == null || tablename.isEmpty()){
            throw new ExceptionInInitializerError("tablename must not null or empty.");
        }
        this.TABLE_NAME = tablename;
	}

    public String getTableName() {
        return TABLE_NAME;
    }
    
    public int add(String id, long value){
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
                        //rs: AcknowledgedUpdateResult{matchedCount=0, modifiedCount=0, upsertedId=BsonString{value='strlisti64_3'}}
                        ret = 0;
                    }
                }
            } catch(final Exception e) {
				_logger.error("MStrListI64DAO.add ", e);
			}
        }
        return ret;
    }
    
    public int addAll(String id, List<Long> list){
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
                        //rs: AcknowledgedUpdateResult{matchedCount=0, modifiedCount=0, upsertedId=BsonString{value='strlisti64_3'}}
                        ret = 0;
                    }
                }
            } catch(final Exception e) {
				_logger.error("MStrListI64DAO.addAll ", e);
			}
        }
        return ret;
    }
    
    public List<Long> get(String id){
        List<Long> ret = new ArrayList<Long>();
        if(dbSource != null) {
            try {
                MongoMapper mapper = new MongoMapper();
                Document filter = new Document("_id", suffix + id);
                Document doc = dbSource.getCollection(TABLE_NAME).find(filter).first();
                if(doc != null) {
					StrListI64 sli = mapper.parseObject(doc);
                    ret = sli.getData();
				}
            } catch(final Exception e) {
				_logger.error("MStrListI64DAO.get ", e);
			}
        }
        return ret;
    }
    
    public int removeValue(String id, long value){
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
				_logger.error("MStrListI64DAO.removeValue ", e);
			}
        }
        return ret;
    }
    
    public int removeAll(String id, List<Long> list){
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
				_logger.error("MStrListI64DAO.removeAll ", e);
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
				_logger.error("MStrListI64DAO.delete ", e);
			}
        }
        return ret;
    }
    
    public class StrListI64 {
        private String id;
        private List<Long> data;

        public StrListI64(String id, List<Long> data) {
            this.id = id;
            this.data = data;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Long> getData() {
            return data;
        }

        public void setData(List<Long> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "StrListI64{" + "id=" + id + ", data=" + data + '}';
        }
    }
    
    private class MongoMapper extends MongoDBParse<StrListI64> {

		@Override
		public StrListI64 parseObject(Document doc) {
			StrListI64 list = new StrListI64(doc.getString("_id"), (List<Long>) doc.get("data"));
			return list;
		}

		@Override
		public Document toDocument(StrListI64 list) {
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
        String tablename = "TestStrListI64";
        MStrListI64DAO li54dao = new MStrListI64DAO(tablename);
        String id = "3";
        
        List<Long> list0 = new ArrayList<Long>();
        for(int i = 1; i < 11; i++){
            list0.add(Long.valueOf(i));
        }
        int err = li54dao.addAll(id, list0);
        System.out.println("===============addAll err: " + err);
        
        List<Long> list1 = li54dao.get(id);
        System.out.println("===============get list1: " + list1);
        
        int err2 = li54dao.delete(id);
        System.out.println("===============delete err2: " + err2);
        
        List<Long> list3 = li54dao.get(id);
        System.out.println("===============get list3: " + list3);
    }
    
    public static void test2(){
        String tablename = "TestStrListI64";
        MStrListI64DAO li54dao = new MStrListI64DAO(tablename);
        String id = "2";
        List<Long> list0 = new ArrayList<Long>();
        for(int i = 1; i < 11; i++){
            list0.add(Long.valueOf(i));
        }
        int err = li54dao.addAll(id, list0);
        System.out.println("===============addAll err: " + err);
        
        List<Long> list1 = li54dao.get(id);
        System.out.println("===============get list1: " + list1);
        
        List<Long> list2 = new ArrayList<Long>();
        for(int i = 1; i < 6; i++){
            list2.add(Long.valueOf(i));
        }
        int err2 = li54dao.removeAll(id, list2);
        System.out.println("===============removeAll err2: " + err2);
        
        List<Long> list3 = li54dao.get(id);
        System.out.println("===============get list3: " + list3);
    }
    
    public static void test1(){
        String tablename = "TestStrListI64";
        MStrListI64DAO li54dao = new MStrListI64DAO(tablename);
        String id = "1";
        int value = 1;
        int err = li54dao.add(id, value);
        err = li54dao.add(id, value);
        err = li54dao.add(id, value);
        err = li54dao.add(id, value);
        System.out.println("===============err: " + err);
        
        List<Long> list = li54dao.get(id);
        System.out.println("===============list: " + list);
        
        int err2 = li54dao.removeValue(id, value);
        System.out.println("===============err2: " + err2);
        
        List<Long> list2 = li54dao.get(id);
        System.out.println("===============list2: " + list2);
    }
    
}
