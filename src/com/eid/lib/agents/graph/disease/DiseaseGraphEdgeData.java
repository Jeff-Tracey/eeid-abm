/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents.graph.disease;

import com.eid.lib.geometricprimitives.*;
import com.eid.lib.agents.graph.*;

/**
 * MERGE WITH EDGE
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
class DiseaseGraphEdgeData implements GraphEdgeData {
    //
    private static int edgeIDcounter = 0;
    private int edgeID;
    //
    private GraphEdge edgeRef;
    private VertexListIn2D line;
    //
    private double euclidTransDistance; // distance between transmissions
    private double elapseTransTime;     // time between transmissions
    // 
    
    /**
     * 
     */
    public DiseaseGraphEdgeData() {
        // must get distance and time from nodes...may require args
        edgeID = edgeIDcounter;
        edgeIDcounter++;
        edgeRef = null;
        line = null;
        euclidTransDistance = Double.NaN;
        elapseTransTime = Double.NaN;
    }
    
    /**
     * 
     * @param e
     */
    public DiseaseGraphEdgeData(GraphEdge e) {
        // must get distance and time from nodes...may require args
        edgeID = edgeIDcounter;
        edgeIDcounter++;
        edgeRef = e;
        setEdgeReference(e);
    }
    
    /**
     * This method is called by the GraphEdge constructor, so it can be used for
     * additional setup of the edge data once the edge itself has been initialized
     * @param e
     */
    public void setEdgeReference(GraphEdge e) {
        edgeRef = e;
        if ((edgeRef != null) && (edgeRef.getFromNode() != null) && (edgeRef.getFromNode().getNodeData() != null) && (edgeRef.getToNode() != null) && (edgeRef.getToNode().getNodeData() != null)) {
            if ((edgeRef.getFromNode().getNodeData() instanceof DiseaseGraphNodeData) && (edgeRef.getToNode().getNodeData() instanceof DiseaseGraphNodeData)) {
                DiseaseGraphNodeData fmDat = (DiseaseGraphNodeData)edgeRef.getFromNode().getNodeData();
                DiseaseGraphNodeData toDat = (DiseaseGraphNodeData)edgeRef.getToNode().getNodeData();
                //
                if ((fmDat.getLocation() != null) && (toDat.getLocation() != null)) {
                    euclidTransDistance = fmDat.getLocation().distanceTo(toDat.getLocation());
                    elapseTransTime = toDat.getInfectionTime() - fmDat.getInfectionTime();
                    line = new VertexListIn2D();
                    line.addVertex(fmDat.getLocation());
                    line.addVertex(toDat.getLocation());
                }
            }
        }
    }
    
    /**
     * 
     * @return
     */
    public GraphEdge getEdgeReference() {
        return edgeRef; // TEMP
    }
    
    /**
     * The argument vl is ignored
     * @param vl
     */
    public void setEdgeGeometry(VertexListIn2D vl) {
        if ((edgeRef != null) && (edgeRef.getFromNode() != null) && (edgeRef.getFromNode().getNodeData() != null) && (edgeRef.getToNode() != null) && (edgeRef.getToNode().getNodeData() != null)) {
            if ((edgeRef.getFromNode().getNodeData() instanceof DiseaseGraphNodeData) && (edgeRef.getToNode().getNodeData() instanceof DiseaseGraphNodeData)) {
                DiseaseGraphNodeData fmDat = (DiseaseGraphNodeData)edgeRef.getFromNode().getNodeData();
                DiseaseGraphNodeData toDat = (DiseaseGraphNodeData)edgeRef.getToNode().getNodeData();
                //
                if ((fmDat.getLocation() != null) && (toDat.getLocation() != null)) {
                    euclidTransDistance = fmDat.getLocation().distanceTo(toDat.getLocation());
                    elapseTransTime = toDat.getInfectionTime() - fmDat.getInfectionTime();
                    line = new VertexListIn2D();
                    line.addVertex(fmDat.getLocation());
                    line.addVertex(toDat.getLocation());
                }
            }
        }
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
            edgeID = i;
        }
    }
    
    /**
     * 
     * @return
     */
    public int getIndex() {
        return edgeID;
    }
    
    public void setPlotColorID(int i) {
        //
    }
    
    public int getPlotColorID() {
        if ((this.getEdgeReference() != null) && (this.getEdgeReference().getFromNode() != null) && (this.getEdgeReference().getFromNode().getNodeData() != null)) {
            return this.getEdgeReference().getFromNode().getNodeData().getPlotColorID();
        } else {
            return -1;
        }
    }
    
    /**
     * 
     * @return
     */
    public double getEuclideanDistanceBetweenTransmissions() {
        return euclidTransDistance;
    }
    
    /**
     * 
     * @return
     */
    public double getElapsedTimeBetweenTransmissions() {
        return elapseTransTime;
    }
}
