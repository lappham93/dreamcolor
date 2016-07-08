package com.mit.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.mit.common.conts.BigDecimalConstants;

public class BigDecimalUtils {
	public static final BigDecimalUtils Instance = new BigDecimalUtils();
	
	private BigDecimalUtils() {}
	
	public long convertToLong(BigDecimal val) {
		return val.multiply(BigDecimalConstants.TEN_THOUSAND).setScale(0, RoundingMode.HALF_UP).longValue();
	}
	
	public BigDecimal revertToBigDecimal(long val) {
		return new BigDecimal(val).divide(BigDecimalConstants.TEN_THOUSAND);
	}
	
	public String formatMoney(BigDecimal value) {
		value = value.setScale(2, BigDecimal.ROUND_UP);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		df.setGroupingUsed(true);

		String result = df.format(value);
		return result;
	}
	
	public static void main(String[] args) {
//		BigDecimal val = new BigDecimal(5337203657755.8076);
//		System.out.println(new BigDecimal(val.doubleValue()).setScale(4, RoundingMode.HALF_UP));
//		long longVal = BigDecimalUtils.Instance.convertToLong(val);
//		System.out.println(BigDecimalUtils.Instance.revertToBigDecimal(longVal));
		
		System.out.println(BigDecimalUtils.Instance.formatMoney(new BigDecimal(5)));
	}
}
