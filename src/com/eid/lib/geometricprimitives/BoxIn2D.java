package com.eid.lib.geometricprimitives;

import java.io.*;

/**
 * A class for a box (rectangle with sides parallel to the axes of the coordinate
 * system) in 2D space intended to support spatial data structures and 
 * computational geometry.
 *
 * Created: 22 January, 2008
 *
 * @author Jeff A. Tracey
 * Copyright 2008 Jeff A. Tracey, SigmaLogistic Consulting.
 * All rights reserved.
 */
public class BoxIn2D implements Serializable {
    private double xLL;
    private double yLL;
    private double xUR;
    private double yUR;
    
    /**
     * 
     */
    public BoxIn2D() {
        xLL = Double.NaN;
        yLL = Double.NaN;
        xUR = Double.NaN;
        yUR = Double.NaN;
    }
    
    
    
    /**
     * Create a minimum bounding box from the edge <code>e</code>.
     * @param e
     */
    BoxIn2D(EdgeIn2D e) {
        if (e == null) {
            xLL = Double.NaN;
            yLL = Double.NaN;
            xUR = Double.NaN;
            yUR = Double.NaN;
        } else {
            // set x-coordinates
            if (e.getFromVertex().getX() <= e.getToVertex().getX()) {
                xLL = e.getFromVertex().getX();
                xUR = e.getToVertex().getX();
            } else {
                xLL = e.getToVertex().getX();
                xUR = e.getFromVertex().getX();
            }
            // set y-coorindates
            if (e.getFromVertex().getY() <= e.getToVertex().getY()){
                yLL = e.getFromVertex().getY();
                yUR = e.getToVertex().getY();
            } else {
                yLL = e.getToVertex().getY();
                yUR = e.getFromVertex().getY();
            }
        }
    }
    
    /**
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public BoxIn2D(double x1, double y1, double x2, double y2) {
        if (x1 <= x2) {
            xLL = x1;
            xUR = x2;
        } else {
            xLL = x2;
            xUR = x1;
        }
        if (y1 <= y2) {
            yLL = y1;
            yUR = y2;
        } else {
            yLL = y2;
            yUR = y1;
        }
    }
    
    /**
     * This constructor makes a square with center at (x,y) and sides of length 2*s.
     * @param x
     * @param y
     * @param s
     */
    public BoxIn2D(double x, double y, double s) {
        xLL = x - Math.abs(s);
        xUR = x + Math.abs(s);
        yLL = y - Math.abs(s);
        yUR = y + Math.abs(s);
    }
    
    /**
     * Creates an array containing the four edges of the box in order of bottom,
     * right, top, and left sides.  The edge form a closed polygon.
     * @return
     */
    public EdgeIn2D[] getEdges() {
        EdgeIn2D[] res = new EdgeIn2D[4];
        res[0] = new EdgeIn2D(xLL, yLL, xUR, yLL); // bottom
        res[1] = new EdgeIn2D(xUR, yLL, xUR, yUR); // right
        res[2] = new EdgeIn2D(xUR, yUR, xLL, yUR); // top
        res[3] = new EdgeIn2D(xLL, yUR, xLL, yLL); // left
        return res;
    }
    
    /**
     * Tests to see if all coordinates are finite and non-NaN.
     * @return true if all coordinates are finite and non-NaN, false otherwise.
     */
    public boolean hasRealCoordinates() {
        if ((Double.isInfinite(xLL)) || (Double.isNaN(xLL)) || 
                (Double.isInfinite(yLL)) || (Double.isNaN(yLL)) || 
                (Double.isInfinite(xUR)) || (Double.isNaN(xUR)) || 
                (Double.isInfinite(yUR)) || (Double.isNaN(yUR))) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * 
     * @return minimum x-coordinate.
     */
    public double getXmin() {
        return xLL;
    }
    
    /**
     * 
     * @return maximum x-coordinate.
     */
    public double getXmax() {
        return xUR;
    }
    
    /**
     * 
     * @return minimum y-coordinate.
     */
    public double getYmin() {
        return yLL;
    }
    
    /**
     * 
     * @return maximum y-coordinate.
     */
    public double getYmax() {
        return yUR;
    }
    
    /**
     * 
     * @return area of box.
     */
    public double getArea() {
        return (xUR - xLL)*(yUR - yLL);
    }
    
    /**
     * 
     * @return height of box.
     */
    public double getHeight() {
        return (yUR - yLL);
    }
    
    /**
     * 
     * @return width of box.
     */
    public double getWidth() {
        return (xUR - xLL);
    }
    
    /**
     * 
     * @return x-coordinate of center of box.
     */
    public double getCenterXcoord() {
        return 0.5*(xLL + xUR);
    }
    
    /**
     * 
     * @return y-coordinate of center of box.
     */
    public double getCenterYcoord() {
        return 0.5*(yLL + yUR);
    }
    
    /**
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void reset(double x1, double y1, double x2, double y2) {
        if (x1 <= x2) {
            xLL = x1;
            xUR = x2;
        } else {
            xLL = x2;
            xUR = x1;
        }
        if (y1 <= y2) {
            yLL = y1;
            yUR = y2;
        } else {
            yLL = y2;
            yUR = y1;
        }
    }
    
    /**
     * 
     * @param b
     */
    public void reset(BoxIn2D b) {
        xLL = b.xLL;
        xUR = b.xUR;
        yLL = b.yLL;
        yUR = b.yUR;
    }
    
    
    
    
    
    /**
     * 
     * @param p
     */
    public void expandToIncludePoint(PointIn2D p) {
        if (p.getX() < xLL) xLL = p.getX();
        if (p.getX() > xUR) xUR = p.getX();
        if (p.getY() < yLL) yLL = p.getY();
        if (p.getY() > yUR) yUR = p.getY();
    }
    
    /**
     * 
     * @param p
     */
    public void setToPoint(PointIn2D p) {
        xLL = p.getX();
        xUR = p.getX();
        yLL = p.getY();
        yUR = p.getY();
    }
    
    /**
     * 
     * @param p
     * @return true if p intersects the box, false otherwise.
     */
    public boolean intersectsPoint(PointIn2D p) {
        if ((p.getX() >= xLL) && (p.getX() <= xUR) && (p.getY() >= yLL) && (p.getY() <= yUR)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Tests for overall of the box with the other box b.
     * @param b
     * @return True if they overlap at all, false otherwise.
     */
    public boolean intersectsBox(BoxIn2D b) {
        if ((this.xLL > b.xUR) || (this.xUR < b.xLL) || (this.yLL > b.yUR) ||(this.yUR < b.yLL)) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Expands the box so that it includes the box b.
     * TEST!!!
     * @param b
     */
    public void expandToIncludeBox(BoxIn2D b) {
        if (xLL > b.xLL) {
            xLL = b.xLL;
        }
        if (xUR < b.xUR) {
            xUR = b.xUR;
        }
        if (yLL > b.yLL) {
            yLL = b.yLL;
        }
        if (yUR < b.yUR) {
            yUR = b.yUR;
        }
    }
    
    /**
     * 
     * @return
     */
    public BoxIn2D getNWquad() {
        double newXLL = xLL, newYLL = 0.5*(yUR + yLL), newXUR = 0.5*(xUR + xLL), newYUR = yUR;
        return new BoxIn2D(newXLL, newYLL, newXUR, newYUR);
    }
    
    /**
     * 
     * @return
     */
    public BoxIn2D getNEquad() {
        double newXLL = 0.5*(xUR + xLL), newYLL = 0.5*(yUR + yLL), newXUR = xUR, newYUR = yUR;
        return new BoxIn2D(newXLL, newYLL, newXUR, newYUR);
    }
    
    /**
     * 
     * @return
     */
    public BoxIn2D getSWquad() {
        double newXLL = xLL, newYLL = yLL, newXUR = 0.5*(xUR + xLL), newYUR = 0.5*(yUR + yLL);
        return new BoxIn2D(newXLL, newYLL, newXUR, newYUR);
    }
    
    /**
     * 
     * @return
     */
    public BoxIn2D getSEquad() {
        double newXLL = 0.5*(xUR + xLL), newYLL = yLL, newXUR = xUR, newYUR = 0.5*(yUR + yLL);
        return new BoxIn2D(newXLL, newYLL, newXUR, newYUR);
    }
    
    /**
     * 
     * @return
     */
    public PointIn2D getCenterPoint() {
        return new PointIn2D(getCenterXcoord(), getCenterYcoord());
    }
    
    /**
     * 
     * @return
     */
    public PointIn2D getSouthEdgeMidpoint() {
        return new PointIn2D(getCenterXcoord(), yLL);
    }
    
    /**
     * 
     * @return
     */
    public PointIn2D getNorthEdgeMidpoint() {
        return new PointIn2D(getCenterXcoord(), yUR);
    }
    
    /**
     * 
     * @return
     */
    public PointIn2D getEastEdgeMidpoint() {
        return new PointIn2D(xUR, getCenterYcoord());
    }
    
    /**
     * 
     * @return
     */
    public PointIn2D getWestEdgeMidpoint() {
        return new PointIn2D(xLL, getCenterYcoord());
    }
    
    /**
     * 
     * @return
     */
    public PointIn2D getNWcorner() {
        return new PointIn2D(xLL, yUR);
    }
    
    /**
     * 
     * @return
     */
    public PointIn2D getNEcorner() {
        return new PointIn2D(xUR, yUR);
    }
    
    /**
     * 
     * @return
     */
    public PointIn2D getSWcorner() {
        return new PointIn2D(xLL, yLL);
    }
    
    /**
     * 
     * @return
     */
    public PointIn2D getSEcorner() {
        return new PointIn2D(xUR, yLL);
    }
    
    /**
     * THIS METHOD IS NOT FINISHED!!!!
     * @param ln
     * @return The line segment clipped to fit within the box.  If the line
     * segment is entirely outside the box, <code>null</code> is returned.
     */
    public EdgeIn2D clipEdgeToBox(EdgeIn2D ln) {
        EdgeIn2D res = null;
        if (ln != null) {
            if ((ln.intersects(this)) && (ln.hasRealCoordinates()) && (this.hasRealCoordinates())) {
                boolean fmIsIn = this.intersectsPoint(ln.getFromVertex());
                boolean toIsIn = this.intersectsPoint(ln.getToVertex());
                EdgeIn2D[] edges = null;
                int pointCount = 0;
                PointIn2D newFmVertex = null, newToVertex = null;
                if ((fmIsIn) && (toIsIn)) {
                    res = new EdgeIn2D(ln);
                } else if ((fmIsIn) && (!toIsIn)) {  // find a new to vertex
                    edges = this.getEdges();
                    for (int i = 0; i < edges.length; i++) {
                        if (ln.intersects(edges[i])) {
                            newToVertex = ln.getIntersection(edges[i]);
                            pointCount++;
                        }
                    }
                    if ((pointCount == 1) && (newToVertex != null)) {
                        res = new EdgeIn2D(ln.getFromVertex(), newToVertex);
                    } else {
                        System.err.println("clipEdgeToBox() failed to correctly find the vertices of the new edge.");
                    }
                } else if ((!fmIsIn) && (toIsIn)) {  // find a new from vertex
                    edges = this.getEdges();
                    for (int i = 0; i < edges.length; i++) {
                        if (ln.intersects(edges[i])) {
                            newFmVertex = ln.getIntersection(edges[i]);
                            pointCount++;
                        }
                    }
                    if ((pointCount == 1) && (newFmVertex != null)) {
                        res = new EdgeIn2D(newFmVertex, ln.getToVertex());
                    } else {
                        System.err.println("clipEdgeToBox() failed to correctly find the vertices of the new edge.");
                    }
                } else {  // find two new vertices
                    edges = this.getEdges();
                    for (int i = 0; i < edges.length; i++) {
                        if (ln.intersects(edges[i])) {
                            if (newFmVertex == null) {
                                newFmVertex = ln.getIntersection(edges[i]);
                            } else {
                                newToVertex = ln.getIntersection(edges[i]);
                            }
                            pointCount++;
                        }
                    }
                    if ((pointCount == 2) && (newFmVertex != null) && (newToVertex != null)) {
                        // check order
                        if (newFmVertex.distanceTo(ln.getFromVertex()) <= newFmVertex.distanceTo(ln.getToVertex())) {
                            res = new EdgeIn2D(newFmVertex, newToVertex);
                        } else {
                            res = new EdgeIn2D(newToVertex, newFmVertex);
                        }
                    } else {
                        System.err.println("clipEdgeToBox() failed to correctly find the vertices of the new edge.");
                    }
                }
            }
        }
        // FOR TESTING
        /*
        if (res != null) {
            System.out.println("clipEdgeToBox() returns:\n" + res.toString() + "\n");
        } else {
            System.out.println("clipEdgeToBox() returns null with:");
            System.out.println(this.toString() + ln.toString() + "\n");
        }
        */
        return res;
    }
    
    @Override public String toString() {
        String res = "BoxIn2D:\n\tlower-left x = " + xLL + 
                "\tupper-right x = " + xUR + "\tlower-left y = " +
                yLL + "\tupper-right y = " + yUR;
        return res;
    }
    
    /**
     * 
     */
    public void display() {
        System.out.println("BoxIn2D:");
        System.out.println("    lower-left x = " + xLL);
        System.out.println("    upper-right x = " + xUR);
        System.out.println("    lower-left y = " + yLL);
        System.out.println("    upper-right y = " + yUR);
    }
}
