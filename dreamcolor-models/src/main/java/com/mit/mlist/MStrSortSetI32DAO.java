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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Aug 27, 2015
 */
public class MStrSortSetI32DAO extends CommonDAO {
    private final Logger _logger = LoggerFactory.getLogger(MStrSortSetI32DAO.class);
    private final String suffix = "strsortseti32_";

	private String TABLE_NAME = "";
    private int sortType = MSortType.ASC.getValue();

	private MStrSortSetI32DAO() {
	}
    
    public MStrSortSetI32DAO(String tablename, MSortType sortType) {
        if(tablename == null || tablename.isEmpty()){
            throw new ExceptionInInitializerError("tablename must not null or empty.");
        }
        this.TABLE_NAME = tablename;
        if(sortType != null){
            this.sortType = sortType.getValue();
        }
	}

    public String getTableName() {
        return TABLE_NAME;
    }
    
    public int add(String id, int value){
        int ret = -1;
        if(dbSource != null) {
            try {
                Document filter = new Document("_id", suffix + id);
                Set<Integer> set = new HashSet<Integer>();
                set.add(value);
                Document update = new Document("$addToSet", new Document("data", new Document("$each", set).append("$sort", sortType)));
                UpdateOptions opt = new UpdateOptions().upsert(true);
                UpdateResult rs = dbSource.getCollection(TABLE_NAME).updateOne(filter, update, opt);
                //System.out.println("======================rs: " + rs);
                if(rs != null && rs.getMatchedCount() > 0) {
                    //rs: AcknowledgedUpdateResult{matchedCount=1, modifiedCount=0, upsertedId=null}
					ret = 0;
				} else{
                    // xử lý trư�?ng hợp khi insert lần đầu tiên.
                    if(rs != null && rs.getUpsertedId() != null && rs.getUpsertedId().isString()){
                        //rs: AcknowledgedUpdateResult{matchedCount=0, modifiedCount=0, upsertedId=BsonString{value='strsortseti32_3'}}
                        ret = 0;
                    }
                }
            } catch(final Exception e) {
				_logger.error("MStrSortSetI32DAO.add ", e);
			}
        }
        return ret;
    }
    
    public int addAll(String id, Set<Integer> list){
        int ret = -1;
        if(dbSource != null) {
            try {
                Document filter = new Document("_id", suffix + id);
                Document update = new Document("$addToSet", new Document("data", new Document("$each", list).append("$sort", sortType)));
                UpdateOptions opt = new UpdateOptions().upsert(true);
                UpdateResult rs = dbSource.getCollection(TABLE_NAME).updateOne(filter, update, opt);
                //System.out.println("=======================rs: " + rs);
                if(rs != null && rs.getMatchedCount() > 0) {
                    //rs: AcknowledgedUpdateResult{matchedCount=1, modifiedCount=0, upsertedId=null}
					ret = 0;
				} else{
                    // xử lý trư�?ng hợp khi insert lần đầu tiên.
                    if(rs != null && rs.getUpsertedId() != null && rs.getUpsertedId().isString()){
                        //rs: AcknowledgedUpdateResult{matchedCount=0, modifiedCount=0, upsertedId=BsonString{value='strsortseti32_3'}}
                        ret = 0;
                    }
                }
            } catch(final Exception e) {
				_logger.error("MStrSortSetI32DAO.addAll ", e);
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
					StrSortSetI32 sli = mapper.parseObject(doc);
                    ret = sli.getData();
				}
            } catch(final Exception e) {
				_logger.error("MStrSortSetI32DAO.get ", e);
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
				_logger.error("MStrSortSetI32DAO.removeValue ", e);
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
				_logger.error("MStrSortSetI32DAO.removeAll ", e);
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
				_logger.error("MStrSortSetI32DAO.delete ", e);
			}
        }
        return ret;
    }
    
    public class StrSortSetI32 {
        private String id;
        private Set<Integer> data;

        public StrSortSetI32(String id, Set<Integer> data) {
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
            return "StrSortSetI32{" + "id=" + id + ", data=" + data + '}';
        }
    }
    
    private class MongoMapper extends MongoDBParse<StrSortSetI32> {

		@Override
		public StrSortSetI32 parseObject(Document doc) {
            String id = doc.getString("_id");
            Set<Integer> data = new HashSet<Integer>((ArrayList<Integer>) doc.get("data"));
			StrSortSetI32 list = new StrSortSetI32(id, data);
			return list;
		}

		@Override
		public Document toDocument(StrSortSetI32 list) {
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
        String tablename = "TestStrSortSetI32";
        MStrSortSetI32DAO ssi32dao = new MStrSortSetI32DAO(tablename, MSortType.ASC);
        String id = "3";
        
        Random rd = new Random();
        Set<Integer> list0 = new HashSet<Integer>();
        for(int i = 1; i < 11; i++){
            list0.add(rd.nextInt(11));
        }
        System.out.println("===============Input list0: " + list0);
        int err = ssi32dao.addAll(id, list0);
        System.out.println("===============addAll err: " + err);
        
        Set<Integer> list1 = ssi32dao.get(id);
        System.out.println("===============get list1: " + list1);
        
        int err2 = ssi32dao.delete(id);
        System.out.println("===============delete err2: " + err2);
        
        Set<Integer> list3 = ssi32dao.get(id);
        System.out.println("===============get list3: " + list3);
    }
    
    public static void test2(){
        String tablename = "TestStrSortSetI32";
        MStrSortSetI32DAO ssi32dao = new MStrSortSetI32DAO(tablename, MSortType.ASC);
        Random rd = new Random();
        String id = "2";
        Set<Integer> list0 = new HashSet<Integer>();
        for(int i = 1; i < 11; i++){
            list0.add(rd.nextInt(11));
        }
        System.out.println("===============Input list0: " + list0);
        int err = ssi32dao.addAll(id, list0);
        System.out.println("===============addAll err: " + err);
        
        Set<Integer> list1 = ssi32dao.get(id);
        System.out.println("===============get list1: " + list1);
        
        Set<Integer> list2 = new HashSet<Integer>();
        for(int i = 1; i < 6; i++){
            list2.add(i);
        }
        int err2 = ssi32dao.removeAll(id, list2);
        System.out.println("===============removeAll: " + list2 + " err2: " + err2);
        
        Set<Integer> list3 = ssi32dao.get(id);
        System.out.println("===============get list3: " + list3);
    }
    
    public static void test1(){
        String tablename = "TestStrSortSetI32";
        MStrSortSetI32DAO ssi32dao = new MStrSortSetI32DAO(tablename, MSortType.ASC);
        String id = "1";
        int value = 1;
        int err = ssi32dao.add(id, value);
        System.out.println("===============add err: " + err);
        err = ssi32dao.add(id, value);
        System.out.println("===============add err: " + err);
        err = ssi32dao.add(id, 5);
        err = ssi32dao.add(id, 2);
        err = ssi32dao.add(id, 5);
        err = ssi32dao.add(id, 2);
        err = ssi32dao.add(id, 9);
        err = ssi32dao.add(id, 6);
        err = ssi32dao.add(id, 3);
        err = ssi32dao.add(id, 7);
        System.out.println("===============add err: " + err);
        
        Set<Integer> list = ssi32dao.get(id);
        System.out.println("===============get list: " + list);
        
        int err2 = ssi32dao.removeValue(id, value);
        System.out.println("===============removeValue: " + value + " err2: " + err2);
        
        Set<Integer> list2 = ssi32dao.get(id);
        System.out.println("===============get list2: " + list2);
    }
}
