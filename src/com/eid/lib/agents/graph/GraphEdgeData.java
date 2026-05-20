/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents.graph;

import com.eid.lib.geometricprimitives.*;

/**
 * Interface for graph edge data.
 * @author  Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public interface GraphEdgeData {
    public void setEdgeReference(GraphEdge e);
    public GraphEdge getEdgeReference();
    public void setEdgeGeometry(VertexListIn2D vl);
    public VertexListIn2D getEdgeGeometry();
    public void setIndex(int i);
    public int getIndex();
    public void setPlotColorID(int i);
    public int getPlotColorID();
}
