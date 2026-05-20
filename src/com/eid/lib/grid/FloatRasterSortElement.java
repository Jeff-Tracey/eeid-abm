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
class FloatRasterSortElement implements Comparable<FloatRasterSortElement> {
    private RasterCellIndex ci = null;
    private float value = Float.NaN;
    
    /**
     * 
     * @param ri
     * @param v
     */
    FloatRasterSortElement(RasterCellIndex ri, float v) {
        ci = ri;
        value = v;
    }
    
    /**
     * 
     * @return
     */
    public float getValue() {
        return value;
    }
    
    /**
     * 
     * @return
     */
    public RasterCellIndex getRasterCellIndex() {
        return ci;
    }

	@Override
	public int compareTo(FloatRasterSortElement arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
