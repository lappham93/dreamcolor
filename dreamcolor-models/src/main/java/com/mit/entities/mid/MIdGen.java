/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.entities.mid;

/**
 *
 * @author nghiatc
 * @since Aug 11, 2015
 */
public class MIdGen {

    private String name;
    private int seq;

    private MIdGen(){}

    public MIdGen(String name) {
        this.name = name;
    }

    public MIdGen(String name, int seq) {
        this.name = name;
        this.seq = seq;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
    
    
    
}
