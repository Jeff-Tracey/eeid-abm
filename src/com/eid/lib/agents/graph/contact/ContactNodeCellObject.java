/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents.graph.contact;

import com.eid.lib.geometricprimitives.*;
import com.eid.lib.gridindex.*;

/**
 *
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
class ContactNodeCellObject implements SpatialAccessMethods {
    private CircleIn2D location = null;
    private ContactGraphNodeData dataRef = null;
    
    ContactNodeCellObject(PointIn2D c, double rad, ContactGraphNodeData pd) {
        location = new CircleIn2D(c, rad);
        dataRef = pd;
    }
    
    public BoxIn2D getBoundingBox() {
        return new BoxIn2D(location.getCenterX(), location.getCenterY(), location.getRadius());
    }
    
    public boolean contains(PointIn2D p) {
        return location.containsPoint(p);
    }
    
    public ContactGraphNodeData getNodeData() {
        return dataRef;
    }
    
    public PointIn2D getLocation() {
        return location.getCenter();
    }
}
