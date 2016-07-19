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

package com.mit.photodb.app;

import com.mit.photodb.thriftserver.TRSingleServer;

/**
 *
 * @author nghiatc
 * @since Sep 22, 2015
 */
public class MainApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
//        TRSingleServer userphoto = new TRSingleServer("userphoto");
//        if (!userphoto.setupAndStart()) {
//            System.err.println("Could not start thrift servers userphoto! Exit now.");
//            System.exit(1);
//        }
        
        TRSingleServer productphoto = new TRSingleServer("productphoto");
        if (!productphoto.setupAndStart()) {
            System.err.println("Could not start thrift servers productphoto! Exit now.");
            System.exit(1);
        }
        
        TRSingleServer videophoto = new TRSingleServer("videophoto");
        if (!videophoto.setupAndStart()) {
            System.err.println("Could not start thrift servers videophoto! Exit now.");
            System.exit(1);
        }
        
        TRSingleServer bannerphoto = new TRSingleServer("bannerphoto");
        if (!bannerphoto.setupAndStart()) {
            System.err.println("Could not start thrift servers bannerphoto! Exit now.");
            System.exit(1);
        }
        
        TRSingleServer colorphoto = new TRSingleServer("colorphoto");
        if (!colorphoto.setupAndStart()) {
            System.err.println("Could not start thrift servers colorphoto! Exit now.");
            System.exit(1);
        }
        
        TRSingleServer distributorphoto = new TRSingleServer("distributorphoto");
        if (!distributorphoto.setupAndStart()) {
            System.err.println("Could not start thrift servers distributorphoto! Exit now.");
            System.exit(1);
        }
        
    }

}
