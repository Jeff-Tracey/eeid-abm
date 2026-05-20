/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.grid;

/**
 *
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
class RasterCellIndex implements Comparable<RasterCellIndex> {
    private int row;
    private int col;
    
    /**
     * 
     */
    public RasterCellIndex() {
        row = -1;
        col = -1;
    }
    
    /**
     * 
     * @param r
     * @param c
     */
    RasterCellIndex(int r, int c) {
        if (r >= 0) {
            row = r;
        } else {
            row = -1;
        }
        if (c >= 0) {
            col = c;
        } else {
            col = -1;
        }
    }
    
    /**
     * 
     * @return
     */
    public int getRow() {
        return row;
    }
    
    /**
     * 
     * @return
     */
    public int getCol() {
        return col;
    }
    
    /**
     * 
     * @param r
     */
    public void setRow(int r) {
        if (r >= 0) {
            row = r;
        } else {
            row = -1;
        }
    }
    
    /**
     * 
     * @param c
     */
    public void setCol(int c) {
        if (c >= 0) {
            col = c;
        } else {
            col = -1;
        }
    }
    
    
    
    
    
    /**
     * 
     */
    void incrementRow() {
        row++;
    }
    
    
    
    /**
     * 
     */
    void incrementCol() {
        col++;
    }
    
    /**
     * Compares objects by row index and then by column index.
     * @param i
     * @return
     */
    public int compareTo(RasterCellIndex i) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        
        if (this == i) {
            return EQUAL;
        } else {
            if (this.row < i.row) {
                return BEFORE;
            } else if (this.row > i.row) {
                return AFTER;
            } else {
                if (this.col < i.col) {
                    return BEFORE;
                } else if (this.col > i.col) {
                    return AFTER;
                } else {
                    return EQUAL;
                }
            }
        }
    }
    
    @Override public String toString() {
        String res = "RasterCellIndex (row = " + row + ", col = " + col + ")\n";
        return res;
    }
}
