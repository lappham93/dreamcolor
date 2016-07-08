package com.mit.utils;

import org.apache.commons.lang.StringUtils;

public class Base64UrlEncode {
	private final String _specialChars = "+/=";
	private final String _replaceChars = "-_,";
	
	private static Base64UrlEncode _instance;
	
	public static Base64UrlEncode getInstance() {
		if (_instance == null) {
			_instance = new Base64UrlEncode();
		}
		
		return _instance;
	}
	
	public String encode(String str) {
		return StringUtils.replaceChars(str, _specialChars, _replaceChars);
	}
	
	public String decode(String str) {
		return StringUtils.replaceChars(str, _replaceChars, _specialChars);
	}
}
