package com.eid.lib.geometricprimitives;

import java.io.*;

/**
 * A class for a point in 2D space intended to support spatial data structures
 * and computational geometry.
 *
 * Created: 22 January, 2008
 *
 * @author Jeff A. Tracey
 * Copyright 2008 Jeff A. Tracey, SigmaLogistic Consulting.
 * All rights reserved.
 */
public class PointIn2D implements Serializable {
    /** The x-coordinate. */
    private double x;
    /** The y-coordinate. */
    private double y;
    
    /**
     * Default constructor.
     */
    public PointIn2D() {
        x = Double.NaN;
        y = Double.NaN;
    }
    
    /**
     * Parameterized constructor.
     * @param xcoord The x-coordinate of this point.
     * @param ycoord The y-coordinate of this point.
     */
    public PointIn2D(double xcoord, double ycoord) {
        x = xcoord;
        y = ycoord;
    }
    
    /**
     * Copy constructor.
     * @param p The point that will be used to initialize this point.
     */
    public PointIn2D(PointIn2D p) {
        if (p != null) {
            x = p.x;
            y = p.y;
        } else {
            x = Double.NaN;
            y = Double.NaN;
        }
    }
    
    /**
     * Set the coordinates of the point.
     * @param xcoord The new x-coordinate of this point.
     * @param ycoord The new y-coordinate of this point.
     */
    public void setCoordinates(double xcoord, double ycoord)  {
        x = xcoord;
        y = ycoord;
    }
    
    /**
     * 
     * 
     * @param xcoord
     */
    public void setXcoordinate(double xcoord) {
        x = xcoord;
    }
    
    /**
     * 
     * @param ycoord
     */
    public void setYcoordinate(double ycoord) {
        y = ycoord;
    }
    
    /**
     * Set the coordinates of the point.
     * @param p The PointIn2D object with new coordinates of this point.
     */
    public void setCoordinates(PointIn2D p)  {
        x = p.x;
        y = p.y;
    }
    
    /**
     * Returns true if the coordinates are finite and non <code>NaN</code> and
     * false otherwise.
     * @return
     */
    public boolean hasRealCoordinates() {
        if ((Double.isInfinite(x)) || (Double.isInfinite(y)) || (Double.isNaN(x)) || (Double.isNaN(y))) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Get the x-coordinate for this point.
     * @return The x-coordinate of this point.
     */
    public double getX() {
        return x;
    }
    
    /**
     * Get the y-coordinate for this point.
     * @return The y-coordinate of this point.
     */
    public double getY() {
        return y;
    }
    
    /**
     * Test to see if this equals another point.
     * @param p Another point.
     * @return True is the coordinates of the points are equal, false otherwise.
     */
    public boolean equals(PointIn2D p) {
        if ((x == p.x) && (y == p.y)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 
     * @param p
     * @return
     */
    public double distanceTo(PointIn2D p) {
        if (p != null) {
            double dx = x - p.x;
            double dy = y - p.y;
            return Math.hypot(dx, dy);
        } else {
            return Double.NaN;
        }
    }
    
    /**
     * 
     * @param p
     * @param dist
     * @return
     */
    public boolean withinDistanceOf(PointIn2D p, double dist) {
        if (p != null) {
            double dx = Math.abs(x - p.x);
            double dy = Math.abs(y - p.y);
            double dist2 = Math.abs(dist);
            if ((dx > dist2) || (dy > dist2)) {
                return false;
            } else if ((dx < 0.707 * dist) && (dy < 0.707 * dist)) { // 0.707 < 1/sqrt(2)
                return true;
            } else if (Math.hypot(dx, dy) <= dist) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    /**
     * 
     * @param p
     * @return
     */
    public double angleTo(PointIn2D p) {
        if (p != null) {
            double dx = p.x - x;
            double dy = p.y - y;
            if ((dx != 0.0) && (dy != 0.0)) {
                return Math.atan2(dy, dx);
            } else {
                return Double.NaN;
            }
        } else {
            return Double.NaN;
        }
    }
    
    /**
     * 
     * @param p
     * @return
     */
    public VectorIn2D vectorTo(PointIn2D p) {
        return new VectorIn2D(distanceTo(p), angleTo(p));
    }
    
    // MANY MORE METHODS TO ADD (SEE C++ CODE)...
    
    @Override public String toString() {
        String res = "PointIn2D: (" + x + ", " + y + ")";
        return res;
    }
    
    /**
     * 
     */
    public void display() {
        System.out.println("PointIn2D: (" + x + ", " + y + ")");
    }
}
