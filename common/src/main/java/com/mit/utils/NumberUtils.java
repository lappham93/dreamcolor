package com.mit.utils;

public class NumberUtils {	
	private NumberUtils() {}
	
	public static long toPrimitive(Long val) {
		return val != null ? val : 0L;
	}
	
	public static int toPrimitive(Integer val) {
		return val != null ? val : 0;
	}
}
