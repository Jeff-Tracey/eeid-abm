/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eid.lib.vectormodels;

import java.util.*;
import com.eid.lib.geometricprimitives.*;

/**
 * The shapes do not have parts (or, only 1 part) unlike PolyLines and Polygons.
 * @author jeff
 */
public class PointLayerIn2D implements VectorLayerBehavior {
    private ArrayList<VertexListIn2D> shapeList = null;
    private ArrayList<Integer> shapeIDs = null;
    private ArrayList<BoxIn2D> shapeMBBs = null;
    private BoxIn2D layerMBB = null;
    // common setter methods
    
    /**
     * 
     */
    public PointLayerIn2D() {
        shapeList = new ArrayList<VertexListIn2D>();
        shapeIDs = new ArrayList<Integer>();
        shapeMBBs = new ArrayList<BoxIn2D>();
        layerMBB = new BoxIn2D();
    }
    
    /**
     * 
     */
    public void addShape() {
        if ((shapeList != null) && (shapeIDs != null)) {
            VertexListIn2D tmpList = new VertexListIn2D();
            shapeList.add(tmpList);
            shapeIDs.add(shapeList.size() - 1);
            shapeMBBs.add(new BoxIn2D());
        }
    }

    /**
     * 
     * @param ns
     */
    public void addShapes(int ns) {
        if ((shapeList != null) && (shapeIDs != null) && (ns > 0)) {
            for (int i = 0; i < ns; i++) {
                VertexListIn2D tmpList = new VertexListIn2D();
                shapeList.add(tmpList);
                shapeIDs.add(shapeList.size() - 1);
                shapeMBBs.add(new BoxIn2D());
            }
        }
    }

    /**
     * This does nothhing for point layers
     * @param s
     * @param np
     */
    public void addPartsToShape(int s, int np) {
        // do nothing
    }

    /**
     * 
     * @param s shape index
     * @param p does not matter for points
     * @param nv number of vertices to add
     */
    public void addVerticesToPart(int s, int p, int nv) {
        if ((s >= 0) && (s < shapeList.size()) && (nv > 0)) {
            VertexListIn2D shpRef = shapeList.get(s);
            for (int i = 0; i < nv; i++) {
                shpRef.addVertex(new PointIn2D());
            }
            
        }
    }

    /**
     * 
     * @param s
     * @param p does not matter for points
     * @param v
     * @param x
     * @param y
     */
    public void setVertexCoordinates(int s, int p, int v, double x, double y) {
        if((shapeList != null) && (s >= 0) && (s < shapeList.size())  && (v >= 0) && (v < shapeList.get(s).getSize())) {
            VertexListIn2D tmp = shapeList.get(s);
            tmp.getPoint(v).setCoordinates(x, y);
            // UPDATE shapeMBBs.get(s) ?????????
        }
    }
    
    
    
    

    /**
     * 
     * @param s index of the shape.
     * @param b the bounding box to set for the shape.
     */
    public void setShapeBoundingBox(int s, BoxIn2D b) {
        if ((shapeList != null) && (shapeMBBs != null) && (s >= 0) && (s < shapeMBBs.size())) {
            shapeMBBs.set(s, b);
        }
    }

    /**
     * 
     * @param b
     */
    public void setLayerBoundingBox(BoxIn2D b) {
        layerMBB = b;
    }

    /**
     * NOT FINISHED!
     */
    public void recalculateBoundingBoxes() {
        //
    }

    /**
     * 
     * @param s
     * @param shpID
     */
    public void setShapeID(int s, int shpID) {
        if ((shapeIDs != null) && (s >= 0) && (s < shapeIDs.size()) && (shpID >= 0)) {
            shapeIDs.set(s, shpID);
        }
    }

    // methods to remove shapes, parts, vertices, clear all
    /**
     * 
     * @param s shape index.
     * @param p does not matter for points.
     */
    public void removeAllVerticesInPart(int s, int p) {
        if ((shapeList != null) && (s >= 0) && (s < shapeList.size())) {
            shapeList.get(s).clearAllVertices();
        }
    }

    /**
     * NOT FINISHED!
     * @param s
     */
    public void removeAllPartsInShape(int s) {
        //
    }

    /**
     * NOT FINISHED!
     * @param s
     */
    public void removeShape(int s) {
        //
    }

    /**
     * 
     */
    public void removeAllShapes() {
        shapeList.clear();
        shapeIDs.clear();
        shapeMBBs.clear();
        layerMBB = new BoxIn2D();
    }

    // common getter methods
    
    /**
     * 
     * @return number of shapes.
     */
    public int getNumberOfShapes() {
        return shapeList.size();
    }

    /**
     * 
     * @param s
     * @return number of parts in shape.
     */
    public int getNumberOfPartsInShape(int s) {
        if ((shapeList != null) && (s >= 0) && (s < shapeList.size())) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 
     * @param s shape index.
     * @param p does not matter for points.
     * @return number of vertices in part.
     */
    public int getNumberOfVerticesInShapePart(int s, int p) {
        if ((shapeList != null) && (s >= 0) && (s < shapeList.size())) {
            return shapeList.get(s).getSize();
        } else {
            return 0;
        }
    }

    /**
     * 
     * @return bouding box.
     */
    public BoxIn2D getLayerBoundingBox() {
        return layerMBB;
    }

    /**
     * 
     * @param s index for the shape that we are getting the bounding box from.
     * @return bounding box for shape
     */
    public BoxIn2D getShapeBoundingBox(int s) {
        if ((shapeList != null) && (shapeMBBs != null) && (s >= 0) && (s < shapeMBBs.size())) {
            return shapeMBBs.get(s);
        } else {
            return null;
        }
    }

    /**
     * 
     * @param s shape index.
     * @param p does not matter for points.
     * @return vertices for shape and part.
     */
    public VertexListIn2D getShapePartVertexList(int s, int p) {
        if ((shapeList != null) && (s >= 0) && (s < shapeList.size())) {
            return shapeList.get(s);
        } else {
            return null;
        }
    }

    /**
     * 
     * @param s shape index.
     * @param p does not matter for points.
     * @param v vertex index.
     * @return vertex.
     */
    public PointIn2D getShapePartVertex(int s, int p, int v) {
        if ((shapeList != null) && (s >= 0) && (s < shapeList.size())  && (v >= 0) && (v < shapeList.get(s).getSize())) {
            return shapeList.get(s).getPoint(v);
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
        if ((shapeIDs != null) && (s >= 0) && (s < shapeIDs.size())) {
            return shapeIDs.get(s);
        } else {
            return -1;  // indicates no shape
        }
    }
}
