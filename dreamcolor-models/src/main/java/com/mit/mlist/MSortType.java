/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.mlist;

/**
 *
 * @author nghiatc
 * @since Aug 27, 2015
 */
public enum MSortType {
    ASC(1),     // Tăng dần.
    DESC(-1);   // Giảm dẩn.

    private final int value;

    private MSortType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getName(){
        return findByValue(value).name();
    }

    public static MSortType findByValue(int value) {
        switch (value) {
            case 1:
                return ASC;
            case -1:
                return DESC;
            default:
                return null;
        }
    }
}
