/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents.graph.contact;

import java.util.*;
import com.eid.lib.agents.graph.*;
import com.eid.lib.gridindex.*;

/**
 * This class provides updating behavior for graphs that connect nodes within
 * some distance...
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class ContactGraphBehavior extends GraphBehaviorTemplate {
    private double maximumDistance;
    private boolean calculateMetrics;
    private GridIndex proxIndexGrid = null;
    // variables for calculating output
    private int totalInProximity; // just the number of edges?
    private int totalAgents; // just the number of nodes?
    // any meaning behind edges/nodes ?
    
    /**
     * 
     */
    public ContactGraphBehavior() {
        maximumDistance = 0.0; // or inf?
        calculateMetrics = false;
        proxIndexGrid = null;
    }
    
    /**
     * 
     * @param dist
     */
    public ContactGraphBehavior(double dist) {
        maximumDistance = Math.abs(dist);
        calculateMetrics = false;
        proxIndexGrid = null;
    }
    
    /**
     * 
     * @param dist
     * @param proxGrid
     */
    public ContactGraphBehavior(double dist, GridIndex proxGrid) {
        maximumDistance = Math.abs(dist);
        calculateMetrics = false;
        proxIndexGrid = proxGrid;
    }
    
    /**
     * 
     * @param proxGrid
     */
    public void setGridIndex(GridIndex proxGrid) {
        proxIndexGrid = proxGrid;
    }
    
    /**
     * NEEDS A BETTER RANGE SEARCH ALGORITHM...
     * This method creates edges from nodes to those within the minimum distance
     * of it.
     * @param g
     */
    public void updateGraph(Graph g, double eventTime) {
        if (g != null) {
            g.clearAllEdges();
            if (proxIndexGrid == null) {
                for (int n = 0; n < g.getNumberOfNodes(); n++) { // from node
                    for (int m = (n + 1); m < g.getNumberOfNodes(); m++) { // to node
                        if (g.getNode(n).getNodeData().getLocation().withinDistanceOf(g.getNode(m).getNodeData().getLocation(), maximumDistance)) {
                            g.addEdge(new GraphEdge(g.getNode(n), g.getNode(m), false, new ContactGraphEdgeData())); // add undirected edge
                            g.addEdge(new GraphEdge(g.getNode(m), g.getNode(n), false, new ContactGraphEdgeData()));
                        }
                    }
                }
            } else {
                ArrayList<SpatialAccessMethods> tmpList = null;
                ContactNodeCellObject tmpDat = null;
                ContactGraphNodeData fmDat = null, toDat = null;
                proxIndexGrid.clearAllObjects();
                for (int n = 0; n < g.getNumberOfNodes(); n++) {
                    proxIndexGrid.insert(new ContactNodeCellObject(g.getNode(n).getNodeData().getLocation(), maximumDistance, (ContactGraphNodeData) g.getNode(n).getNodeData()));
                }
                for (int n = 0; n < g.getNumberOfNodes(); n++) {
                    tmpList = proxIndexGrid.getObjectsContainingPoint(g.getNode(n).getNodeData().getLocation());
                    if (tmpList != null) {
                        toDat = (ContactGraphNodeData) g.getNode(n).getNodeData();
                        for (int k = 0; k < tmpList.size(); k++) {
                            tmpDat = (ContactNodeCellObject) tmpList.get(k);
                            fmDat = tmpDat.getNodeData();
                            if (fmDat != toDat) { // don't link to self
                                g.addEdge(new GraphEdge(fmDat.getNodeReference(), toDat.getNodeReference(), false, new ContactGraphEdgeData()));
                            }
                        }
                    }
                }
                // proxIndexGrid.display(); // FOR TESTING
            }
        }
        // g.displaySummary(); // FOR TESTING
        // System.out.println("proximity distance = " + maximumDistance);
    }
    
    /**
     * 
     * @param g
     */
    public void calculateMetrics(Graph g, double eventTime) {
        totalInProximity = g.getNumberOfEdges();
        totalAgents = g.getNumberOfNodes();
    }
    
    /**
     * 
     * @return
     */
    @Override public boolean performMetricCalculations() {
        return calculateMetrics;
    }
    
    /**
     * 
     * @param b
     */
    public void setPerformMetricCalculations(boolean b) {
        calculateMetrics = b;
    }
    
    /**
     * 
     * @return
     */
    public double getMaximumDistance() {
        return maximumDistance;
    }
    
    /**
     * 
     * @param dist
     */
    public void setMaximumDistance(double dist) {
        maximumDistance = Math.abs(dist);
    }
    
    /**
     * Returns the total contacts in the current graph, which is simply the
     * number of nodes.
     * @return
     */
    public int getTotalContacts() {
        return totalInProximity;
    }
    
    /**
     * Returns the total number of agents, which is simply the number of nodes
     * in the graph.
     * @return
     */
    public int getTotalAgents() {
        return totalAgents;
    }
    
    /**
     * Returns the average contacts, which is the average number of edges per 
     * node.
     * @return
     */
    public float getAverageContacts() {
        if (totalAgents > 0) {
            return ((float)totalInProximity)/((float)totalAgents);
        } else {
            return Float.NaN;
        }
    }
}
