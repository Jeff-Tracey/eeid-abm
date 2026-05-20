package com.eid.lib.agents.graph;

import java.util.*;
import java.io.*;

import com.eid.lib.geometricprimitives.*;
import com.eid.lib.stochastic.*;

/**
 * A super class for graphs and networks.
 *
 * Created: 24 Janurary 2008
 *
 * @author Jeff A. Tracey
 * Copyright 2008 Jeff A. Tracey, SigmaLogistic Consulting.
 * All rights reserved.
 */
public class Graph implements Serializable {
    /**  */
    private ArrayList<GraphNode> graphNodeList = null;
    /**  */
    private ArrayList<GraphEdge> graphEdgeList = null;
    /**  */
    private ArrayList<GraphBehaviorTemplate> graphBehList = null;
    
    /**
     *
     */
    public Graph() {
        graphNodeList = new ArrayList<GraphNode>();
        graphEdgeList = new ArrayList<GraphEdge>();
        graphBehList = new ArrayList<GraphBehaviorTemplate>();
    }
    
    /*--------------------------------------------------------------------------
    - getter methods
    --------------------------------------------------------------------------*/
    
    /**
     * Will return <code>null</code> if index is out-of-bounds.
     * @param i
     * @return node i.
     */
    public GraphNode getNode(int i) {
        GraphNode res = null;
        if ((i >= 0) && (i < graphNodeList.size())) {
            res = graphNodeList.get(i);
        }
        return res;
    }
    
    /**
     * Will return <code>null</code> if index is out-of-bounds.
     * @param i
     * @return the ith graph edge, or null if out-of-bounds.
     */
    public GraphEdge getEdge(int i) {
        GraphEdge res = null;
        if ((i >= 0) &&(i < graphEdgeList.size())) {
            res = graphEdgeList.get(i);
        }
        return res;
    }
    
    /**
     * 
     * @return the number of nodes in the graph.
     */
    public int getNumberOfNodes() {
        return graphNodeList.size();
    }
    
    /**
     * 
     * @return the number of edges in the graph.
     */
    public int getNumberOfEdges() {
        return graphEdgeList.size();
    }
    
    /**
     * 
     * @param i
     * @return the ith graph behavior
     */
    public GraphBehaviorTemplate getGraphBehavior(int i) {
        if ((i >= 0) && (i < graphBehList.size())) {
            return graphBehList.get(i);
        } else {
            return null;
        }
    }
    
    /*--------------------------------------------------------------------------
    - setter methods
    --------------------------------------------------------------------------*/
    
    /**
     * 
     * @param n
     */
    public void addNode(GraphNode n) {
        if (!graphNodeList.contains(n)) {
            graphNodeList.add(n);
        }
    }
    
    /**
     * 
     * @param e
     */
    public void addEdge(GraphEdge e) {
        if (!graphEdgeList.contains(e)) {
            graphEdgeList.add(e);
        }
        for (int i = 0; i < graphNodeList.size(); i++) {
            graphNodeList.get(i).insertEdge(e);
        }
    }
    
    
    /**
     * 
     * @param i
     */
    private void removeEdge(int i) {
        // must also remove it from the list for the nodes it is connected to
        if ((i >= 0) && (i < graphEdgeList.size())) {
            if (graphEdgeList.get(i) != null) {
                GraphEdge e = graphEdgeList.get(i);
                if (e.getFromNode() != null) {
                    e.getFromNode().removeEdge(e);
                }
                if (e.getToNode() != null) {
                    e.getToNode().removeEdge(e);
                }
            graphEdgeList.remove(e);
            }
        } // else, do nothing
    }
    
    
    /**
     * 
     */
    public void clearAllEdges() {
        while (graphEdgeList.size() > 0) {
            removeEdge(0);
        }
    }
    
    /**
     * 
     */
    public void clearGraphNodesAndEdges() {
        graphEdgeList.clear();
        graphNodeList.clear();
    }
    
    /**
     * 
     * @param b
     */
    public void addGraphBehavior(GraphBehaviorTemplate b) {
        if (b != null) {
            graphBehList.add(b);
        }
    }
    
    /**
     * Will return <code>null</code> if (x, y) is more than d away from any edge.
     * If there are several edges within d, it will return the nearest to (x, y).
     * If there are congruent edges, it will return the first encountered.
     * @param x
     * @param y
     * @param d
     * @return the nearest edge to (x, y) within distance d, or null if no such
     * edge exists.
     */
    /*
    public GraphEdge getEdgeAt(double x, double y, double d) {
        GraphEdge res = null;
        double minD = Double.POSITIVE_INFINITY;
        double tmpD = 0.0;
        for (int i = 0; i < graphEdgeList.size(); i++) {
            tmpD = graphEdgeList.get(i).getEdgeData().getDistanceToEdgeLine(x, y);
            if ((tmpD < minD) && (tmpD < d)) {
                minD = tmpD;
                res = graphEdgeList.get(i);
            }
        }
        return res;
    }
    */
    
    /**
     *  Will return <code>null</code> if coordinates do not fall within any node icon.
     *  
     * One potential issue is that more than one node icon may overlap, and in
     *     this case the point might fall in more than one icon.  I should modify
     *     this method to find the node with the icon center nearest to the
     *     point given as args, and return that one.
     */
    /*
    public GraphNode getNodeAt(double x, double y, double dist) {
        GraphNode res = null;
        for (int i = 0; i < graphNodeList.size(); i++) {
            if (graphNodeList.get(i).getNodeData().withinDistanceOfLocation(new PointIn2D(x, y), dist)) {
                res = graphNodeList.get(i);
                break;
            }
        }
        return res;
    }
    */
    
    /**
     * 
     * @param d
     * @param n
     * @return true if any nodes in the graph are within distance d of n.
     */
    /*
    public boolean nodeWithinDistanceOfNodesInGraph(double d, GraphNode n) {
        boolean res = false;
        for (int i = 0; i < graphNodeList.size(); i++) {
            if (graphNodeList.get(i).getNodeData().withinDistanceOfOtherNode(d, n.getNodeData())) {
                res = true;
                break;
            }
        }
        return res;
    }
    */
    
    /**
     * 
     * Note that this is an O(n^2) algorithm as currently implemented.
     * @param d
     */
    /*
    public void connectNodesWithinDistance(double d) {
        // remove all existing edges
        while (graphEdgeList.size() > 0) {
            removeEdge(0);
        }
        GraphEdge tmpEdge = null;
        for (int n = 0; n < graphNodeList.size(); n++) {
            for (int m = (n+1); m < graphNodeList.size(); m++) {
                if (graphNodeList.get(n).getNodeData().withinDistanceOfOtherNode(d, graphNodeList.get(m).nodeData)) {
                    // may want to use setWeight(double wt) and distanceToOtherIcon(GraphNodeIcon n) to set f(distance) for edge
                    tmpEdge = new GraphEdge(graphNodeList.get(n), graphNodeList.get(m), false, new GraphEdgeDataBase()); 
                    addEdge(tmpEdge); // add undirected edge
                    tmpEdge = new GraphEdge(graphNodeList.get(m), graphNodeList.get(n), false, new GraphEdgeDataBase()); 
                }
            }
        }
    }
     */
    
    /**
     * Reconnect node with index nodeIndex to other nodes within distance d
     * Note that this is an O(n^2) algorithm as currently implemented.
     * @param d
     * @param nodeIndex
     */
    /*
    public void connectNodesWithinDistance(double d, int nodeIndex) {
        // remove all existing edges
        if ((nodeIndex >= 0) && (nodeIndex < graphNodeList.size())) {
            int nEdges = graphEdgeList.size();
            for (int i = (nEdges - 1); i >= 0; i--) {
                if ((graphEdgeList.get(i).getFromNode() == graphNodeList.get(nodeIndex)) || (graphEdgeList.get(i).getToNode() == graphNodeList.get(nodeIndex))) {
                    graphEdgeList.remove(i);
                }
                removeEdge(0);
            }
            GraphEdge tmpEdge = null;
            for (int m = (nodeIndex + 1); m < graphNodeList.size(); m++) {
                if (graphNodeList.get(nodeIndex).getNodeData().withinDistanceOfOtherNode(d, graphNodeList.get(m).nodeData)) {
                    // may want to use setWeight(double wt) and distanceToOtherIcon(GraphNodeIcon n) to set f(distance) for edge
                    tmpEdge = new GraphEdge(graphNodeList.get(nodeIndex), graphNodeList.get(m), false, new GraphEdgeDataBase());
                    addEdge(tmpEdge); // add undirected edge
                    tmpEdge = new GraphEdge(graphNodeList.get(m), graphNodeList.get(nodeIndex), false, new GraphEdgeDataBase());
                }
            }
        }
    }
     */
    
    /**
     * 
     * @param p
     * @param cm
     */
    /*
    public void testSetInitialSpreadState(double p, ColorMap cm) {
        double tmpRnd = 0.0;
        int spreadState = 1;
        for (int n = 0; n < graphNodeList.size(); n++) {
            tmpRnd = RandomNumberGenerator.drawDouble();
            if (tmpRnd <= p) {
                graphNodeList.get(n).getNodeData().setIndex(spreadState);
                spreadState++;
            }
        }
    }
     */
    
    /**
     * THIS IS A SOMEWHAT SPECIFIC METHODS WHICH MAY BE REMOVED...
     * @param d threshold distance
     * @param p
     * @param cm
     */
    /*
    public void testSpreadContact(double d, double p, ColorMap cm) {
        double tmpRnd = 0.0;
        GraphEdge tmpEdge = null;
        VertexListIn2D tmpEdgeGeom = null;
        int spreadState;
        for (int n = 0; n < graphNodeList.size(); n++) {
            // iff n has the spreadState
            if (graphNodeList.get(n).getNodeData().getIndex() > 0) {
                spreadState = graphNodeList.get(n).getNodeData().getIndex();
                for (int m = 0; m < graphNodeList.size(); m++) {
                    if ((n != m) && (graphNodeList.get(m).getNodeData().getIndex() <= 0)) {
                        if (graphNodeList.get(n).getNodeData().distanceToOtherNode(graphNodeList.get(m).nodeData) <= d) {
                            tmpRnd = RandomNumberGenerator.drawDouble();
                            if (tmpRnd <= p) {
                                // change state of m
                                graphNodeList.get(m).getNodeData().setIndex(spreadState);
                                // add edge from n to m
                                tmpEdgeGeom = new VertexListIn2D();
                                tmpEdgeGeom.addVertex(graphNodeList.get(n).getNodeData().getLocation());
                                tmpEdgeGeom.addVertex(graphNodeList.get(m).getNodeData().getLocation());
                                tmpEdge = new GraphEdge(graphNodeList.get(n), graphNodeList.get(m), true, new GraphEdgeDataBase());
                                tmpEdge.getEdgeData().setEdgeGeometry(tmpEdgeGeom);
                                addEdge(tmpEdge); //
                            }
                        }
                    }
                }

            }
        }
    }
     */
    
}
