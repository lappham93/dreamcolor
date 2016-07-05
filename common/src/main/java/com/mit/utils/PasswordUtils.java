package com.mit.utils;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.mit.utils.PasswordUtils;

public class PasswordUtils {
	
	public static boolean checkPassword(String password, String username, boolean requireStrong) {
		if (requireStrong)
			return isStrongPassword(password, username);
		else
			return isMediumPassword(password, username);
	}

	public static boolean isStrongPassword(String password, String username) {
		if (password.length() < 7 || password.length() > 20) {
			return false;
		}
		
		if (password.toLowerCase().contains(username)) {
			return false;
		}

		boolean isContainDigit = false;
		boolean isContainSpecialCharacter = false;
		for (int i = 0; i < password.length(); i++) {
			final char c = password.charAt(i);
			if (Character.isDigit(c)) {
				isContainDigit = true;
			} else if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
				isContainSpecialCharacter = true;
			}
		}

		return isContainDigit & isContainSpecialCharacter;
	}
	
	public static boolean isMediumPassword(String password, String username) {
		if (password.length() < 6) {
			return false;
		}
		
		return true;
	}
	
	public static String random(String username, boolean requireStrong) {
		if (requireStrong)
			return randomStrongPassword(username);
		else
			return randomMediumPassword(username);
	}
	
	public static String randomStrongPassword(String username) {
		String password = "";

		do {
			password = RandomStringUtils.randomAlphanumeric(10);
			String specialChar = RandomStringUtils.random(1, "~!@#$%^&*()-+=");
			int randPos = RandomUtils.nextInt(password.length() + 1);
			password = new StringBuilder(password).insert(randPos, specialChar).toString();
		} while (!PasswordUtils.isStrongPassword(password, username));
		
		return password;
	}
	
	public static String randomMediumPassword(String username) {
		String password = "";

		do {
			password = RandomStringUtils.randomAlphanumeric(6);
		} while (!PasswordUtils.isMediumPassword(password, username));
		
		return password;
	}
}
