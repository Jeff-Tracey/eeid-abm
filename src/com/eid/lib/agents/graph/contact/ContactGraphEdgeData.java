/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents.graph.contact;

import com.eid.lib.geometricprimitives.*;
import com.eid.lib.agents.graph.*;

/**
 * MERGE WITH EDGE
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class ContactGraphEdgeData implements GraphEdgeData {
    private GraphEdge edge;
    private VertexListIn2D line;
    private double distance;
    private int recordIndex;
    
    /**
     * 
     */
    public ContactGraphEdgeData() {
        edge = null;
        line = null;
        distance = Double.NaN;
    }
    
    /**
     * 
     * @param e
     */
    public ContactGraphEdgeData(GraphEdge e) {
        edge = e;
        updateDistance();
    }
    
    /**
     * This method is called by the GraphEdge constructor...
     * @param e
     */
    public void setEdgeReference(GraphEdge e) {
        edge = e;
        updateDistance();
    }
    
    /**
     * 
     * @return
     */
    public GraphEdge getEdgeReference() {
        return edge;
    }
    
    /**
     * 
     * @param vl This argument is ignored, the line geomtry is updated from the
     * locations of the form and to nodes.
     */
    public void setEdgeGeometry(VertexListIn2D vl) {
        updateDistance(); // this slows things down much
    }
    
    /**
     * 
     * @return
     */
    public VertexListIn2D getEdgeGeometry() {
        return line;
    }
    
    /**
     * 
     * @param i
     */
    public void setIndex(int i) {
        if (i >= 0) {
            recordIndex = i;
        } else {
            recordIndex = -1;
        }
    }
    
    /**
     * 
     * @return
     */
    public int getIndex() {
        return recordIndex;
    }
    
    public void setPlotColorID(int i) {
        // nothing to do, gets from node data
    }
    
    public int getPlotColorID() {
        int tmpIndex = -1;
        if ((getEdgeReference() != null) && (getEdgeReference().getFromNode() != null) && (getEdgeReference().getToNode() != null)) {
            if ((getEdgeReference().getFromNode().getNodeData() != null) && (getEdgeReference().getToNode().getNodeData() != null)) {
                tmpIndex = getEdgeReference().getFromNode().getNodeData().getPlotColorID();
                if (getEdgeReference().getToNode().getNodeData().getPlotColorID() < tmpIndex) {
                    tmpIndex = getEdgeReference().getToNode().getNodeData().getPlotColorID();
                }
            }
        }
        return tmpIndex;
    }
    
    /**
     * 
     */
    public void updateDistance() {
        if (edge != null) {
            if ((edge.getFromNode() != null) && 
                    (edge.getToNode() != null) &&
                    (edge.getFromNode().getNodeData() instanceof ContactGraphNodeData) && 
                    (edge.getToNode().getNodeData() instanceof ContactGraphNodeData) && 
                    (edge.getFromNode().getNodeData().getLocation() != null) && 
                    (edge.getToNode().getNodeData().getLocation() != null)
                    ) {
                if (line != null) {
                    line.clearAllVertices();
                } else {
                    line = new VertexListIn2D();
                }
                line.addVertex(edge.getFromNode().getNodeData().getLocation());
                line.addVertex(edge.getToNode().getNodeData().getLocation());
                distance = edge.getFromNode().getNodeData().getLocation().distanceTo(edge.getToNode().getNodeData().getLocation());
            } else {
                line = null;
                distance = Double.NaN;
            }
        }
    }
    
    /**
     * 
     * @return
     */
    public double getDistance() {
        return distance;
    }
    
    /**
     * 
     * @return
     */
    public double getDeltaX() {
        if ((line != null) && (line.getSize() == 2)) {
            return line.getPoint(1).getX() - line.getPoint(0).getX();
        } else {
            return Double.NaN;
        }
    }
    
    /**
     * 
     * @return
     */
    public double getDeltaY() {
        if ((line != null) && (line.getSize() == 2)) {
            return line.getPoint(1).getY() - line.getPoint(0).getY();
        } else {
            return Double.NaN;
        }
    }
}
