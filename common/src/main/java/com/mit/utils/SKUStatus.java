/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.utils;

/**
 *
 * @author nghiatc
 * @since Jan 14, 2016
 */
public enum SKUStatus {
    DELETE(0), ACTIVE(1);
    
    private final int value;
    
    private SKUStatus(int value){
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
}
