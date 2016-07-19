/*
 * Copyright 2015 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mit.photodb.handler;

import com.mit.mphoto.thrift.MPhotoService;
import com.mit.mphoto.thrift.TMPhoto;
import com.mit.mphoto.thrift.TMPhotoResult;
import com.mit.mphoto.thrift.TMapMPhotoResult;
import com.mit.rocksdb.MRSerializer;
import com.mit.rocksdb.RDBConnection;
import com.mit.rocksdb.RDBPool;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Sep 23, 2015
 */
public class TMPhotoHandler implements MPhotoService.Iface {
    private final Logger logger = LoggerFactory.getLogger(TMPhotoHandler.class);
    private final String prefix = "mphoto";
    private RDBPool rdbPool;
    private MRSerializer mrs;
    private TSerializer ts;
    private TDeserializer td;
    private static TMPhotoHandler instance = new TMPhotoHandler();
    private TMPhotoHandler(){
        rdbPool = new RDBPool(prefix);
        mrs = new MRSerializer();
        ts = new TSerializer(new TBinaryProtocol.Factory());
        td = new TDeserializer(new TBinaryProtocol.Factory());
    }
    public static TMPhotoHandler getInstance(){
        return instance;
    }

    @Override
    public int putMPhoto(TMPhoto tmp) throws TException {
        RDBConnection con = rdbPool.getRDBConnection();
        try {
            byte[] k = mrs.serializeLong(tmp.getId());
            byte[] v = ts.serialize(tmp);
            if(k != null && v != null){
                con.put(k, v);
            } else{
                return -1;
            }
        } catch (Exception ex) {
            logger.error("TMPhotoHandler.putMPhoto ", ex);
            return -1;
        } finally{
            con.closeRDB();
        }
        return 0;
    }

    @Override
    public int multiPutMPhoto(List<TMPhoto> list) throws TException {
        RDBConnection con = rdbPool.getRDBConnection();
        try {
            if(list != null && !list.isEmpty()){
                for(TMPhoto tmp : list){
                    byte[] k = mrs.serializeLong(tmp.getId());
                    byte[] v = ts.serialize(tmp);
                    if(k != null && v != null){
                        con.put(k, v);
                    }
                }
            } else{
                return -1;
            }
        } catch (Exception ex) {
            logger.error("TMPhotoHandler.multiPutMPhoto ", ex);
            return -1;
        } finally{
            con.closeRDB();
        }
        return 0;
    }

    @Override
    public void owPutMPhoto(TMPhoto tmp) throws TException {
        RDBConnection con = rdbPool.getRDBConnection();
        try {
            byte[] k = mrs.serializeLong(tmp.getId());
            byte[] v = ts.serialize(tmp);
            if(k != null && v != null){
                con.put(k, v);
            }
        } catch (Exception ex) {
            logger.error("TMPhotoHandler.owPutMPhoto ", ex);
        } finally{
            con.closeRDB();
        }
    }

    @Override
    public void owMultiPutMPhoto(List<TMPhoto> list) throws TException {
        RDBConnection con = rdbPool.getRDBConnection();
        try {
            if(list != null && !list.isEmpty()){
                for(TMPhoto tmp : list){
                    byte[] k = mrs.serializeLong(tmp.getId());
                    byte[] v = ts.serialize(tmp);
                    if(k != null && v != null){
                        con.put(k, v);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("TMPhotoHandler.owMultiPutMPhoto ", ex);
        } finally{
            con.closeRDB();
        }
    }

    @Override
    public TMPhotoResult getMPhoto(long ptId) throws TException {
        TMPhotoResult ret = new TMPhotoResult(-1);
        RDBConnection con = rdbPool.getRDBConnection();
        try {
            if(ptId >= 0){
                byte[] k = mrs.serializeLong(ptId);
                TMPhoto tmp = new TMPhoto();
                byte[] v = con.get(k);
                td.deserialize(tmp, v);
                if(tmp != null && tmp.getId() >= 0){
                    ret.setError(0);
                    ret.setValue(tmp);
                }
            }
        } catch (Exception ex) {
            logger.error("TMPhotoHandler.getMPhoto ", ex);
        } finally{
            con.closeRDB();
        }
        return ret;
    }

    @Override
    public TMapMPhotoResult multiGetMPhoto(List<Long> list) throws TException {
        TMapMPhotoResult ret = new TMapMPhotoResult(-1);
        RDBConnection con = rdbPool.getRDBConnection();
        try {
            if(list != null && !list.isEmpty()){
                Map<Long, TMPhoto> mapData = new HashMap<Long, TMPhoto>();
                for(long id : list){
                    if(id >= 0){
                        byte[] k = mrs.serializeLong(id);
                        TMPhoto tmp = new TMPhoto();
                        byte[] v = con.get(k);
                        td.deserialize(tmp, v);
                        if(tmp != null && tmp.getId() >= 0){
                            mapData.put(id, tmp);
                        }
                    }
                }
                if(mapData != null && !mapData.isEmpty()){
                    ret.setError(0);
                    ret.setValue(mapData);
                }
            }
        } catch (Exception ex) {
            logger.error("TMPhotoHandler.multiGetMPhoto ", ex);
        } finally{
            con.closeRDB();
        }
        return ret;
    }

    @Override
    public int updateMPhoto(long ptId, TMPhoto tmp) throws TException {
        RDBConnection con = rdbPool.getRDBConnection();
        try {
            byte[] k = mrs.serializeLong(ptId);
            byte[] v = ts.serialize(tmp);
            if(k != null && v != null){
                con.put(k, v);
            } else{
                return -1;
            }
        } catch (Exception ex) {
            logger.error("TMPhotoHandler.updateMPhoto ", ex);
            return -1;
        } finally{
            con.closeRDB();
        }
        return 0;
    }

    @Override
    public int multiUpdateMPhoto(Map<Long, TMPhoto> map) throws TException {
        RDBConnection con = rdbPool.getRDBConnection();
        try {
            if(map != null && !map.isEmpty()){
                for(long id : map.keySet()){
                    TMPhoto tmp = map.get(id);
                    byte[] k = mrs.serializeLong(id);
                    byte[] v = ts.serialize(tmp);
                    if(k != null && v != null){
                        con.put(k, v);
                    }
                }
            } else{
                return -1;
            }
        } catch (Exception ex) {
            logger.error("TMPhotoHandler.multiUpdateMPhoto ", ex);
            return -1;
        } finally{
            con.closeRDB();
        }
        return 0;
    }

    @Override
    public int deleteMPhoto(long ptId) throws TException {
        RDBConnection con = rdbPool.getRDBConnection();
        try {
            if(ptId >= 0){
                byte[] k = mrs.serializeLong(ptId);
                if(k != null){
                    con.remove(k);
                } else{
                    return -1;
                }
            } else{
                return -1;
            }
        } catch (Exception ex) {
            logger.error("TMPhotoHandler.deleteMPhoto ", ex);
            return -1;
        } finally{
            con.closeRDB();
        }
        return 0;
    }

    @Override
    public int multiDeleteMPhoto(List<Long> list) throws TException {
        RDBConnection con = rdbPool.getRDBConnection();
        try {
            if(list != null && !list.isEmpty()){
                for(long mvId : list){
                    if(mvId >= 0){
                        byte[] k = mrs.serializeLong(mvId);
                        if(k != null){
                            con.remove(k);
                        }
                    }
                }
            } else{
                return -1;
            }
        } catch (Exception ex) {
            logger.error("TMPhotoHandler.multiDeleteMPhoto ", ex);
            return -1;
        } finally{
            con.closeRDB();
        }
        return 0;
    }

    @Override
    public void ping() throws TException {
        //logger.info("-----------------TMPhotoHandler.ping-----------------");
    }
    
}
