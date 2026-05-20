/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents.graph.contact;

import com.eid.lib.geometricprimitives.*;
import com.eid.lib.agents.*;
import com.eid.lib.agents.graph.*;

/**
 * MERGE WITH NODE
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class ContactGraphNodeData implements GraphNodeData {
    // static variables
    private static int nodeIDcounter = 0;
    //
    private GraphNode node;
    private Agent agent;
    private PointIn2D point;
    private int nodeID;
    
    /**
     * 
     */
    public ContactGraphNodeData(Agent a) {
        node = null;
        agent = a;
        point = null;
        nodeID = nodeIDcounter;
        nodeIDcounter++;
    }
    
    /**
     * 
     * @param n
     */
    public ContactGraphNodeData(Agent a, GraphNode n) {
        node = n;
        agent = a;
        point = null;
        nodeID = nodeIDcounter;
        nodeIDcounter++;
    }
    
    /**
     * 
     * @param p
     */
    public ContactGraphNodeData(Agent a, PointIn2D p) {
        node = null;
        agent = a;
        point = p;
        nodeID = nodeIDcounter;
        nodeIDcounter++;
    }
    
    /**
     * 
     * @param n
     * @param p
     */
    public ContactGraphNodeData(Agent a, GraphNode n, PointIn2D p) {
        node = n;
        agent = a;
        point = p;
        nodeID = nodeIDcounter;
        nodeIDcounter++;
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
        point = p;
        // UPDATE EDGE DATA?
    }
    
    /**
     * 
     * @return
     */
    public PointIn2D getLocation() {
        return point;
    }
    
    /**
     * 
     * @param i
     */
    public void setIndex(int i) {
        if (i >= 0) {
            nodeID = i;
        } else {
            nodeID = -1;
        }
    }
    
    /**
     * 
     * @return the degree out because if it is displayed we may color accordingly.
     */
    public int getIndex() {
        return nodeID;
    }
    
    /**
     * 
     * @param i
     */
    public void setPlotColorID(int i) {
        // do nothing
    }
    
    /**
     * 
     * @return
     */
    public int getPlotColorID() {
        if (node != null) {
            return node.getNodeDegreeOut();
        } else {
            return -1;
        }
    }
    
    /**
     * 
     * @return
     */
    public Agent getAgentReference() {
        return agent;
    }
}
