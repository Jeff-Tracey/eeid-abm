/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.sorting;

/**
 *
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class FloatComparable implements Comparable<FloatComparable> {
    private static final int NULL_INDEX = -1;
    private float value;
    private int index;
    
    public FloatComparable() {
        value = 0.0f;
        index = NULL_INDEX;
    }
    
    public FloatComparable(float f) {
        value = f;
        index = NULL_INDEX;
    }
    
    public FloatComparable(float f, int i) {
        value = f;
        index = i;
    }
    
    public void setValue(float f) {
        value = f;
    }
    
    public void setIndex(int i) {
        index = i;
    }
    
    public float getValue() {
        return value;
    }
    
    public int getIndex() {
        return index;
    }
    
    /**
     * Float.compareTo() returns in order -inf, neg, -0.0, 0.0, pos, +inf, NaN.
     * @param obj
     * @return
     */
    public int compareTo(FloatComparable obj) {
        /*
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        */
        return (new Float(this.value)).compareTo(new Float(obj.value));
        /*
        if ((!Float.isNaN(this.value)) && (!Float.isNaN(obj.value))) {
            if (this.value < obj.value) {
                return BEFORE;
            } else if (this.value > obj.value) {
                return AFTER;
            } else {
                return EQUAL;
            }
        } else {
        //
        }
        */
    }
}
