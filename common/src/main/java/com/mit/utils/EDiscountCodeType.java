/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.utils;

/**
 *
 * @author nghiatc
 * @since Jan 22, 2016
 */
public enum EDiscountCodeType {
    RETAILER(1), SALON_BUYER(2), DISTRIBUTOR(3), MASTER(4), COUNTRY_MASTER(5), GLOBAL_MASTER(6);
    
    private final int value;
    
    private EDiscountCodeType(int value){
        this.value = value;
    }
    
    public static EDiscountCodeType getDiscountCodeType(int value) {
    	for (EDiscountCodeType e : EDiscountCodeType.values())
    		if (e.value == value)
    			return e;
    	return null;
    }
    public int getValue(){
        return this.value;
    }
}
