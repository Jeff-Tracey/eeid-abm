package com.eid.lib.geometricprimitives;

import java.io.*;

/**
 * A class for a vector (point, angle, length) in 2D space intended to support
 * spatial data structures and computational geometry.
 *
 * Created: 22 January, 2008
 *
 * @author Jeff A. Tracey
 * Copyright 2008 Jeff A. Tracey, SigmaLogistic Consulting.
 * All rights reserved.
 */
public class VectorIn2D implements Serializable {
    private double angle;
    private double length;
    
    /**
     * 
     */
    public VectorIn2D() {
        angle = Double.NaN;
        length = 0.0;
    }
    
    /**
     * 
     * @param l
     * @param a
     */
    VectorIn2D(double l, double a) {
        if (l == 0.0) {
            angle = Double.NaN;
            length = 0.0;
        } else if (l < 0.0) {
            length = Math.abs(l);
            angle = a + Math.PI;
            // really must do the modulo thing with a
            if (angle < -Math.PI) angle += Math.PI;
            if (angle > Math.PI) angle -= Math.PI;
        } else {
            length = l;
            angle = a;
        }
    }
    
    /**
     * 
     * @return The angle of the vector.
     */
    public double getAngle() {
        return angle;
    }
    
    /**
     * 
     * @return The length of the vector.
     */
    public double getLength() {
        return length;
    }
}
