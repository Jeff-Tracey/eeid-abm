/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.simulation;

import com.eid.lib.grid.*;
import com.eid.lib.vectormodels.*;
import com.eid.lib.agents.graph.*;

/**
 * An interface for simulation engines.  A concrete class that implements this
 * interface should provide access to any simulation data that may need to be
 * changed, written to output, or otherwise used elsewhere.  It will have some
 * combination of raster data, vector model data, graphs, and agent objects.
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public interface SimulationEngineBehavior {
    /***************************************************************************
    * call simulation behavior
    ***************************************************************************/
    /**
     * update state of simulation
     */
    public void advanceSimulationTime();
    
    /**
     * reset for new realization
     */
    public void reinitializeForNextRealization();
    
    /**
     * reset for a new simulation
     */
    public void reinitializeForNextSimulation();
    
    /**
     * is done with current realization
     * @return true if current realization is complete.
     */
    public boolean hasCompletedRealization();
    
    /**
     * is done with last realization
     * @return true if simulation is complete.
     */
    public boolean hasCompletedSimulation();
    
    /***************************************************************************
    * subject methods for observer pattern
    ***************************************************************************/
    
    /**
     * 
     * @param seo
     */
    public void attachObserver(SimulationEngineObserver seo);
    
    /**
     * 
     * @param seo
     */
    public void detachObserver(SimulationEngineObserver seo);
    
    /**
     * 
     * @param ev
     */
    public void notify(SimulationEngineObserverEvent ev);
    
    /***************************************************************************
    * set simulation control parameters
    ***************************************************************************/
    /**
     * 
     * @param t
     */
    public void setSimTimeInterval(double t);
    
    /**
     * 
     * @param t
     */
    public void setSimTimeDuration(double t);
    
    /**
     * 
     * @param t
     */
    public void setdiscreteTimeStep(double t);
    
    /**
     * 
     * @param n
     */
    public void setNumberOfRealizations(int n);
    
    /***************************************************************************
    * methods to check status
    ***************************************************************************/
    
    /**
     * 
     * @return current realization number.
     */
    public int getCurrentRealizationNumber();
    
    /**
     * 
     * @return total number of realizations.
     */
    public int getNumberOfRealizations();
    
    /**
     * 
     * @return current time in simulation.
     */
    public double getCurrentTime();
    
    /**
     * 
     * @return the duration of an update interval in the simulation.
     */
    public double getTimeDuration();
    
    /**
     * 
     * @return a string with a verbal description of the status of the simulation
     */
    public String getStatusMessage();
    
    /***************************************************************************
    * calculate/write output
    ***************************************************************************/
    
    /**
     * 
     */
    public void performEndOfTrialCalculations();
    
    /**
     * 
     */
    public void performEndOfSimulationCalculations();
    
    /**
     * 
     */
    public void writeOutput();
    
    /***************************************************************************
    * set and retreive simulation data
    ***************************************************************************/
    // Graph: set, get, check for existence
    
    public void addGraph(Graph g, int i);
    public Graph getGraph(int i);
    public boolean isValidGraphIndex(int i);
    
    // FloatRaster: set, get, check for existence
    
    public void addFloatRaster(FloatRaster f, int i);
    public FloatRaster getFloatRaster(int i);
    public boolean isValidFloatRasterIndex(int i);
    
    // IntegerRaster: set, get, check for existence
    
    public void addIntegerRaster(IntegerRaster f, int i);
    public IntegerRaster getIntegerRaster(int i);
    public boolean isValidIntegerRasterIndex(int i);
    
    // PointLayerIn2D: set, get, check for existence
    
    public void addPointLayerIn2D(PointLayerIn2D p, int i);
    public PointLayerIn2D getPointLayerIn2D(int i);
    public boolean isValidPointLayerIn2DIndex(int i);
    
    // PolyLineLayerIn2D: set, get, check for existence
    
    public void addPolyLineLayerIn2D(PolyLineLayerIn2D p, int i);
    public PolyLineLayerIn2D getPolyLineLayerIn2D(int i);
    public boolean isValidPolyLineLayerIn2DIndex(int i);
    
}
