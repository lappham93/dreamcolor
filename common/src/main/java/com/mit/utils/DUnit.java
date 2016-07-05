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
public enum DUnit {
    INCH(0), FEET(1), MILIMETER(2), CENTIMETER(3), METER(4);
    
    private final int value;
    
    private DUnit(int value){
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
    
    public String getName(){
        return findByValue(value).name();
    }
    
    public static DUnit findByValue(int value){
        switch (value) {
            case 0:
                return INCH;
            case 1:
                return FEET;
            case 2:
                return MILIMETER;
            case 3:
                return CENTIMETER;
            case 4:
                return METER;
            default:
                return null;
        }
    }
}
