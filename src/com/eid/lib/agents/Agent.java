/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents;

import java.util.*;

import com.eid.lib.agents.graph.*;
import com.eid.lib.geometricprimitives.*;
import com.eid.lib.simulation.*;

/**
 * Parent class for agents.  Provides a list of network nodes belonging to the
 * agent, a list of agent behaviors, access to these lists, an agent location,
 * and a reference to the simulation engine.  Subclasses should provide states,
 * possible models for transition between different behaviors, and specifics
 * about the nodes belonging to the agent.
 *
 * 
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class Agent {
    private static int agentIDcounter = 0;
    private int agentID;
    private SimulationEngineBehavior simRef;
    private PointIn2D location = null; /** spatial coordinates of the node */
    private ArrayList<MoveBehaviorRuleTemplate> agentBehaviorSet = null; // array of movement behaviors...?
    private ArrayList<GraphNodeData> agentNodes = null;
    private int priorityIndex;
    // state
    // objectives?
    // ID and static counter...
    
    /**
     * 
     */
    public Agent() {
        agentBehaviorSet = new ArrayList<MoveBehaviorRuleTemplate>();
        agentNodes = new ArrayList<GraphNodeData>();
        location = new PointIn2D();
        priorityIndex = 0; // used so set priority for events
        agentID = agentIDcounter;
        agentIDcounter++;
    }
    
    /**
     * 
     * @param p
     */
    Agent(PointIn2D p) {
        agentBehaviorSet = new ArrayList<MoveBehaviorRuleTemplate>();
        location = p;
        priorityIndex = 0; // used so set priority for events
        agentNodes = new ArrayList<GraphNodeData>();
        agentID = agentIDcounter;
        agentIDcounter++;
    }
    
    /**
     * 
     * @param p
     */
    Agent(PointIn2D p, SimulationEngineBehavior s) {
        agentBehaviorSet = new ArrayList<MoveBehaviorRuleTemplate>();
        simRef = s;
        location = p;
        priorityIndex = 0; // used so set priority for events
        agentNodes = new ArrayList<GraphNodeData>();
        agentID = agentIDcounter;
        agentIDcounter++;
    }
    
    /**
     * 
     * @return
     */
    public int getAgentID() {
        return agentID;
    }
    
    /**
     * 
     * @param p
     */
    public void setLocation(PointIn2D p) {
        location = p;
    }
    
    /**
     * 
     * @return
     */
    public PointIn2D getLocation() {
        return location;
    }
    
    /**
     * THIS WILL BE CHANGED...
     */
    public void setPriority(int i) {
        priorityIndex = i;
    }
    
    /**
     * THIS WILL BE CHANGED...
     * @return priority
     */
    public int getPriority() {
        return priorityIndex;
    }
    
    /**
     * 
     * @return
     */
    public int getNumberOfBehaviors() {
        return agentBehaviorSet.size();
    }
    
    /**
     * 
     * @param abt
     */
    public void addAgentBehavior(MoveBehaviorRuleTemplate abt) {
        if (abt != null) {
            agentBehaviorSet.add(abt);
        }
    }
    
    /**
     * 
     * @param i index
     * @param abt the agent behavior to be set at index i.  If i is out-of-bounds
     * or abt is null, nothing is done.
     */
    public void setAgentBehavior(int i, MoveBehaviorRuleTemplate abt) {
        if ((i >= 0) && (i < agentBehaviorSet.size()) && (abt != null)) {
            agentBehaviorSet.set(i, abt);
        }
    }
    
    /**
     * 
     * @param i
     * @return
     */
    public MoveBehaviorRuleTemplate getAgentBehavior(int i) {
        if ((i >= 0) && (i < agentBehaviorSet.size())) {
            return agentBehaviorSet.get(i);
        } else {
            return null;
        }
    }
    
    /**
     * 
     * @param nd
     */
    public void addGraphNodeData(GraphNodeData nd) {
        if ((nd != null) && (agentNodes != null)) {
            agentNodes.add(nd);
        }
    }
    
    /**
     * 
     * @param i
     * @return
     */
    public GraphNodeData getNodeData(int i) {
        GraphNodeData res = null;
        if ((i >= 0) && (i < agentNodes.size()) && (agentNodes != null)) {
            res = agentNodes.get(i);
        }
        return res;
    }
    
    /**
     * 
     * @return
     */
    public int getNumberOfGraphNodes() {
        if (agentNodes != null) {
            return agentNodes.size();
        } else {
            return 0;
        }
    }
    
    /**
     * HANDLE THE CASE WHERE i >= agentNodes.size
     * @param i
     * @param nd
     */
    public void setNodeData(int i, GraphNodeData nd) {
        if ((i >= 0) && (i < agentNodes.size()) && (agentNodes != null)) {
            agentNodes.set(i, nd);
        }
    }
    
    /**
     * 
     * @param i
     */
    public void removeNodeData(int i) {
        if ((i >= 0) && (i < agentNodes.size()) && (agentNodes != null)) {
            agentNodes.remove(i);
        }
    }
    
    /**
     * 
     */
    public void clearAllNodeData() {
        if (agentNodes != null) {
            agentNodes.clear();
        }
    }
    
    /**
     * 
     * @param s
     */
    public void setSimulationReference(SimulationEngineBehavior s) {
        simRef = s;
    }
    
    /**
     * 
     * @param s
     */
    public SimulationEngineBehavior getSimulationReference() {
        return simRef;
    }
}
