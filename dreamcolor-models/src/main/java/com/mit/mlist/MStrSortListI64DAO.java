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
import java.util.Random;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Aug 25, 2015
 */
public class MStrSortListI64DAO extends CommonDAO {
    private final Logger _logger = LoggerFactory.getLogger(MStrSortListI64DAO.class);
    private final String suffix = "strsortlisti64_";

	private String TABLE_NAME = "";
    private int sortType = MSortType.ASC.getValue();

	private MStrSortListI64DAO() {
	}
    
    public MStrSortListI64DAO(String tablename, MSortType sortType) {
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
    
    public int add(String id, long value){
        int ret = -1;
        if(dbSource != null) {
            try {
                Document filter = new Document("_id", suffix + id);
                List<Long> list = new ArrayList<Long>();
                list.add(value);
                Document update = new Document("$push", new Document("data", new Document("$each", list).append("$sort", sortType)));
                UpdateOptions opt = new UpdateOptions().upsert(true);
                UpdateResult rs = dbSource.getCollection(TABLE_NAME).updateOne(filter, update, opt);
                //System.out.println("=======================rs: " + rs);
                if(rs != null && rs.getModifiedCount() > 0) {
                    //rs: AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}
					ret = 0;
				} else{
                    // xử lý trư�?ng hợp khi insert lần đầu tiên.
                    if(rs != null && rs.getUpsertedId() != null && rs.getUpsertedId().isString()){
                        //rs: AcknowledgedUpdateResult{matchedCount=0, modifiedCount=0, upsertedId=BsonString{value='strsortlisti64_3'}}
                        ret = 0;
                    }
                }
            } catch(final Exception e) {
				_logger.error("MStrSortListI64DAO.add ", e);
			}
        }
        return ret;
    }
    
    public int addAll(String id, List<Long> list){
        int ret = -1;
        if(dbSource != null) {
            try {
                Document filter = new Document("_id", suffix + id);
                Document update = new Document("$push", new Document("data", new Document("$each", list).append("$sort", sortType)));
                UpdateOptions opt = new UpdateOptions().upsert(true);
                UpdateResult rs = dbSource.getCollection(TABLE_NAME).updateOne(filter, update, opt);
                //System.out.println("=======================rs: " + rs);
                if(rs != null && rs.getModifiedCount() > 0) {
                    //rs: AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}
					ret = 0;
				} else{
                    // xử lý trư�?ng hợp khi insert lần đầu tiên.
                    if(rs != null && rs.getUpsertedId() != null && rs.getUpsertedId().isString()){
                        //rs: AcknowledgedUpdateResult{matchedCount=0, modifiedCount=0, upsertedId=BsonString{value='strsortlisti64_3'}}
                        ret = 0;
                    }
                }
            } catch(final Exception e) {
				_logger.error("MStrSortListI64DAO.addAll ", e);
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
					StrSortListI64 sli = mapper.parseObject(doc);
                    ret = sli.getData();
				}
            } catch(final Exception e) {
				_logger.error("MStrSortListI64DAO.get ", e);
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
				_logger.error("MStrSortListI64DAO.removeValue ", e);
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
				_logger.error("MStrSortListI64DAO.removeAll ", e);
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
				_logger.error("MStrSortListI64DAO.delete ", e);
			}
        }
        return ret;
    }
    
    public class StrSortListI64 {
        private String id;
        private List<Long> data;

        public StrSortListI64(String id, List<Long> data) {
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
            return "StrSortListI64{" + "id=" + id + ", data=" + data + '}';
        }
    }
    
    private class MongoMapper extends MongoDBParse<StrSortListI64> {

		@Override
		public StrSortListI64 parseObject(Document doc) {
			StrSortListI64 list = new StrSortListI64(doc.getString("_id"), (List<Long>) doc.get("data"));
			return list;
		}

		@Override
		public Document toDocument(StrSortListI64 list) {
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
        String tablename = "TestStrSortListI64";
        MStrSortListI64DAO sli64dao = new MStrSortListI64DAO(tablename, MSortType.ASC);
        String id = "3";
        Random rd = new Random();
        List<Long> list0 = new ArrayList<Long>();
        for(int i = 1; i < 11; i++){
            list0.add(Long.valueOf(rd.nextInt(11)));
        }
        int err = sli64dao.addAll(id, list0);
        System.out.println("===============addAll err: " + err);
        
        List<Long> list1 = sli64dao.get(id);
        System.out.println("===============get list1: " + list1);
        
        int err2 = sli64dao.delete(id);
        System.out.println("===============delete err2: " + err2);
        
        List<Long> list3 = sli64dao.get(id);
        System.out.println("===============get list3: " + list3);
    }
    
    public static void test2(){
        String tablename = "TestStrSortListI64";
        MStrSortListI64DAO sli64dao = new MStrSortListI64DAO(tablename, MSortType.ASC);
        Random rd = new Random();
        String id = "2";
        List<Long> list0 = new ArrayList<Long>();
        for(int i = 1; i < 11; i++){
            list0.add(Long.valueOf(rd.nextInt(11)));
        }
        int err = sli64dao.addAll(id, list0);
        System.out.println("===============addAll err: " + err);
        
        List<Long> list1 = sli64dao.get(id);
        System.out.println("===============get list1: " + list1);
        
        List<Long> list2 = new ArrayList<Long>();
        for(int i = 1; i < 6; i++){
            list2.add(Long.valueOf(i));
        }
        int err2 = sli64dao.removeAll(id, list2);
        System.out.println("===============removeAll err2: " + err2);
        
        List<Long> list3 = sli64dao.get(id);
        System.out.println("===============get list3: " + list3);
    }
    
    public static void test1(){
        String tablename = "TestStrSortListI64";
        MStrSortListI64DAO sli64dao = new MStrSortListI64DAO(tablename, MSortType.ASC);
        String id = "1";
        int value = 5;
        int err = sli64dao.add(id, 5);
        System.out.println("===============add err: " + err);
        err = sli64dao.add(id, 2);
        err = sli64dao.add(id, 7);
        err = sli64dao.add(id, 4);
        err = sli64dao.add(id, 3);
        err = sli64dao.add(id, 8);
        System.out.println("===============add err: " + err);
        
        List<Long> list = sli64dao.get(id);
        System.out.println("===============get list: " + list);
        
        int err2 = sli64dao.removeValue(id, 5);
        err2 = sli64dao.removeValue(id, 7);
        System.out.println("===============removeValue err2: " + err2);
        
        List<Long> list2 = sli64dao.get(id);
        System.out.println("===============get list2: " + list2);
    }
    
    
    
}
