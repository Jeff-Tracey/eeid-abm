package com.eid.lib.agents.graph;

import java.io.*;
import com.eid.lib.geometricprimitives.*;

/**
 * A class for a directed edge in a graph or network
 *
 * Created: 24 Janurary 2008
 *
 * @author Jeff A. Tracey
 * Copyright 2008 Jeff A. Tracey, SigmaLogistic Consulting.
 * All rights reserved.
 */
public class GraphEdge implements Serializable {
    /**  */
    private GraphEdgeData edgeData = null;
    /**  */
    private GraphNode fmNode = null;
    /**  */
    private GraphNode toNode = null;
    
    private boolean isDirected;
    
    /**
     * Don't know if I should even allow this...
     */
    public GraphEdge() {
        edgeData = null;
        fmNode = null;
        toNode = null;
        isDirected = false;
    }
    
    /**
     * 
     */
    public GraphEdge(GraphNode f, GraphNode t, boolean isDir, GraphEdgeData ed) {
        fmNode = f;
        toNode = t;
        edgeData = ed;
        ed.setEdgeReference(this); // also updates geometry in edgeData
        // what do we do about the case where f == t ?
        // updateEdgeGeometryFromNodeLocations();
        // IN THE NOT-SO DISTANT FUTURE I WILL ALLOW THREE OPTIONS
        // (a) a straight line
        // (b) an arc
        // (c) a line taken from the edge data (i. e. spatial networks)
        isDirected = isDir;
    }
    
    // MORE METHODS
    
    /**
     * 
     * @return the data for the edge.
     */
    public GraphEdgeData getEdgeData() {
        return edgeData;
    }
    
    /**
     * 
     * @return the from-node for the edge.
     */
    public GraphNode getFromNode() {
        return fmNode;
    }
    
    /**
     * 
     * @return the to-node for the edge.
     */
    public GraphNode getToNode() {
        return toNode;
    }
    
    
    /**
     * @param n A <code>GraphNode</code> object to test.
     * @return True if one of the nodes to which the edge is connected is the
     * argument object.
     */
    boolean isConnectedToNode(GraphNode n) {
        if ((toNode == n) || (fmNode == n)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 
     * @return True is the edge is directed and false otherwise.  This may be
     * used to determine if an arrow should be drawn at the end of a line icon.
     */
    public boolean isDirectedEdge() {
        return isDirected;
    }
    
    /**
     * WILL UPDATE TO ALLOW DIFFERENT KINDS OF LINE ICONS TO BE DRAWN (straight,
     * an arc, or a polyline from the edge data).
     */
    void updateEdgeGeometryFromNodeLocations() {
        VertexListIn2D tmp = new VertexListIn2D();
        tmp.addVertex(fmNode.getNodeData().getLocation());
        tmp.addVertex(toNode.getNodeData().getLocation());
        edgeData.setEdgeGeometry(tmp);
    }
    
    /**
     * 
     * 
     * @param ed
     */
    public void setEdgeData(GraphEdgeData ed) {
        edgeData = ed;
        ed.setEdgeReference(this);
    }
}
