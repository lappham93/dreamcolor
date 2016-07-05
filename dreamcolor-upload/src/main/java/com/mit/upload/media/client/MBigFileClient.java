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

package com.mit.upload.media.client;

import com.mit.mbigfile.thrift.MBigFileService;
import com.mit.mbigfile.thrift.TMVideo;
import com.mit.mbigfile.thrift.TMVideoResult;
import com.mit.mbigfile.thrift.TMapMVideoResult;
import com.mit.thriftclient2.common.MCommonDef;
import com.mit.thriftclient2.common.MErrorDef;
import com.mit.thriftclient2.pool.MClientPoolUtil;
import com.mit.thriftclient2.pool.TClientPool;
import java.util.*;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Aug 21, 2015
 */
public class MBigFileClient {
    private static final Class _ThisClass = MBigFileClient.class;
	private static final Logger _Logger = LoggerFactory.getLogger(_ThisClass);
	private static final String _name = "mmedia";
	private TClientPool.BizzConfig _bizzCfg;
    
    private static MBigFileClient instance = new MBigFileClient();
    
    private MBigFileClient(){
        assert (_name != null && !_name.isEmpty());
		_initialize();
    }
    
    public static MBigFileClient getInstance(){
        return instance;
    }

	private void _initialize() {
        System.out.println("MBigFileClient._initialize");
		MClientPoolUtil.setDefaultPoolProp(_name //instName
				, null //host
				, null //auth
				, MCommonDef.TClientTimeoutMilisecsDefault //timeout
				, MCommonDef.TClientNRetriesDefault //nretry
				, MCommonDef.TClientMaxRdAtimeDefault //maxRdAtime
				, MCommonDef.TClientMaxWrAtimeDefault //maxWrAtime
		);
		MClientPoolUtil.createPools(_name, new MBigFileService.Client.Factory()); //auto create pools
		_bizzCfg = MClientPoolUtil.getBizzCfg(_name);
	}

	private TClientPool<MBigFileService.Client> getClientPool() {
		return (TClientPool<MBigFileService.Client>) MClientPoolUtil.getPool(_name);
	}

	private TClientPool.BizzConfig getBizzCfg() {
		return _bizzCfg;
	}
	///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	///error objects 
	///
	///e1001: NO_CONNECTION
	public static final TMVideoResult TMVideoResult_NO_CONNECTION = new TMVideoResult(MErrorDef.NO_CONNECTION);
    public static final TMapMVideoResult TMapMVideoResult_NO_CONNECTION = new TMapMVideoResult(MErrorDef.NO_CONNECTION);
	///e1002: BAD_CONNECTION
	public static final TMVideoResult TMVideoResult_BAD_CONNECTION = new TMVideoResult(MErrorDef.BAD_CONNECTION);
    public static final TMapMVideoResult TMapMVideoResult_BAD_CONNECTION = new TMapMVideoResult(MErrorDef.BAD_CONNECTION);
	///e1003: BAD_REQUEST
	public static final TMVideoResult TMVideoResult_BAD_REQUEST = new TMVideoResult(MErrorDef.BAD_REQUEST);
    public static final TMapMVideoResult TMapMVideoResult_BAD_REQUEST = new TMapMVideoResult(MErrorDef.BAD_REQUEST);

	///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	///common methods
	///
	public int ping() {
		TClientPool<MBigFileService.Client> pool = getClientPool();
		MBigFileService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			cli.ping();
			pool.returnClient(cli);
			return MErrorDef.SUCCESS;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MBigFileClient.ping", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MBigFileClient.ping", ex);
			return MErrorDef.BAD_REQUEST;
		}
	}
    
    ///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	///user define methods
	///
    
    public int putMVideo(TMVideo tmv) {
        TClientPool<MBigFileService.Client> pool = getClientPool();
		MBigFileService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			int err = cli.putMVideo(tmv);
			pool.returnClient(cli);
			return err;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MBigFileClient.putMVideo", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MBigFileClient.putMVideo", ex);
			return MErrorDef.BAD_REQUEST;
		}
    }
    
    public int multiPutMVideo(List<TMVideo> list) {
        TClientPool<MBigFileService.Client> pool = getClientPool();
		MBigFileService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			int err = cli.multiPutMVideo(list);
			pool.returnClient(cli);
			return err;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MBigFileClient.multiPutMVideo", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MBigFileClient.multiPutMVideo", ex);
			return MErrorDef.BAD_REQUEST;
		}
    }
    
    public void owPutMVideo(TMVideo tmv) {
        TClientPool<MBigFileService.Client> pool = getClientPool();
		MBigFileService.Client cli = pool.borrowClient();
		if (cli == null) {
			return;
		}
		try {
			cli.owPutMVideo(tmv);
			pool.returnClient(cli);
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MBigFileClient.owPutMVideo", ex);
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MBigFileClient.owPutMVideo", ex);
		}
    }
    
    public void owMultiPutMVideo(List<TMVideo> list) {
        TClientPool<MBigFileService.Client> pool = getClientPool();
		MBigFileService.Client cli = pool.borrowClient();
		if (cli == null) {
			return;
		}
		try {
			cli.owMultiPutMVideo(list);
			pool.returnClient(cli);
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MBigFileClient.owPutMVideo", ex);
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MBigFileClient.owPutMVideo", ex);
		}
    }
    
    public TMVideoResult getMVideo(long mvId) {
        TClientPool<MBigFileService.Client> pool = getClientPool();
		TClientPool.BizzConfig bCfg = getBizzCfg();
        //System.out.println("bCfg.getNRetry(): " + bCfg.getNRetry());
		for (int retry = 0; retry < bCfg.getNRetry(); ++retry) {
			MBigFileService.Client cli = pool.borrowClient();
			if (cli == null) {
				return TMVideoResult_NO_CONNECTION;
			}
			try {
				TMVideoResult ret = cli.getMVideo(mvId);
				pool.returnClient(cli);
				return ret;
			} catch (TTransportException ex) {
				pool.invalidateClient(cli, ex);
                _Logger.error("MBigFileClient.getMVideo", ex);
				continue;
			} catch (TException ex) {
				pool.invalidateClient(cli, ex);
                _Logger.error("MBigFileClient.getMVideo", ex);
				return TMVideoResult_BAD_REQUEST;
			} catch (Exception ex) {
				pool.invalidateClient(cli, ex);
                _Logger.error("MBigFileClient.getMVideo", ex);
				return TMVideoResult_BAD_REQUEST;
			}
		}
		return TMVideoResult_BAD_CONNECTION;
    }
    
    public TMapMVideoResult multiGetMVideo(List<Long> list) {
        TClientPool<MBigFileService.Client> pool = getClientPool();
		TClientPool.BizzConfig bCfg = getBizzCfg();
        //System.out.println("bCfg.getNRetry(): " + bCfg.getNRetry());
		for (int retry = 0; retry < bCfg.getNRetry(); ++retry) {
			MBigFileService.Client cli = pool.borrowClient();
			if (cli == null) {
				return TMapMVideoResult_NO_CONNECTION;
			}
			try {
				TMapMVideoResult ret = cli.multiGetMVideo(list);
				pool.returnClient(cli);
				return ret;
			} catch (TTransportException ex) {
				pool.invalidateClient(cli, ex);
                _Logger.error("MBigFileClient.getMVideo", ex);
				continue;
			} catch (TException ex) {
				pool.invalidateClient(cli, ex);
                _Logger.error("MBigFileClient.getMVideo", ex);
				return TMapMVideoResult_BAD_REQUEST;
			} catch (Exception ex) {
				pool.invalidateClient(cli, ex);
                _Logger.error("MBigFileClient.getMVideo", ex);
				return TMapMVideoResult_BAD_REQUEST;
			}
		}
		return TMapMVideoResult_BAD_CONNECTION;
    }
    
    public int updateMVideo(long mvId, TMVideo tmv) {
        TClientPool<MBigFileService.Client> pool = getClientPool();
		MBigFileService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			int err = cli.updateMVideo(mvId, tmv);
			pool.returnClient(cli);
			return err;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MBigFileClient.putMVideo", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MBigFileClient.putMVideo", ex);
			return MErrorDef.BAD_REQUEST;
		}
    }
    
    public int multiUpdateMVideo(Map<Long, TMVideo> map) {
        TClientPool<MBigFileService.Client> pool = getClientPool();
		MBigFileService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			int err = cli.multiUpdateMVideo(map);
			pool.returnClient(cli);
			return err;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MBigFileClient.putMVideo", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MBigFileClient.putMVideo", ex);
			return MErrorDef.BAD_REQUEST;
		}
    }
    
    public int deleteMVideo(long mvId) {
        TClientPool<MBigFileService.Client> pool = getClientPool();
		MBigFileService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			int err = cli.deleteMVideo(mvId);
			pool.returnClient(cli);
			return err;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MBigFileClient.putMVideo", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MBigFileClient.putMVideo", ex);
			return MErrorDef.BAD_REQUEST;
		}
    }
    
    public int multiDeleteMVideo(List<Long> list) {
        TClientPool<MBigFileService.Client> pool = getClientPool();
		MBigFileService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			int err = cli.multiDeleteMVideo(list);
			pool.returnClient(cli);
			return err;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MBigFileClient.putMVideo", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MBigFileClient.putMVideo", ex);
			return MErrorDef.BAD_REQUEST;
		}
    }
    
    
    
    
}
