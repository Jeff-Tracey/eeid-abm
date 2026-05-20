/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.gridindex;

import com.eid.lib.geometricprimitives.*;

/**
 *
 * @author  Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public interface SpatialAccessMethods {
    public BoxIn2D getBoundingBox();
    public boolean contains(PointIn2D p);
    // possibly other tests...
}
