/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents.graph;

/**
 *
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public abstract class GraphBehaviorTemplate {
    
    
    public final void executeBehavior(Graph g, double eventTime) {
        // 1.  update the graph
        updateGraph(g, eventTime);
        // 2. provide the option to calculate graph metrics
        if (performMetricCalculations()) {
            calculateMetrics(g, eventTime);
        }
    }
    // there must be at leat one step in the behavior
    public abstract void updateGraph(Graph g, double eventTime);
    public abstract void calculateMetrics(Graph g, double eventTime);
    
    public boolean performMetricCalculations() {
        return false;
    }
    
    //  what about calculating a graph metric?
}
