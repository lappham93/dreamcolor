/*
 * Copyright 2016 nghiatc.
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

package com.mit.photodb.thriftserver;

import com.mit.mphoto.thrift.MPhotoService;
import com.mit.photodb.handler.TRSinglePhotoHandler;
import com.mit.thriftserver.ThriftServers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Jan 21, 2016
 */
public class TRSingleServer {
    private final Logger logger = LoggerFactory.getLogger(TRSingleServer.class);
    private String prefix;
    private TRSingleServer(){}

    public TRSingleServer(String prefix) {
        this.prefix = prefix;
    }
    
    public boolean setupAndStart() {
        if(prefix == null || prefix.isEmpty()){
            throw new ExceptionInInitializerError("Prefix config of TRSingleServer must not null or empty.");
        }
        ThriftServers servers = new ThriftServers(prefix);
        MPhotoService.Processor processor = new MPhotoService.Processor(TRSinglePhotoHandler.getInstance(prefix));
        servers.setup(processor);
        System.out.println(servers.getInfo());
        logger.info(servers.getInfo());
        return servers.start();
    }
}
