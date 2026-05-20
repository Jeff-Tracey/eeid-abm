/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.gridindex;

import java.util.*;
import com.eid.lib.geometricprimitives.*;

/**
 *
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
class GridIndexCell {
    private ArrayList<SpatialAccessMethods> objList = null;
    private BoxIn2D cellMBB = null;
    
    GridIndexCell(double xMin, double yMin, double xMax, double yMax) {
        cellMBB = new BoxIn2D(xMin, yMin, xMax, yMax);
        objList = new ArrayList<SpatialAccessMethods>();
    }
    
    public int getNumberOfObjectsInCell() {
        return objList.size();
    }
    
    
    
    boolean cellObjectContainsPoint(int i, PointIn2D p) {
        if ((i>= 0) && (i < objList.size())) {
            return objList.get(i).contains(p);
        } else {
            return false;
        }
    }
    
    SpatialAccessMethods getCellObject(int i) {
        if ((i>= 0) && (i < objList.size())) {
            return objList.get(i);
        } else {
            return null;
        }
    }
    
    void removeAllObjects() {
        objList.clear();
    }
    
    void addCellObject(SpatialAccessMethods o) {
        if (!objList.contains(o)) {
            objList.add(o);
        }
    }
}
