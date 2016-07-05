package com.mit.utils;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

public class BitUtils {
	public static List<Integer> intToBit(int n) {
		List<Integer> bitArr = new LinkedList<Integer>();
		
		int i = 1;
		while (i <= n) {
			if ((n & i) != 0) {
				bitArr.add(i);
			}
			
			i <<= 1;
		}
		
		return bitArr;
	}
	
	public static int bitToInt(List<Integer> bitArr) {
		int n = 0;
		
		for (int bit: bitArr) {
			n |= bit;
		}
		
		return n;
	}
	
	public static int countSetBits(int i) {
	     i = i - ((i >> 1) & 0x55555555);
	     i = (i & 0x33333333) + ((i >> 2) & 0x33333333);
	     return (((i + (i >> 4)) & 0x0F0F0F0F) * 0x01010101) >> 24;
	}
}
