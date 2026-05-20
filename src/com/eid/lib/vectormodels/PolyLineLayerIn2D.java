/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.vectormodels;

import java.util.*;
import com.eid.lib.geometricprimitives.*;

/**
 * PUT QUERIES IN VectorModelFunctions...
 * @author jeff
 */
public class PolyLineLayerIn2D implements VectorLayerBehavior {
    ArrayList<PolyLineIn2D> lineLayer = null;
    BoxIn2D mbb = null; /** minimum bounding box for polyline layer*/
    
    /**
     * 
     */
    public PolyLineLayerIn2D() {
        lineLayer = new ArrayList<PolyLineIn2D>();
        mbb = new BoxIn2D();
    }
    
    /**
     * Adds a PolyLineIn2D shape to layer.
     * @param p
     */
    public void addPolyLine(PolyLineIn2D p) {
        if (!lineLayer.contains(p)) {
            boolean setMBB = false;
            if (lineLayer.size() == 0) {
                setMBB = true;
            }
            lineLayer.add(p);
            if (p != null) {
                if (setMBB) {
                    mbb.reset(p.getMinimumBoundingBox());
                } else {
                    mbb.expandToIncludeBox(p.getMinimumBoundingBox());
                }
            }
        }
    }
    
    // clear, remove PolyLines, etc.
    
    // VectorLayerBehavior interface
    // common setter methods
    
    /**
     * 
     */
    public void addShape() {
        if (lineLayer == null) {
            lineLayer = new ArrayList<PolyLineIn2D>();
        }
        lineLayer.add(new PolyLineIn2D());
    }
    
    /**
     * 
     * @param ns
     */
    public void addShapes(int ns) {
        if (lineLayer == null) {
            lineLayer = new ArrayList<PolyLineIn2D>();
        }
        for (int i = 0; i < ns; i++) {
            lineLayer.add(new PolyLineIn2D());
        }
    }
    
    /**
     * 
     * @param s
     * @param np
     */
    public void addPartsToShape(int s, int np) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            for (int i = 0; i < np; i++) {
                lineLayer.get(s).addNewPolyLinePart();
            }
        }
    }
    
    /**
     * 
     * @param s
     * @param p
     * @param nv
     */
    public void addVerticesToPart(int s, int p, int nv) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            if ((p >= 0) && (p < lineLayer.get(s).getNumberOfParts())) {
                for (int i = 0 ; i < nv; i++) {
                    lineLayer.get(s).getVertexListForPart(p).addVertex(new PointIn2D());
                }
            }
        }
    }
    
    /**
     * 
     * @param s
     * @param p
     * @param v
     * @param x
     * @param y
     */
    public void setVertexCoordinates(int s, int p, int v, double x, double y) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            if ((p >= 0) && (p < lineLayer.get(s).getNumberOfParts())) {
                if ((v >= 0) && (v < lineLayer.get(s).getVertexListForPart(p).getSize())) {
                    lineLayer.get(s).getVertexListForPart(p).getPoint(v).setCoordinates(x, y);
                }
            }
        }
    }
    
    /**
     * 
     * @param s
     * @param b
     */
    public void setShapeBoundingBox(int s, BoxIn2D b) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            lineLayer.get(s).setBoundingBox(b);
        }
    }
    
    /**
     * 
     * @param b
     */
    public void setLayerBoundingBox(BoxIn2D b) {
        mbb = b;
    }
    
    /**
     * 
     */
    public void recalculateBoundingBoxes() {
        // go through all parts and calculate mbb
        if (mbb == null) {
            mbb = new BoxIn2D();
        }
        for (int i = 0; i < lineLayer.size(); i++) {
            if (i == 0) {
                mbb.reset(lineLayer.get(i).getMinimumBoundingBox());
            } else {
                mbb.expandToIncludeBox(lineLayer.get(i).getMinimumBoundingBox());
            }
        }
    }
    
    /**
     * 
     * @param s
     * @param shpID
     */
    public void setShapeID(int s, int shpID) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            lineLayer.get(s).setShapeID(shpID);
        }
    }
    
    // methods to remove shapes, parts, vertices, clear all
    
    /**
     * 
     * @param s
     * @param p
     */
    public void removeAllVerticesInPart(int s, int p) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            if ((p >= 0) && (p < lineLayer.get(s).getNumberOfParts())) {
                lineLayer.get(s).getVertexListForPart(p).clearAllVertices();
            }
        }
    }
    
    /**
     * 
     * @param s
     */
    public void removeAllPartsInShape(int s) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            lineLayer.get(s).removeAllParts();
        }
    }
    
    /**
     * 
     * @param s
     */
    public void removeShape(int s) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            lineLayer.remove(s);
        }
    }
    
    /**
     * 
     */
    public void removeAllShapes() {
        if (lineLayer != null) {
            lineLayer.clear();
        }
    }
    
    // common getter methods
    
    /**
     * 
     * @return number of shapes.
     */
    public int getNumberOfShapes() {
        if (lineLayer != null) {
            return lineLayer.size();
        } else {
            return 0;
        }
    }
    
    /**
     * 
     * @param s
     * @return number of parts in shape.
     */
    public int getNumberOfPartsInShape(int s) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size()) && (lineLayer.get(s) != null)) {
            return lineLayer.get(s).getNumberOfParts();
        } else {
            return 0;
        }
    }
    
    /**
     * 
     * @param s
     * @param p
     * @return number of vertices in shape and part.
     */
    public int getNumberOfVerticesInShapePart(int s, int p) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            if ((p >= 0) && (p < lineLayer.get(s).getNumberOfParts())) {
                return lineLayer.get(s).getVertexListForPart(p).getSize();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    /**
     * 
     * @return bouding box.
     */
    public BoxIn2D getLayerBoundingBox() {
        return mbb;
    }
    
    /**
     * 
     * @param s
     * @return shape bounding box.
     */
    public BoxIn2D getShapeBoundingBox(int s) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            return lineLayer.get(s).getMinimumBoundingBox();
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @param s
     * @param p
     * @return vertex list.
     */
    public VertexListIn2D getShapePartVertexList(int s, int p) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            if ((p >= 0) && (p < lineLayer.get(s).getNumberOfParts())) {
                return lineLayer.get(s).getVertexListForPart(p);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @param s
     * @param p
     * @param v
     * @return vertex.
     */
    public PointIn2D getShapePartVertex(int s, int p, int v) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            if ((p >= 0) && (p < lineLayer.get(s).getNumberOfParts())) {
                if ((v >= 0) && (v < lineLayer.get(s).getVertexListForPart(p).getSize())) {
                    return lineLayer.get(s).getVertexListForPart(p).getPoint(v);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @param s
     * @return shape ID.
     */
    public int getShapeID(int s) {
        if ((lineLayer != null) && (s >= 0) && (s < lineLayer.size())) {
            return lineLayer.get(s).getShapeID();
        } else {
            return -1;
        }
    }
}
