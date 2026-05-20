package com.eid.lib.geometricprimitives;

import java.io.*;

/**
 * A class for a line segment (defined by a starting and end point) in 2D space
 * intended to support spatial data structures and computational geometry.
 *
 * Created: 22 January, 2008
 *
 * @author Jeff A. Tracey
 * Copyright 2008 Jeff A. Tracey, SigmaLogistic Consulting.
 * All rights reserved.
 */
public class EdgeIn2D implements Serializable {
    /**  */
    private PointIn2D fmVertex;
    /**  */
    private PointIn2D toVertex;
    
    /**
     *
     */
    public EdgeIn2D() {
        fmVertex = new PointIn2D();
        toVertex = new PointIn2D();
    }
    
    /**
     * 
     * @param p1
     * @param p2
     */
    EdgeIn2D(PointIn2D p1, PointIn2D p2) {
        fmVertex = new PointIn2D(p1);
        toVertex = new PointIn2D(p2);
    }
    
    /**
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public EdgeIn2D(double x1, double y1, double x2, double y2) {
        fmVertex = new PointIn2D(x1, y1);
        toVertex = new PointIn2D(x2, y2);
    }
    
    /**
     * 
     * @param l
     */
    EdgeIn2D(EdgeIn2D l) {
        fmVertex = new PointIn2D(l.fmVertex);
        toVertex = new PointIn2D(l.toVertex);
    }
    
    // possibly construct from a vector object
    
    /**
     * 
     * @return the from-vertex of the line segment.
     */
    public PointIn2D getFromVertex() {
        return fmVertex;
    }
    
    /**
     * 
     * @return the to-vertex of the line segment.
     */
    public PointIn2D getToVertex() {
        return toVertex;
    }
    
    /**
     * 
     * @param p
     */
    public void setFromVertex(PointIn2D p) {
        fmVertex.setCoordinates(p);
    }
    
    
    
    /**
     * 
     * @param p
     */
    public void setToVertex(PointIn2D p) {
        toVertex.setCoordinates(p);
    }
    
    
    
    /**
     * 
     * @param p1
     * @param p2
     */
    void setVertices(PointIn2D p1, PointIn2D p2) {
        fmVertex.setCoordinates(p1);
        toVertex.setCoordinates(p2);
    }
    
    
    
    /**
     * Test if line is vertical.
     * @return
     */
    public boolean isVertical() {
        if (fmVertex.getX() == toVertex.getX()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Test if line is horizontal.
     * @return
     */
    public boolean isHorizontal() {
        if (fmVertex.getY() == toVertex.getY()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Returns true if the coordinates are finite and non <code>NaN</code> and
     * false otherwise.
     * @return
     */
    boolean hasRealCoordinates() {
        if ((fmVertex.hasRealCoordinates()) && (toVertex.hasRealCoordinates())) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Calculates the length of the edge.  If all coordinates are not defined,
     * then <code>NaN</code> is returned.
     * @return
     */
    public double getLength() {
        double res = Double.NaN;
        if (this.hasRealCoordinates()) {
            res = Math.hypot((toVertex.getX() - fmVertex.getX()), (toVertex.getY() - fmVertex.getY()));
        }
        return res;
    }
    
    /**
     * Calculates the angle of the edge in radians.  If all coordinates are not 
     * defined, then <code>NaN</code> is returned.
     * @return
     */
    public double getAngleInRadians() {
        double res = Double.NaN;
        if (this.hasRealCoordinates()) {
            res = Math.atan2((toVertex.getY() - fmVertex.getY()), (toVertex.getX() - fmVertex.getX()));
        }
        return res;
    }
    
    /**
     * Uses the determinant formula for the area of a triangle.  If the point
     * <code>p</code> is to the left of the edge, the result will be 
     * <code>&gt 0</code>.  If the point <code>p</code> is to the right of the 
     * edge, the result will be <code>&lt 0</code>.  If the point is on the edge,
     * the result will be 0.
     * @param p
     * @return
     */
    private double triangleAreaFunction(PointIn2D p) {
        double res = Double.NaN;
        if (p != null) {
            res = p.getX()*(fmVertex.getY() - toVertex.getY()) + 
                    p.getY()*(toVertex.getX() - fmVertex.getX()) + 
                    fmVertex.getX()*toVertex.getY() - 
                    toVertex.getX()*fmVertex.getY();
            res *= 0.5;
        }
        return res;
    }
    
    /**
     * Test for intersection with edge <code>e</code>.  If <code>e</code> is
     * <code>null</code>, false is returned.
     * 
     * I ASSUME THAT IF MINIMUM BOUNDING BOXES FOR THE EDGES INTERSECT AND
     * LINES THROUGH THE EDGES INTERSECT, THEN THE EDGES THEMSELVES INTERSECT.
     * CHECK THIS ASSUMTION.
     * 
     * THERE ARE ALSO SOME IMPLEMENTATION CHANGES I WANT TO MAKE FOR THIS.
     * 
     * @param e
     * @return
     */
    boolean intersects(EdgeIn2D e) {
        boolean res = false;
        if (e != null) {
            // I DON'T THINK I LIKE THIS BUSINESS OF CREATING NEW BOX OBJECTS
            BoxIn2D thisBox = new BoxIn2D(this);
            BoxIn2D eBox = new BoxIn2D(e);
            // THERE ARE SOME OTHER BOOLEAN CHECKS WE CAN DO TO AVOID CALLING
            // triangleAreaFunction() UNLESS ABSOLUTELY NECESSARY
            if (thisBox.intersectsBox(eBox)) {
                if ((this.triangleAreaFunction(e.fmVertex) >= 0) && (this.triangleAreaFunction(e.toVertex) <= 0)) {
                    res = true;
                } else if ((this.triangleAreaFunction(e.fmVertex) <= 0) && (this.triangleAreaFunction(e.toVertex) >= 0)) {
                    res = true;
                }
            }
        }
        return res;
    }
    
    /**
     * Test for intersection with box <code>b</code>.  If <code>b</code> is
     * <code>null</code>, false is returned.
     * @param b
     * @return
     */
    boolean intersects(BoxIn2D b) {
        boolean res = false;
        if (b != null) {
            if ((b.intersectsPoint(fmVertex)) || (b.intersectsPoint(toVertex))) {
                res = true;
            } else {
                EdgeIn2D[] boxEdges = b.getEdges();
                for (int i = 0; i < boxEdges.length; i++) {
                    if (intersects(boxEdges[i])) {
                        res = true;
                    }
                }
            }
        }
        return res;
    }
    
    /**
     * If the lines intersect and do not have the same slope a point is returned.
     * Otherwise null is returned.
     * @param e
     * @return
     */
    PointIn2D getIntersection(EdgeIn2D e) {
        PointIn2D res = null;
        if (this.intersects(e)) {
            double resX = Double.NaN, resY = Double.NaN;
            double dx1, dy1, dx2, dy2, m1, m2;
            // edge 1 is this edge, edge 2 is e
            boolean edge1vertical = this.isVertical();
            boolean edge2vertical = e.isVertical();
            if ((!edge1vertical) && (!edge2vertical)) {
                boolean edge1horizontal = this.isHorizontal();
                boolean edge2horizontal = e.isHorizontal();
                if ((!edge1horizontal) && (!edge2horizontal)) {
                    dx1 = this.toVertex.getX() - this.fmVertex.getX();
                    dy1 = this.toVertex.getY() - this.fmVertex.getY();
                    dx2 = e.toVertex.getX() - e.fmVertex.getX();
                    dy2 = e.toVertex.getY() - e.fmVertex.getY();
                    m1 = dy1/ dx1;
                    m2 = dy2/dx2;
                    resX = (e.fmVertex.getY() - this.fmVertex.getY() - m2 * e.fmVertex.getX() + m1 * this.fmVertex.getX()) / (m1 - m2);
                    resY = m1 * (resX - this.fmVertex.getX()) + this.fmVertex.getY();
                } else if ((edge1horizontal) && (!edge2horizontal)) {
                    resY = this.fmVertex.getY();
                    dx2 = e.toVertex.getX() - e.fmVertex.getX();
                    dy2 = e.toVertex.getY() - e.fmVertex.getY();
                    m2 = dx2/dy2;
                    resX = (resY - e.fmVertex.getY()) * m2 + e.fmVertex.getX();
                } else if ((!edge1horizontal) && (edge2horizontal)) {
                    resY = e.fmVertex.getY();
                    dx1 = this.toVertex.getX() - this.fmVertex.getX();
                    dy1 = this.toVertex.getY() - this.fmVertex.getY();
                    m1 = dx1/ dy1;
                    resX = (resY - this.fmVertex.getY()) * m1 + this.fmVertex.getX();
                } // if both horizontal, no intersection (what if common vertex??)
            } else if ((edge1vertical) && (!edge2vertical)) {
                resX = this.fmVertex.getX();
                dx2 = e.toVertex.getX() - e.fmVertex.getX();
                dy2 = e.toVertex.getY() - e.fmVertex.getY();
                m2 = dy2 / dx2;
                resY = m2 * (resX - e.fmVertex.getX()) + e.fmVertex.getY();
            } else if ((!edge1vertical) && (edge2vertical)) {
                resX = e.fmVertex.getX();
                dx1 = this.toVertex.getX() - this.fmVertex.getX();
                dy1 = this.toVertex.getY() - this.fmVertex.getY();
                m1 = dy1 / dx1;
                resY = m1 * (resX - this.fmVertex.getX()) + this.fmVertex.getY();
            } // if both vertical, no intersection (what if common vertex??)
            res = new PointIn2D(resX, resY);
        }
        return res;
    }
    
    /**
     * 
     * @param qx
     * @param qy
     * @return the point on the line segment nearest to (qx, qy).
     */
    PointIn2D getNeartestPointOnEdgeTo(double qx, double qy) {
        PointIn2D n = new PointIn2D();    // return variable
        double alpha_numer, alpha_denom, alpha_val;
        double dx1, dy1, dx2, dy2;

        // translate so from node of edge is the origin
        dx1 = qx - fmVertex.getX();
        dy1 = qy - fmVertex.getY();
        dx2 = toVertex.getX() - fmVertex.getX();
        dy2 = toVertex.getY() - fmVertex.getY();

        if (dx2 == 0) {
            if (dy2 == 0) { // edge is zero length-nearest point is either edge vertice
                n.setCoordinates(fmVertex);
            } else {
                // edge is vertical
                n.setXcoordinate(fmVertex.getX());
                if (dy2 > 0) {
                    if (dy1 < 0) {
                        n.setYcoordinate(fmVertex.getY());
                    } else if (qy > toVertex.getY()) {
                        n.setYcoordinate(toVertex.getY());
                    } else {
                        n.setYcoordinate(qy);
                    }
                } else {
                    if (qy < toVertex.getY()) {
                        n.setYcoordinate(toVertex.getY());
                    } else if (dy1 > 0) {
                        n.setYcoordinate(fmVertex.getY());
                    } else {
                        n.setYcoordinate(qy);
                    }
                }
            }
        } else { // edge is not vertical or zero length
            alpha_numer = dx1 * dx2 + dy1 * dy2; // might be negative
            if (alpha_numer < 0) {
                n.setCoordinates(fmVertex);
            } else {
                alpha_denom = dx2 * dx2 + dy2 * dy2; // must be positive
                alpha_val = alpha_numer / alpha_denom;
                if (alpha_val >= 1) {
                    n.setCoordinates(toVertex);
                } else {
                    double newx = alpha_val * dx2 + fmVertex.getX();
                    double newy = alpha_val * dy2 + fmVertex.getY();
                    n.setCoordinates(newx, newy);
                }
            }
        }
        return n;
    }
    
    // EQUALS (compare points...)
    
    @Override public String toString() {
        String res = "Edge:\n\tFrom " + fmVertex.toString() + "\n\tTo " + toVertex.toString();
        return res;
    }
}
