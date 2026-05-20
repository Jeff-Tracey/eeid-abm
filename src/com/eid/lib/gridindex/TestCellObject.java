/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.gridindex;

import com.eid.lib.geometricprimitives.*;

/**
 *
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class TestCellObject implements SpatialAccessMethods {
    private CircleIn2D location = null;
    private int dataNum = -1;
    
    public TestCellObject(PointIn2D c, double rad, int dataID) {
        location = new CircleIn2D(c, rad);
        dataNum = dataID;
    }
    
    public BoxIn2D getBoundingBox() {
        return new BoxIn2D(location.getCenterX(), location.getCenterY(), location.getRadius());
    }
    
    public boolean contains(PointIn2D p) {
        return location.containsPoint(p);
    }
    
    public int getDataID() {
        return dataNum;
    }
    
    public PointIn2D getLocation() {
        return location.getCenter();
    }
}
