package com.mit.utils;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class AuthenticateUtils {

	public static String generateSession(String userName) {
		StringBuilder session = new StringBuilder(Base64.encodeBase64String(DigestUtils.sha256(userName + System.nanoTime())));
//		session.insert(2, RandomStringUtils.random(3));
//		session.insert(11, RandomStringUtils.random(6));
//		session.insert(26, RandomStringUtils.random(9));
		return session.toString();
	}

	public static String hashPassword(String realPass, String salt) {
		StringBuilder hash = new StringBuilder(Base64.encodeBase64String(realPass.getBytes()).replace("=", ""));
		String hashSalt = Base64.encodeBase64String(salt.getBytes()).replace("=", "");
		hash.insert(hash.length() / 2, hashSalt);
		String hPass = Base64.encodeBase64String(DigestUtils.sha256(hash.toString())).replace("=", "");
		return hPass;
	}

	public static void main(String[] args) {
		SecureRandom secureRandom = new SecureRandom();
        int seedByteCount = 50;
        byte[] seed = secureRandom.generateSeed(seedByteCount);
        secureRandom.setSeed(seed);
        String random = String.valueOf(secureRandom.nextLong());
        String s = Base64.encodeBase64String(seed);
        System.out.println(random + " -- " + s);
	}
}
