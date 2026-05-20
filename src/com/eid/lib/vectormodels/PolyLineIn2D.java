/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.vectormodels;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import com.eid.lib.geometricprimitives.*;

/**
 * Implement serializable...
 * 
 * 
 * 
 * @author jeff
 */
public class PolyLineIn2D {
    ArrayList<VertexListIn2D> partList = null; /** array of VertexListIn2D, one for each part */
    /** line icon(s) for plotting the polyline */
    /** point icon(s) for plotting points over lines if desired */
    BoxIn2D mbb = null; /** minimum bounding box for polyline */
    boolean firstMBBset = false;
    int shapeID = -1;
    /**
     * 
     */
    public PolyLineIn2D() {
        partList = new ArrayList<VertexListIn2D>();
        mbb = new BoxIn2D();
        firstMBBset = false;
        // line icon...
    }
    
    /**
     * 
     * @param b
     */
    public void setBoundingBox(BoxIn2D b) {
        mbb = b;
    }
    
    /**
     * SHOULD WE HAVE A FORM THAT ALLOWS THE MINIMUM SIZE TO BE SET?
     */
    public void addNewPolyLinePart() {
        partList.add(new VertexListIn2D());
    }
    
    /**
     * 
     * @param v an existing vertex list for the part.
     */
    public void addNewPolyLinePart(VertexListIn2D v) {
        partList.add(v);
    }
    
    /**
     * 
     * @param n
     */
    public void addNewPolyLineParts(int n) {
        if (n > 0) {
            for (int i = 0; i < n; i++) {
                partList.add(new VertexListIn2D());
            }
        }
    }
    
    /**
     * 
     * @param pID
     * @param v
     */
    public void addVertexToPart(int pID, PointIn2D v) {
        if ((pID >= 0) && (pID < partList.size())) {
            partList.get(pID).addVertex(v);
            if (firstMBBset) {
                mbb.expandToIncludePoint(v);
            } else {
                mbb.setToPoint(v);
                firstMBBset = true;
            }
        }
    }
    
    /**
     * 
     * @param pID
     */
    public void removePart(int pID) {
        if ((pID >= 0) && (pID < partList.size())) {
            partList.remove(pID);
        }
        // reset bounding box
        firstMBBset = false;
        for (int i = 0; i < partList.size(); i++) {
            for (int j = 0; j < partList.get(i).getSize(); j++) {
                if (firstMBBset) {
                    mbb.expandToIncludePoint(partList.get(i).getPoint(j));
                } else {
                    mbb.setToPoint(partList.get(i).getPoint(j));
                    firstMBBset = true;
                }
            }
        }
    }
    
    /**
     * 
     * @param pID
     */
    public void removeAllVerticesInPart(int pID) {
        if ((pID >= 0) && (pID < partList.size())) {
            partList.get(pID).clearAllVertices();
        }
        // reset bounding box
        firstMBBset = false;
        for (int i = 0; i < partList.size(); i++) {
            for (int j = 0; j < partList.get(i).getSize(); j++) {
                if (firstMBBset) {
                    mbb.expandToIncludePoint(partList.get(i).getPoint(j));
                } else {
                    mbb.setToPoint(partList.get(i).getPoint(j));
                    firstMBBset = true;
                }
            }
        }
    }
    
    /**
     * 
     */
    public void removeAllVerticesInAllParts() {
        for (int i = 0; i < partList.size(); i++) {
            partList.get(i).clearAllVertices();
        }
        mbb.reset(0.0, 0.0, 0.0, 0.0);
        firstMBBset = false;
    }
    
    /**
     * clear all parts
     */
    public void removeAllParts() {
        partList.clear();
        mbb.reset(0.0, 0.0, 0.0, 0.0);
        firstMBBset = false;
    }
    
    // COPY X COORDS TO ARRAY
    // COPY Y COORDS TO ARRAY
    
    // SET X, Y COORDS FROM ARRAYS
    
    // set, get VertexListIn2D
    
    /**
     * 
     * @return number of parts.
     */
    public int getNumberOfParts() {
        return partList.size();
    }
    
    /**
     * 
     * @param i
     * @return number of vertices in part.
     */
    public int getNumberOfVerticesInPart(int i) {
        if ((i >= 0) && (i < partList.size())) {
            return partList.get(i).getSize();
        } else {
            return 0;
        }
    }
    
    /**
     * 
     * @param i
     * @return vertex list.
     */
    public VertexListIn2D getVertexListForPart(int i) {
        if ((i >= 0) && (i < partList.size())) {
            return partList.get(i);
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @param p
     * @param v
     * @return vertex.
     */
    public PointIn2D getVertexInPart(int p, int v) {
        if ((p >= 0) && (p < partList.size())) {
            if ((v >= 0) && (v < partList.get(p).getSize())) {
                return partList.get(p).getPoint(v);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @return bouding box.
     */
    public BoxIn2D getMinimumBoundingBox() {
        return mbb;
    }
    
    /**
     * 
     * @param i
     */
    public void setShapeID(int i) {
        shapeID = i;
    }
    
    /**
     * 
     * @return shape ID.
     */
    public int getShapeID() {
        return shapeID;
    }
    
    
    
    /**
     * 
     */
    public void display() {
        System.out.println("PolyLineIn2D:");
        System.out.println("contains " + partList.size() + " parts");
        for (int i = 0; i < partList.size(); i++) {
            System.out.println("    part " + i + " has " + partList.get(i).getSize() + " vertices");
        }
    }
}
