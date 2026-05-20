package com.eid.lib.geometricprimitives;

import java.util.*;
import java.io.*;

/**
 * This class represents a list of points that can be used to represent parts of
 * more complex geometric objects such as parts of polylines or parts of
 * polygons.
 *
 * Created: 22 January, 2008
 *
 * @author Jeff A. Tracey
 * Copyright 2008 Jeff A. Tracey, SigmaLogistic Consulting.
 * All rights reserved.
 */
public class VertexListIn2D implements Serializable {
    ArrayList<PointIn2D> vertexList;
    
    /**
     * 
     * 
     */
    public VertexListIn2D() {
        vertexList = new ArrayList<PointIn2D>();
    }
    
    /**
     * 
     * @param p a <code>PointIn2D</code> object to be added to the end of the list
     */
    public void addVertex(PointIn2D p) {
        vertexList.add(p);
    }
    
    /**
     * 
     * @return the number of points in the vertex list.
     */
    public int getSize() {
        return vertexList.size();
    }
    
    /**
     * 
     * @param i The index of the point to get.
     * @return The i-th point in the list, or null if i is out-of-bounds.
     */
    public PointIn2D getPoint(int i) {
        if ((i >= 0) && (i < vertexList.size())) {
            return vertexList.get(i);
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @param ax
     * @param ay
     * @return the point in the vertex list (as a sequence of line segments)
     * nearest to the point (ax, ay).
     */
    public PointIn2D getNearestPointTo(double ax, double ay) {
        if (vertexList.size() == 0) {
            return new PointIn2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        } else if (vertexList.size() == 1) {
            return vertexList.get(0);
        } else {
            EdgeIn2D tmpSeg = new EdgeIn2D();
            PointIn2D tmpPt = new PointIn2D();
            PointIn2D resPt = new PointIn2D();
            double minDist = Double.POSITIVE_INFINITY;
            double tmpDist = 0.0;
            for (int i = 0; i < (vertexList.size() - 1); i++) {
                tmpSeg.setVertices(vertexList.get(i), vertexList.get(i+1));
                tmpPt.setCoordinates(tmpSeg.getNeartestPointOnEdgeTo(ax, ay));
                tmpDist = Math.sqrt((ay - tmpPt.getY())*(ay - tmpPt.getY()) + (ax - tmpPt.getX())*(ax - tmpPt.getX()));
                if (tmpDist < minDist) {
                    minDist = tmpDist;
                    resPt.setCoordinates(tmpPt);
                }
            }
            return resPt;
        }
    }
    
    /**
     * 
     * @param ax
     * @param ay
     * @return the the distance to the point in the vertex list (as a sequence of
     * line segments) to the point (ax, ay).
     */
    public double getDistanceTo(double ax, double ay) {
        if (vertexList.size() == 0) {
            return Double.POSITIVE_INFINITY;
        } else if (vertexList.size() == 1) {
            PointIn2D tmpPt = vertexList.get(0);
            return Math.sqrt((ay - tmpPt.getY())*(ay - tmpPt.getY()) + (ax - tmpPt.getX())*(ax - tmpPt.getX()));
        } else {
            EdgeIn2D tmpSeg = new EdgeIn2D();
            PointIn2D tmpPt = new PointIn2D();
            double minDist = Double.POSITIVE_INFINITY;
            double tmpDist = 0.0;
            for (int i = 0; i < (vertexList.size() - 1); i++) {
                tmpSeg.setVertices(vertexList.get(i), vertexList.get(i+1));
                tmpPt.setCoordinates(tmpSeg.getNeartestPointOnEdgeTo(ax, ay));
                tmpDist = Math.sqrt((ay - tmpPt.getY())*(ay - tmpPt.getY()) + (ax - tmpPt.getX())*(ax - tmpPt.getX()));
                if (tmpDist < minDist) {
                    minDist = tmpDist;
                }
            }
            return minDist;
        }
    }
    
    /**
     * 
     * @return all of the x-coordinates in the vertex list as an array a of double.
     */
    public double[] getAllXcoords() {
        if (vertexList.size() > 0) {
            double[] res = new double[vertexList.size()];
            for (int i = 0; i < vertexList.size(); i++) {
                res[i] = vertexList.get(i).getX();
            }
            return res;
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @return all of the x-coordinates in the vertex list as an array a of
     * double.  This method places the first x-coordinate at the end of the array
     * to close the list.  This is used if it represents the boundary of a polygon.
     */
    public double[] getAllXcoordsClosed() {
        if (vertexList.size() > 0) {
            double[] res = new double[vertexList.size()+1];
            for (int i = 0; i < vertexList.size(); i++) {
                res[i] = vertexList.get(i).getX();
            }
            res[vertexList.size()] = vertexList.get(0).getX();
            return res;
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @param vf
     * @param vt
     * @return all of the x-coordinates in the vertex list between indices vf and
     * vt as an array a of double.
     */
    public double[] getXcoords(int vf, int vt) {
        int fmV = vf, toV = vt;
        if (vf > vt) {
            fmV = vt;
            toV = vf;
        }
        if ((fmV >= 0) && (toV < vertexList.size()) && (toV >= fmV)) {
            double[] res = new double[(toV-fmV+1)];
            for (int i = fmV; i <= toV; i++) {
                res[i-fmV] = vertexList.get(i).getX();
            }
            return res;
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @return all of the y-coordinates in the vertex list as an array a of double.
     */
    public double[] getAllYcoords() {
        if (vertexList.size() > 0) {
            double[] res = new double[vertexList.size()];
            for (int i = 0; i < vertexList.size(); i++) {
                res[i] = vertexList.get(i).getY();
            }
            return res;
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @return all of the y-coorindates in the vertex list as an array a of
     * double.  This method places the first y-coordinate at the end of the array
     * to close the list.  This is used if it represents the boundary of a polygon.
     */
    public double[] getAllYcoordsClosed() {
        if (vertexList.size() > 0) {
            double[] res = new double[vertexList.size()+1];
            for (int i = 0; i < vertexList.size(); i++) {
                res[i] = vertexList.get(i).getY();
            }
            res[vertexList.size()] = vertexList.get(0).getY();
            return res;
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @param vf
     * @param vt
     * @return all of the y-coordinates in the vertex list between indices vf and
     * vt as an array a of double.
     */
    public double[] getYcoords(int vf, int vt) {
        int fmV = vf, toV = vt;
        if (vf > vt) {
            fmV = vt;
            toV = vf;
        }
        if ((fmV >= 0) && (toV < vertexList.size()) && (toV >= fmV)) {
            double[] res = new double[(toV-fmV+1)];
            for (int i = fmV; i <= toV; i++) {
                res[i-fmV] = vertexList.get(i).getY();
            }
            return res;
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @param vf
     * @param vt
     * @return
     */
    public VertexListIn2D getVertices(int vf, int vt) {
        int fmV = vf, toV = vt;
        if (vf > vt) {
            fmV = vt;
            toV = vf;
        }
        if (fmV < 0) {
            fmV = 0;
        }
        if (toV >= vertexList.size()) {
            toV = vertexList.size() - 1;
        }
        if (toV >= fmV) {
            VertexListIn2D res = new VertexListIn2D();
            for (int i = fmV; i <= toV; i++) {
                res.addVertex(vertexList.get(i));
            }
            return res;
        } else {
            return null;
        }
    }
    
    /**
     * 
     */
    public void clearAllVertices() {
        vertexList.clear();
    }
    
    /**
     * 
     */
    public void clearNaNcoords() {
        int n = vertexList.size();
        double tmpX, tmpY;
        for (int i = (n-1); i >= 0; i--) {
            tmpX = vertexList.get(i).getX();
            tmpY = vertexList.get(i).getY();
            if ((Double.isNaN(tmpX)) || (Double.isNaN(tmpY))) {
                vertexList.remove(i);
            }
        }
    }
    
    /**
     * 
     */
    public void display() {
        System.out.println("VertexList:");
        for (int i = 0; i < vertexList.size(); i++) {
            System.out.println("Vextex " + i + " = (" + vertexList.get(i).getX() + ", " + vertexList.get(i).getY() + ")");
        }
    }
}
