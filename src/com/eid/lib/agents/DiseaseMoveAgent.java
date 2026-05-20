package com.eid.lib.agents;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;

import com.eid.lib.geometricprimitives.*;
import com.eid.lib.agents.graph.*;
import com.eid.lib.agents.graph.disease.*;
import com.eid.lib.agents.graph.contact.*;
import com.eid.lib.simulation.*;

/**
 * THIS CLASS REALLY COUNTS ON A ProximityGraphNodeData OBJECT AT INDEX 0...
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class DiseaseMoveAgent extends Agent {
    //
    private static final int PROX_NODE_INDEX = 0;
    private static final int DIS_NODE_INDEX = 1;
    //
    private boolean isAlive;
    
    /**
     * 
     * @param p the initial location in the move path.
     */
    public DiseaseMoveAgent(PointIn2D p, boolean startInfected, int initPatchID) {
        super(p);
        // other stuff for this class
        isAlive = true;
        ContactGraphNodeData tmpNd = new ContactGraphNodeData(this, p);
        super.addGraphNodeData(tmpNd);
        super.addGraphNodeData(new DiseaseGraphNodeData(this, startInfected, initPatchID));
    }
    
    public DiseaseMoveAgent(PointIn2D p, SimulationEngineBehavior s, boolean startInfected, int initPatchID) {
        super(p, s);
        // other stuff for this class
        isAlive = true;
        ContactGraphNodeData tmpNd = new ContactGraphNodeData(this, p);
        super.addGraphNodeData(tmpNd);
        super.addGraphNodeData(new DiseaseGraphNodeData(this, startInfected, initPatchID));
    }
    
    /**
     * 
     * @param p
     */
    @Override public void setLocation(PointIn2D p) {
        // CHECK FOR p != null ????
        super.setLocation(p);
        if ((getNodeData(PROX_NODE_INDEX) != null) && (getNodeData(PROX_NODE_INDEX) instanceof ContactGraphNodeData)) {
            getNodeData(PROX_NODE_INDEX).setLocation(p);
        }
    }
    
    /**
     * Sets the agents state to not alive.
     */
    public void smiteAgent() {
        isAlive = false;
    }
    
    /**
     * 
     * @return true if agent is alive, false otherwise.
     */
    public boolean agentIsAlive() {
        return isAlive;
    }
    
    /**
     * 
     * @return
     */
    public boolean agentIsSusceptible() {
        if ((this.getNodeData(DIS_NODE_INDEX) != null) && (this.getNodeData(DIS_NODE_INDEX) instanceof DiseaseGraphNodeData)) {
            DiseaseGraphNodeData tmp = (DiseaseGraphNodeData)this.getNodeData(DIS_NODE_INDEX);
            return tmp.isSusceptible();
        } else {
            return true;
        }
    }
    
    /**
     * 
     * @return
     */
    public boolean agentIsInfectious() {
        if ((this.getNodeData(DIS_NODE_INDEX) != null) && (this.getNodeData(DIS_NODE_INDEX) instanceof DiseaseGraphNodeData)) {
            DiseaseGraphNodeData tmp = (DiseaseGraphNodeData)this.getNodeData(DIS_NODE_INDEX);
            return tmp.isInfectious();
        } else {
            return false;
        }
    }
    
    public DiseaseGraphNodeData getDiseaseNodeData() {
        DiseaseGraphNodeData res = null;
        if (super.getNodeData(DIS_NODE_INDEX) != null) {
            res = (DiseaseGraphNodeData) super.getNodeData(DIS_NODE_INDEX);
        }
        return res;
    }
    
    public ContactGraphNodeData getProximityNodeData() {
        ContactGraphNodeData res = null;
        if (super.getNodeData(PROX_NODE_INDEX) != null) {
            res = (ContactGraphNodeData) super.getNodeData(PROX_NODE_INDEX);
        }
        return res;
    }
    
    /**
     * 
     * @param otherAgent
     * @param dist
     * @return true if this agent is within dist of other agent (provided dist
     * is less than or equal to the distance used to construct the proximity
     * network).
     */
    public boolean withinDistance(DiseaseMoveAgent otherAgent, double dist) {
        boolean res = false;
        if (agentIsSusceptible()) {
            if (super.getNodeData(PROX_NODE_INDEX) != null) {
                ContactGraphNodeData focalProx = (ContactGraphNodeData)super.getNodeData(PROX_NODE_INDEX);
                ContactGraphEdgeData tmpProxE = null;
                ContactGraphNodeData tmpProxN = null;
                DiseaseMoveAgent tmpAgent = null;
                for (int a = 0; a < focalProx.getNodeReference().getNodeDegreeOut(); a++) {
                    try {
                        tmpProxE = (ContactGraphEdgeData)focalProx.getNodeReference().getEdgeOut(a).getEdgeData();
                        tmpProxN = (ContactGraphNodeData)tmpProxE.getEdgeReference().getToNode().getNodeData();
                        tmpAgent = (DiseaseMoveAgent)tmpProxN.getAgentReference();
                        if ((tmpAgent == otherAgent) && (tmpProxE.getDistance() <= dist)) {
                            res = true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        // just don't do anything
                        tmpProxE = null;
                        tmpProxN = null;
                        tmpAgent = null;
                    }
                }
            }
        }
        return res;
    }
    
    /**
     * 
     * @param dist
     * @return
     */
    public int getNeighborsWithinDistance(double dist) {
        int res = 0;
        ContactGraphNodeData focalProx = this.getProximityNodeData();
        if (focalProx != null) {
            ContactGraphEdgeData tmpProxE = null;
            for (int a = 0; a < focalProx.getNodeReference().getNodeDegreeOut(); a++) {
                try {
                    tmpProxE = (ContactGraphEdgeData) focalProx.getNodeReference().getEdgeOut(a).getEdgeData();
                    if (tmpProxE.getDistance() <= dist) {
                        res++;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // just don't do anything
                    tmpProxE = null;
                }
            }
        }
        return res;
    }
    
    /**
     * COULD MAKE THIS MORE GENERAL TO GET NUMBER IN ANY STATE...
     * @param dist
     * @return
     */
    public int getNumberInfectiveWithinDistance(double dist) {
        int res = 0;
        ContactGraphNodeData focalProx = this.getProximityNodeData();
        if (focalProx != null) {
            ContactGraphEdgeData tmpProxE = null;
            ContactGraphNodeData tmpProxN = null;
            DiseaseMoveAgent tmpAgent = null;
            for (int a = 0; a < focalProx.getNodeReference().getNodeDegreeOut(); a++) {
                try {
                    tmpProxE = (ContactGraphEdgeData) focalProx.getNodeReference().getEdgeOut(a).getEdgeData();
                    tmpProxN = (ContactGraphNodeData) tmpProxE.getEdgeReference().getToNode().getNodeData();
                    tmpAgent = (DiseaseMoveAgent) tmpProxN.getAgentReference();
                    if ((tmpAgent.agentIsInfectious()) && (tmpProxE.getDistance() <= dist)) {
                        res++;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // just don't do anything
                    tmpProxE = null;
                    tmpProxN = null;
                    tmpAgent = null;
                }
            }
        }
        return res;
    }
    
    /**
     * 
     * @param n
     * @param dist
     * @return
     */
    public DiseaseGraphNodeData getDiseaseNodeForNthInfectiveWithinDistance(int n, double dist) {
        DiseaseGraphNodeData res = null;
        ContactGraphNodeData focalProx = this.getProximityNodeData();
        if (focalProx != null) {
            ContactGraphEdgeData tmpProxE = null;
            ContactGraphNodeData tmpProxN = null;
            DiseaseMoveAgent tmpAgent = null;
            int tmpCount = 0;
            for (int a = 0; a < focalProx.getNodeReference().getNodeDegreeOut(); a++) {
                try {
                    tmpProxE = (ContactGraphEdgeData) focalProx.getNodeReference().getEdgeOut(a).getEdgeData();
                    tmpProxN = (ContactGraphNodeData) tmpProxE.getEdgeReference().getToNode().getNodeData();
                    tmpAgent = (DiseaseMoveAgent) tmpProxN.getAgentReference();
                    if ((tmpAgent.agentIsInfectious()) && (tmpProxE.getDistance() <= dist)) {
                        if (tmpCount == n) {
                            res = tmpAgent.getDiseaseNodeData();
                        }
                        tmpCount++;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // just don't do anything
                    tmpProxE = null;
                    tmpProxN = null;
                    tmpAgent = null;
                }
            }
        }
        return res;
    }
    
}
