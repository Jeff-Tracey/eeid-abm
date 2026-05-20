/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.geometricprimitives;

import java.io.*;

/**
 *
 * @author jeff
 */
public class CircleIn2D implements Serializable {
    private PointIn2D center;
    private double radius;
    
    /**
     * 
     */
    public CircleIn2D() {
        center = new PointIn2D(0.0, 0.0);
        radius = 0.0;
    }
    
    /**
     * 
     * @param p
     * @param r
     */
    public CircleIn2D(PointIn2D p, double r) {
        center = p;
        radius = Math.abs(r);
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param r
     */
    public CircleIn2D(double x, double y, double r) {
        center = new PointIn2D(x, y);
        radius = Math.abs(r);
    }
    
    /**
     * 
     * @param x
     * @param y
     */
    public void setCenter(double x, double y) {
        center = new PointIn2D(x, y);
    }
    
    /**
     * 
     * @param p
     */
    public void setCenter(PointIn2D p) {
        center = p;
    }
    
    /**
     * 
     * @param r
     */
    public void setRadius(double r) {
        radius = Math.abs(r);
    }
    
    /**
     * 
     * @return the x-coordinate of the center of the circle.
     */
    public double getCenterX() {
        return center.getX();
    }
    
    /**
     * 
     * @return the y-coordinate of the center of the circle.
     */
    public double getCenterY() {
        return center.getY();
    }
    
    /**
     * 
     * @return the minimum x-coordinate of the circle.
     */
    public double getXmin() {
        return (center.getX() - radius);
    }
    
    /**
     * 
     * @return the maximum x-coordinate of the circle.
     */
    public double getXmax() {
        return (center.getX() + radius);
    }
    
    /**
     * 
     * @return the minimum y-coordinate of the circle.
     */
    public double getYmin() {
        return (center.getY() - radius);
    }
    
    /**
     * 
     * @return the maximum y-coordinate of the circle.
     */
    public double getYmax() {
        return (center.getY() + radius);
    }
    
    /**
     * 
     * @return the center of the cirlce.
     */
    public PointIn2D getCenter() {
        return center;
    }
    
    /**
     * 
     * @return the radius of the circle.
     */
    public double getRadius() {
        return radius;
    }
    
    /**
     * Returns true if p is in or on the edge of the circle.
     * @param p
     * @return
     */
    public boolean containsPoint(PointIn2D p) {
        if (p != null) {
            return center.withinDistanceOf(p, radius);
        } else {
            return false;
        }
    }

    @Override public String toString() {
        String res = "Circle (" + center.toString() + ", radius = " + radius + ")";
        return res;
    }
}
