/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents.graph;

import com.eid.lib.agents.*;
import com.eid.lib.geometricprimitives.*;

/**
 *
 * @author  Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public interface GraphNodeData {
    public void setNodeReference(GraphNode n);
    public GraphNode getNodeReference();
    public void setLocation(PointIn2D p);
    public PointIn2D getLocation();
    public void setIndex(int i);
    public int getIndex();
    public void setPlotColorID(int i);
    public int getPlotColorID();
}
