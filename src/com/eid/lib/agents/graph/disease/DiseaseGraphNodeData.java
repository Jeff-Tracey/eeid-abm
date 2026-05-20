/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents.graph.disease;

import com.eid.lib.agents.*;
import com.eid.lib.geometricprimitives.*;
import com.eid.lib.agents.graph.*;
import com.eid.lib.agents.graph.contact.*;

/**
 * MERGE WITH NODE
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class DiseaseGraphNodeData implements GraphNodeData {
    // static variables
    private static int nodeIDcounter = 0;
    private static int treeIDcounter = 0;
    
    //
    private GraphNode node;
    private int nodeID;
    private Agent agent;
    private AgentDiseaseState diseaseState;
    private int diseaseTreeID;
    
    // transmssion info
    private PointIn2D transmissionLocation = null;
    private double transmissionTime;
    private boolean startedAsInfective = false;
    private int transmissionPatch;
    
    /**
     * 
     * @param a
     * @param startAsInfective
     */
    public DiseaseGraphNodeData(Agent a, boolean startAsInfective, int initPatch) {
        nodeID = nodeIDcounter;
        nodeIDcounter++;
        node = null;
        agent = a;
        startedAsInfective = startAsInfective;
        if (startAsInfective) {
            diseaseState = AgentDiseaseState.INFECTIVE;
            diseaseTreeID = treeIDcounter;
            treeIDcounter++;
            if (a != null) {
                transmissionLocation = a.getLocation();
            } else {
                transmissionLocation = null;
            }
            transmissionTime = 0.0;
            transmissionPatch = initPatch;
        } else {
            diseaseState = AgentDiseaseState.SUSCEPTIBLE;
            diseaseTreeID = -1;
            transmissionLocation = null;
            transmissionTime = Double.NaN;
            transmissionPatch = -9999;
        }
    }
    
    /**
     * 
     * @param a
     * @param n
     * @param startAsInfective
     */
    public DiseaseGraphNodeData(Agent a, GraphNode n, boolean startAsInfective, int initPatch) {
        nodeID = nodeIDcounter;
        nodeIDcounter++;
        node = n;
        startedAsInfective = startAsInfective;
        if (startAsInfective) {
            diseaseState = AgentDiseaseState.INFECTIVE;
            diseaseTreeID = treeIDcounter;
            treeIDcounter++;
            if (a != null) {
                transmissionLocation = a.getLocation();
            } else {
                transmissionLocation = null;
            }
            transmissionTime = 0.0;
            transmissionPatch = initPatch;
        } else {
            diseaseState = AgentDiseaseState.SUSCEPTIBLE;
            diseaseTreeID = -1;
            transmissionLocation = null;
            transmissionTime = Double.NaN;
            transmissionPatch = -9999;
        }
    }
    
    /**
     * 
     * @param n
     */
    public void setNodeReference(GraphNode n) {
        node = n;
    }
    
    /**
     * 
     * @return
     */
    public GraphNode getNodeReference() {
        return node;
    }
    
    /**
     * 
     * @param p
     */
    public void setLocation(PointIn2D p) {
        if (p != null) {
            transmissionLocation = new PointIn2D(p);
        }
    }
    
    /**
     * 
     * @return
     */
    public PointIn2D getLocation() {
        return transmissionLocation; // TEMP
    }
    
    /**
     * 
     * @param i
     */
    public void setIndex(int i) {
        if (i >= 0) {
            nodeID = i;
        }
    }
    
    /**
     * 
     * @return
     */
    public int getIndex() {
        return diseaseTreeID;
    }
    
    public void setPlotColorID(int i) {
        // do nothing
    }
    
    public int getPlotColorID() {
        return diseaseTreeID;
    }
    
    /**
     * 
     * @return
     */
    public Agent getAgentReference() {
        return agent;
    }
    
    /**
     * 
     * @return
     */
    public boolean isInfectious() {
        if (diseaseState == AgentDiseaseState.INFECTIVE) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 
     * @return
     */
    public boolean isSusceptible() {
        if (diseaseState == AgentDiseaseState.SUSCEPTIBLE) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 
     * @return
     */
    public AgentDiseaseState getDiseaseState() {
        return diseaseState;
    }
    
    /**
     * 
     * @param treeID
     * @param infTime
     * @param loc
     * @param patchID
     */
    public void setToInfectious(int treeID, double infTime, PointIn2D loc, int patchID) {
        if (diseaseState == AgentDiseaseState.SUSCEPTIBLE) {
            diseaseState = AgentDiseaseState.INFECTIVE;
            diseaseTreeID = treeID;
            transmissionLocation = loc;
            transmissionTime = infTime;
            transmissionPatch = patchID;
        }
    }
    
    /**
     * 
     * @return
     */
    public double getInfectionTime() {
        return transmissionTime;
    }
    
    /**
     * 
     * @return
     */
    public int getTransmissionPatchID() {
        return transmissionPatch;
    }
    
    /**
     * 
     * @return
     */
    public int getTransmissionTreeID() {
        return diseaseTreeID;
    }
    
    /**
     * 
     * @return
     */
    public int getNodeID() {
        return nodeID;
    }
    
    /**
     * 
     * @return
     */
    public boolean startedAsInfective() {
        return startedAsInfective;
    }
    
    /**
     * 
     * @return
     */
    public static int getNumberOfInitialInfective() {
        return treeIDcounter;
    }
}
