package com.mit.utils;

import org.apache.commons.codec.binary.Base64;

import com.mit.utils.ByteUtils;
import com.mit.utils.StringUtils;
import com.mit.utils.TEA;

public class IDCipher {
	private static TEA tea = new TEA("spabee@#$!DKMazx".getBytes());

	public static String encryptInt(int id) {
		return StringUtils.bytesToHexString(tea.encrypt(ByteUtils.intToByte(id)));
	}

	public static int decryptInt(String hash) {
		int id = -1;
		try {
			id = ByteUtils.byteToInt(tea.decrypt(StringUtils.hexStringToBytes(hash)));
		} catch(Exception e) {

		}

		return id;
	}

	public static String encryptLong(long id) {
		return StringUtils.bytesToHexString(tea.encrypt(ByteUtils.longToByte(id)));
	}

	public static long decryptLong(String hash) {
		long id = -1;
		try {
			id = ByteUtils.byteToLong(tea.decrypt(StringUtils.hexStringToBytes(hash)));
		} catch(Exception e) {

		}

		return id;
	}

	public static String encryptString(String id) {
		return Base64.encodeBase64String(tea.encrypt(id.getBytes()));
	}

	public static String decryptString(String hash) {
		String id = "";
		try {
			id = new String(tea.decrypt(Base64.decodeBase64(hash)));
		} catch(Exception e) {

		}

		return id;
	}
}
