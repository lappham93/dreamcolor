/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.entities.mid;

/**
 *
 * @author nghiatc
 * @since Aug 20, 2015
 */
public class MIdGenLong {
    private String name;
    private long seq;

    private MIdGenLong(){}

    public MIdGenLong(String name) {
        this.name = name;
    }

    public MIdGenLong(String name, long seq) {
        this.name = name;
        this.seq = seq;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
