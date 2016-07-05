/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.utils;

/**
 *
 * @author nghiatc
 * @since Jan 13, 2016
 */
public enum WUnit {
    OZ(0), POUND(1), GRAM(2), KILOGRAM(3);
    
    private final int value;
    
    private WUnit(int value){
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
    
    public String getName(){
        return findByValue(value).name();
    }
    
    public static WUnit findByValue(int value){
        switch (value) {
            case 0:
                return OZ;
            case 1:
                return POUND;
            case 2:
                return GRAM;
            case 3:
                return KILOGRAM;
            
            default:
                return null;
        }
    }
}
