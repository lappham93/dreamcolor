package com.mit.utils;

import org.apache.commons.codec.binary.Base64;

import com.mit.utils.AESDigestUtils;

public class IDAESCipher {
	private static final String _encryptKey = "71a4d3b85d56514e4e2b197f44f016b3";

	public static String encryptString(String id) {
		String eId = "";
		try {
			eId = AESDigestUtils.encrypt(id, _encryptKey);
		} catch (Exception e) {

		}
		
		return eId;
	}

	public static String decryptString(String hash) {
		String id = "";
		try {
			id = AESDigestUtils.decrypt(hash, _encryptKey).trim();
		} catch(Exception e) {

		}

		return id;
	}
}
