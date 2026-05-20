package com.eid.lib.agents.graph;

import java.util.*;
import java.io.*;

import com.eid.lib.geometricprimitives.*;
import com.eid.lib.vectormodels.*;

/**
 * A node for a graph or network.
 *
 * Created: 24 Janurary 2008
 *
 * @author Jeff A. Tracey
 * Copyright 2008 Jeff A. Tracey, SigmaLogistic Consulting.
 * All rights reserved.
 */
public class GraphNode implements Serializable {
    /**  */
    private GraphNodeData nodeData = null;
    /** A list of edges that start from this node. */
    private ArrayList<GraphEdge> fmEdgeList = null;
    /** A list of edges that go to this node. */
    private ArrayList<GraphEdge> toEdgeList = null;
    //
    
    /**
     *
     */
    public GraphNode() {
        nodeData = null;
        fmEdgeList = new ArrayList<GraphEdge>();
        toEdgeList = new ArrayList<GraphEdge>();
        nodeData.setNodeReference(this);
    }
    
    /**
     * 
     * @param dat
     */
    public GraphNode(GraphNodeData dat) {
        nodeData = dat;   // do I want to just set the reference or deep copy?
        fmEdgeList = new ArrayList<GraphEdge>();
        toEdgeList = new ArrayList<GraphEdge>();
        nodeData.setNodeReference(this);
    }
    
    /**
     * 
     * @return the data for the node.
     */
    public GraphNodeData getNodeData() {
        return nodeData;
    }
    
    
    /**
     * 
     * @param e
     */
    void insertEdge(GraphEdge e) {
        if (e.getFromNode() == this) {
            if (!fmEdgeList.contains(e)) {
                fmEdgeList.add(e);
                //e.updateEdgeGeometryFromNodeLocations();
            }
        }
        if (e.getToNode() == this) {
            if (!toEdgeList.contains(e)) {
                toEdgeList.add(e);
                //e.updateEdgeGeometryFromNodeLocations();
            }
        }
    }
    
    
    
    
    /**
     * 
     * @param nd
     */
    public void setNodeData(GraphNodeData nd) {
        nodeData = nd;
        nodeData.setNodeReference(this);
    }
    
    /**
     * Note that this method is a bit dangerous because it can leave haning
     * edges if the edge is not removed from the other node.  Use with caution!
     * @param e
     */
    void removeEdge(GraphEdge e) {
        if (fmEdgeList.contains(e)) fmEdgeList.remove(e);
        if (toEdgeList.contains(e)) toEdgeList.remove(e);
    }
    
    /**
     * Returns the number of edges in the to (out) edge list.  If the node
     * is connected to another with more than one edge, it counts all connections.
     * @return The out degree of the node.
     */
    public int getNodeDegreeIn() {
        return toEdgeList.size();
    }
    
    /**
     * 
     * @return
     */
    public int getNodeDegreeOut() {
        return fmEdgeList.size();
    }
    
    
    
    /**
     * 
     * @param i
     * @return
     */
    public GraphEdge getEdgeOut(int i) {
        if ((i >= 0) && (i < fmEdgeList.size())) {
            return fmEdgeList.get(i);
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @return true if the node is a root node (has no parents) but has 
     * one or more children.
     */
    public boolean isRoot() {
        if ((toEdgeList.size() == 0) && (fmEdgeList.size() > 0)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 
     * @return true if node is a leaf (has no children) but has at least one
     * parent.
     */
    public boolean isLeaf() {
        if ((toEdgeList.size() > 0) && (fmEdgeList.size() == 0)) {
            return true;
        } else {
            return false;
        }
    }
    
}
