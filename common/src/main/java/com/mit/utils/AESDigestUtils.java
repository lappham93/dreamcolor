package com.mit.utils;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AESDigestUtils {
	private static final String CIPHER = "AES";
	private static final String HASH = "SHA-256";
	private static final String CHARSET = "utf-8";
	private static final IvParameterSpec iv = new IvParameterSpec("dkmobile@#3%&123".getBytes());

	public static String encrypt(String text, String secretKey) throws Exception {
		MessageDigest md = MessageDigest.getInstance(HASH);
		byte[] digestOfPassword = md.digest(secretKey.getBytes(CHARSET));
		byte[] keyBytes = digestOfPassword;

		SecretKey key = new SecretKeySpec(keyBytes, CIPHER);
		final Cipher cipher = Cipher.getInstance(CIPHER + "/CBC/NoPadding");		
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);

		byte[] plainTextBytes = text.getBytes(CHARSET);
		if(plainTextBytes.length % 16 > 0) {
			byte[] oldData = plainTextBytes;
			int length = oldData.length + (16 - (plainTextBytes.length % 16));
			plainTextBytes = new byte[length];
			System.arraycopy(oldData, 0, plainTextBytes, 0, oldData.length);
		}

	    byte[] buf = cipher.doFinal(plainTextBytes);
	    String base64EncryptedString = Base64.encodeBase64String(buf);

	    return base64EncryptedString;
	}

	public static String decrypt(String encryptedText, String secretKey) throws Exception {
	    byte[] message = Base64.decodeBase64(encryptedText);

		MessageDigest md = MessageDigest.getInstance(HASH);
		byte[] digestOfPassword = md.digest(secretKey.getBytes(CHARSET));
		byte[] keyBytes =digestOfPassword;
		SecretKey key = new SecretKeySpec(keyBytes, CIPHER);

		final Cipher cipher = Cipher.getInstance(CIPHER + "/CBC/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, key, iv);

		byte[] plainText = cipher.doFinal(message);

		return new String(plainText, "UTF-8");
	}
}
