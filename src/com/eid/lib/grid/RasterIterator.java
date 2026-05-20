/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.grid;

/**
 *
 * @author  Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public interface RasterIterator {
    public boolean hasNext();
    public RasterCellIndex next();
    public void start();
}
