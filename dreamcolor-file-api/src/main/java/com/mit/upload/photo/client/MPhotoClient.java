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

package com.mit.upload.photo.client;

import com.mit.mphoto.thrift.MPhotoService;
import com.mit.mphoto.thrift.TMPhoto;
import com.mit.mphoto.thrift.TMPhotoResult;
import com.mit.mphoto.thrift.TMapMPhotoResult;
import com.mit.thriftclient2.common.MCommonDef;
import com.mit.thriftclient2.common.MErrorDef;
import com.mit.thriftclient2.pool.MClientPoolUtil;
import com.mit.thriftclient2.pool.TClientPool;
import java.util.List;
import java.util.Map;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Sep 23, 2015
 */
public class MPhotoClient {
    private static final Class _ThisClass = MPhotoClient.class;
	private static final Logger _Logger = LoggerFactory.getLogger(_ThisClass);
	private static final String _name = "mphoto";
	private TClientPool.BizzConfig _bizzCfg;
    
    private static MPhotoClient instance = new MPhotoClient();
    
    private MPhotoClient(){
        assert (_name != null && !_name.isEmpty());
		_initialize();
    }
    
    public static MPhotoClient getInstance(){
        return instance;
    }

	private void _initialize() {
        System.out.println("MPhotoClient._initialize");
		MClientPoolUtil.setDefaultPoolProp(_name //instName
				, null //host
				, null //auth
				, MCommonDef.TClientTimeoutMilisecsDefault //timeout
				, MCommonDef.TClientNRetriesDefault //nretry
				, MCommonDef.TClientMaxRdAtimeDefault //maxRdAtime
				, MCommonDef.TClientMaxWrAtimeDefault //maxWrAtime
		);
		MClientPoolUtil.createPools(_name, new MPhotoService.Client.Factory()); //auto create pools
		_bizzCfg = MClientPoolUtil.getBizzCfg(_name);
	}

	private TClientPool<MPhotoService.Client> getClientPool() {
		return (TClientPool<MPhotoService.Client>) MClientPoolUtil.getPool(_name);
	}

	private TClientPool.BizzConfig getBizzCfg() {
		return _bizzCfg;
	}
	///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	///error objects 
	///
	///e1001: NO_CONNECTION
	public static final TMPhotoResult TMPhotoResult_NO_CONNECTION = new TMPhotoResult(MErrorDef.NO_CONNECTION);
    public static final TMapMPhotoResult TMapMPhotoResult_NO_CONNECTION = new TMapMPhotoResult(MErrorDef.NO_CONNECTION);
	///e1002: BAD_CONNECTION
	public static final TMPhotoResult TMPhotoResult_BAD_CONNECTION = new TMPhotoResult(MErrorDef.BAD_CONNECTION);
    public static final TMapMPhotoResult TMapMPhotoResult_BAD_CONNECTION = new TMapMPhotoResult(MErrorDef.BAD_CONNECTION);
	///e1003: BAD_REQUEST
	public static final TMPhotoResult TMPhotoResult_BAD_REQUEST = new TMPhotoResult(MErrorDef.BAD_REQUEST);
    public static final TMapMPhotoResult TMapMPhotoResult_BAD_REQUEST = new TMapMPhotoResult(MErrorDef.BAD_REQUEST);

	///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	///common methods
	///
	public int ping() {
		TClientPool<MPhotoService.Client> pool = getClientPool();
		MPhotoService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			cli.ping();
			pool.returnClient(cli);
			return MErrorDef.SUCCESS;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MPhotoClient.ping", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MPhotoClient.ping", ex);
			return MErrorDef.BAD_REQUEST;
		}
	}
    
    ///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	///user define methods
	///
    
    public int putMPhoto(TMPhoto tmp) {
        TClientPool<MPhotoService.Client> pool = getClientPool();
		MPhotoService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			int err = cli.putMPhoto(tmp);
			pool.returnClient(cli);
			return err;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MPhotoClient.putMPhoto", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MPhotoClient.putMPhoto", ex);
			return MErrorDef.BAD_REQUEST;
		}
    }
    
    public int multiPutMPhoto(List<TMPhoto> list) {
        TClientPool<MPhotoService.Client> pool = getClientPool();
		MPhotoService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			int err = cli.multiPutMPhoto(list);
			pool.returnClient(cli);
			return err;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MPhotoClient.multiPutMPhoto", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MPhotoClient.multiPutMPhoto", ex);
			return MErrorDef.BAD_REQUEST;
		}
    }
    
    public void owPutMPhoto(TMPhoto tmp) {
        TClientPool<MPhotoService.Client> pool = getClientPool();
		MPhotoService.Client cli = pool.borrowClient();
		if (cli == null) {
			return;
		}
		try {
			cli.owPutMPhoto(tmp);
			pool.returnClient(cli);
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MPhotoClient.owPutMPhoto", ex);
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MPhotoClient.owPutMPhoto", ex);
		}
    }
    
    public void owMultiPutMPhoto(List<TMPhoto> list) {
        TClientPool<MPhotoService.Client> pool = getClientPool();
		MPhotoService.Client cli = pool.borrowClient();
		if (cli == null) {
			return;
		}
		try {
			cli.owMultiPutMPhoto(list);
			pool.returnClient(cli);
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MPhotoClient.owPutMPhoto", ex);
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MPhotoClient.owPutMPhoto", ex);
		}
    }
    
    public TMPhotoResult getMPhoto(long mvId) {
        TClientPool<MPhotoService.Client> pool = getClientPool();
		TClientPool.BizzConfig bCfg = getBizzCfg();
        //System.out.println("bCfg.getNRetry(): " + bCfg.getNRetry());
		for (int retry = 0; retry < bCfg.getNRetry(); ++retry) {
			MPhotoService.Client cli = pool.borrowClient();
			if (cli == null) {
				return TMPhotoResult_NO_CONNECTION;
			}
			try {
				TMPhotoResult ret = cli.getMPhoto(mvId);
				pool.returnClient(cli);
				return ret;
			} catch (TTransportException ex) {
				pool.invalidateClient(cli, ex);
                _Logger.error("MPhotoClient.getMPhoto", ex);
				continue;
			} catch (TException ex) {
				pool.invalidateClient(cli, ex);
                _Logger.error("MPhotoClient.getMPhoto", ex);
				return TMPhotoResult_BAD_REQUEST;
			} catch (Exception ex) {
				pool.invalidateClient(cli, ex);
                _Logger.error("MPhotoClient.getMPhoto", ex);
				return TMPhotoResult_BAD_REQUEST;
			}
		}
		return TMPhotoResult_BAD_CONNECTION;
    }
    
    public TMapMPhotoResult multiGetMPhoto(List<Long> list) {
        TClientPool<MPhotoService.Client> pool = getClientPool();
		TClientPool.BizzConfig bCfg = getBizzCfg();
        //System.out.println("bCfg.getNRetry(): " + bCfg.getNRetry());
		for (int retry = 0; retry < bCfg.getNRetry(); ++retry) {
			MPhotoService.Client cli = pool.borrowClient();
			if (cli == null) {
				return TMapMPhotoResult_NO_CONNECTION;
			}
			try {
				TMapMPhotoResult ret = cli.multiGetMPhoto(list);
				pool.returnClient(cli);
				return ret;
			} catch (TTransportException ex) {
				pool.invalidateClient(cli, ex);
                _Logger.error("MPhotoClient.getMPhoto", ex);
				continue;
			} catch (TException ex) {
				pool.invalidateClient(cli, ex);
                _Logger.error("MPhotoClient.getMPhoto", ex);
				return TMapMPhotoResult_BAD_REQUEST;
			} catch (Exception ex) {
				pool.invalidateClient(cli, ex);
                _Logger.error("MPhotoClient.getMPhoto", ex);
				return TMapMPhotoResult_BAD_REQUEST;
			}
		}
		return TMapMPhotoResult_BAD_CONNECTION;
    }
    
    public int updateMPhoto(long mvId, TMPhoto tmp) {
        TClientPool<MPhotoService.Client> pool = getClientPool();
		MPhotoService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			int err = cli.updateMPhoto(mvId, tmp);
			pool.returnClient(cli);
			return err;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MPhotoClient.putMPhoto", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MPhotoClient.putMPhoto", ex);
			return MErrorDef.BAD_REQUEST;
		}
    }
    
    public int multiUpdateMPhoto(Map<Long, TMPhoto> map) {
        TClientPool<MPhotoService.Client> pool = getClientPool();
		MPhotoService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			int err = cli.multiUpdateMPhoto(map);
			pool.returnClient(cli);
			return err;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MPhotoClient.putMPhoto", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MPhotoClient.putMPhoto", ex);
			return MErrorDef.BAD_REQUEST;
		}
    }
    
    public int deleteMPhoto(long mvId) {
        TClientPool<MPhotoService.Client> pool = getClientPool();
		MPhotoService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			int err = cli.deleteMPhoto(mvId);
			pool.returnClient(cli);
			return err;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MPhotoClient.putMPhoto", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MPhotoClient.putMPhoto", ex);
			return MErrorDef.BAD_REQUEST;
		}
    }
    
    public int multiDeleteMPhoto(List<Long> list) {
        TClientPool<MPhotoService.Client> pool = getClientPool();
		MPhotoService.Client cli = pool.borrowClient();
		if (cli == null) {
			return MErrorDef.NO_CONNECTION;
		}
		try {
			int err = cli.multiDeleteMPhoto(list);
			pool.returnClient(cli);
			return err;
		} catch (TTransportException ex) {
			pool.invalidateClient(cli, ex);
            _Logger.error("MPhotoClient.putMPhoto", ex);
			return MErrorDef.BAD_CONNECTION;
		} catch (TException ex) {
			pool.invalidateClient(cli, ex);
			_Logger.error("MPhotoClient.putMPhoto", ex);
			return MErrorDef.BAD_REQUEST;
		}
    }
}
