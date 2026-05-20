/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.vectormodels;

import com.eid.lib.geometricprimitives.*;

/**
 * This is an interface implemented by all vector model layers.  This provides
 * some common functionality and allows IO methods (for example) to work with
 * the interface rather than a specific implementation of it.
 * @author jeff
 */
public interface VectorLayerBehavior {
    // common setter methods
    public void addShape(); // adds new shape to end
    public void addShapes(int ns); // add ns shapes to end of layer
    public void addPartsToShape(int s, int np);
    public void addVerticesToPart(int s, int p, int nv); // create np vertices
    public void setVertexCoordinates(int s, int p, int v, double x, double y);
    public void setShapeBoundingBox(int s, BoxIn2D b);
    public void setLayerBoundingBox(BoxIn2D b);
    public void recalculateBoundingBoxes();
    public void setShapeID(int s, int shpID);
    
    // methods to remove shapes, parts, vertices, clear all?
    public void removeAllVerticesInPart(int s, int p);
    public void removeAllPartsInShape(int s);
    public void removeShape(int s);
    public void removeAllShapes();
    
    // common getter methods
    public int getNumberOfShapes();
    public int getNumberOfPartsInShape(int s);
    public int getNumberOfVerticesInShapePart(int s, int p);
    public BoxIn2D getLayerBoundingBox();
    public BoxIn2D getShapeBoundingBox(int s); // s is shape index
    public VertexListIn2D getShapePartVertexList(int s, int p);
    public PointIn2D getShapePartVertex(int s, int p, int v);
    public int getShapeID(int s);
}
