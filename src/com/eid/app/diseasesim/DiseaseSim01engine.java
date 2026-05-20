/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.app.diseasesim;

import com.eid.lib.agents.DiseaseMoveAgent;
import java.util.*;
import java.io.*;
import java.nio.*;

import com.eid.lib.agents.*;
import com.eid.lib.agents.movement.*;
import com.eid.lib.geometricprimitives.*;
import com.eid.lib.vectormodels.*;
import com.eid.lib.gridindex.*;
import com.eid.lib.agents.graph.*;
import com.eid.lib.agents.graph.disease.*;
import com.eid.lib.agents.graph.contact.*;
import com.eid.lib.grid.*;
import com.eid.lib.simulation.*;
import com.eid.lib.sorting.*;
import com.eid.lib.stochastic.*;

/**
 * THIS IS A SIMULATION ENGINE TO SUPPORT DISEASE SIMULATIONS THAT YIELD
 * OUTPUT FOR FURTHER ANALYSIS/STUDY.
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
class DiseaseSim01engine implements SimulationEngineBehavior {
    // things to control time
    private double maxTime = 0.0;
    private double currentTime = 0.0;
    private double discreteTimeStep = 0.0; /** this is the discrete time step for the simulation */
    private int timeStepsPerRealization = 0;
    private int timeStepIndex;
    // things to control times at which time series info is updated
    private double timeSeriesUpdateInterval = 0.5;
    private double nextTimeSeriesUpdateTime = 0.0;
    private int timeSeriesUpdateIndex = 0;
    private int timeSeriesUpdatesPerRealization;
    private int localSampleIndex = 0;
    private final int localSamplesPerTrial = 50;
    // things to control realizations
    private int numberRealizations = 0;
    private int currentRealization = -1;
    private boolean realizationComplete;
    //
    private int updateLandCoverInterval = 20;
    //
    private String simStatusMessage = "";
    private String simOutputFilenameBase = "./DiseaseSim";
    //
    private ArrayList<SimulationEngineObserver> observerList = null;
    //
    private int[] randOrder = null;
    private ArrayList<DiseaseMoveAgent> agentList = null;
    // simulation data:
    
    // settings for land cover
    private int rasterNumRows = 500;
    private int rasterNumCols = 500;
    private double rasterCellSize = 100;
    private int[] landcovTypes = {1, 2}; // 1 = non-habitat, 2 = habitat
    private int[] coreTypes = {2}; // 1 = non-habitat, 2 = habitat
    private int[] fixTypes = {1}; // 1 = non-habitat, 2 = habitat
    private double[] landcoverProps = {0.5, 0.5};
    private double landcoverH = 0.01;
    // settings for movement
    private int moveBehaviorNum = 0;
    private double[] lcovWts = {0.0125, 1.0}; // relative pref for non-hab, hab
    private double angleConcentration = 1.0;
    private double logisticLocParam = 250.0;
    private double logisticScaleParam = 0.1;
    private double proxNetworkDistance = 1000.0; // must set up to assign
    // settings for disease
    private double disTransProb = 0.05;
    private double disTransDistMax = 50.0;
    private double prInitInfect = 0.05;
    // rasters and graphs
    private BoxIn2D landscapeMBB = null; // calc from elev and landcover
    private Graph[] simGraphs = null;              // need 2: proximity, disease
    private FloatRaster[] fltRast = null;          // need 6: usage, allContacts, siContacts, propHab200m, propHab1000m, distToHab
    private IntegerRaster[] intRast = null;        // need 2: land cover, cores
    private PointLayerIn2D[] pointLayers = null;   // need 1: initial locations, ?
    private PolyLineLayerIn2D[] lineLayers = null; // need 2: roads, move paths
    private ContactGraphBehavior distGraphBeh = null;
    private DiseaseGraphBehavior diseaseGraphBeh = null;
    //
    private CategoryList patchCats = null;
    private double[] patchAreas = null;
    private boolean[] patchTransmissions = null;
    private double propNumPatchesWithTransmissions = 0.0;
    private double propAreaPatchesWithTransmissions = 0.0;
    //
    private static final int PROXIMITY_GRAPH = 0;
    private static final int DISEASE_GRAPH = 1;
    //
    private static final int USAGE_RAST = 0;
    private static final int CONTACT_ALL_RAST = 1;
    private static final int CONTACT_SI_RAST = 2;
    private static final int PROP_200M_RAST = 3;
    private static final int PROP_1000M_RAST = 4;
    private static final int DIST_RAST = 5;
    // 
    private static final int LANDCOV_RAST = 0;
    private static final int CORE_RAST = 1;
    private static final int PATCH_RAST = 2;
    
    
    // output arrays
    private float[] times = null;
    private float[][] propInfectedTimeSeries = null;
    private float[][] simulationTimeSeries = null;
    private float[][] trialPatchStats = null;
    private float[] simulationPatchStats = null;
    private float[][] localResponseSamples = null;
    private float PropSimsWithTransmissions = 0.0f;
    private boolean finalOutputCalculated = false;
    // movement data
    private int numberInitialLocations = 0;
    private double agentDensity = 0.0;
    private boolean useAgentDensity = false;
    private VertexListIn2D initialLocs = null;
    
    /**
     *
     * @param nTrails
     * @param timeStep
     * @param maxTime
     */
    DiseaseSim01engine(int nTrials, double timeStep, double maxTime) {
        // set simulation control varaiables
        setdiscreteTimeStep(timeStep);
        setSimTimeDuration(maxTime);
        setNumberOfRealizations(nTrials);
        timeStepsPerRealization = (int)Math.ceil(maxTime/discreteTimeStep);
        timeSeriesUpdatesPerRealization = (int)Math.floor(maxTime/timeSeriesUpdateInterval);
        //
        observerList = new ArrayList<SimulationEngineObserver>();
        // set up data arrays
        agentList = new ArrayList<DiseaseMoveAgent>();
        simGraphs = new Graph[2];              // need 2: move, disease
        fltRast = new FloatRaster[6];          // need 6: usage, allContacts, siContacts, propHab200m, propHab1000m, distToHab
        intRast = new IntegerRaster[3];        // need 2: land cover, cores, patchID
        pointLayers = new PointLayerIn2D[1];   // need 1: initial locations, ?
        lineLayers = new PolyLineLayerIn2D[1]; // need 1: move paths
        // do other constructor stuff
        initialLocs = new VertexListIn2D();
        simGraphs[PROXIMITY_GRAPH] = new Graph();
        distGraphBeh = new ContactGraphBehavior(proxNetworkDistance);
        distGraphBeh.setPerformMetricCalculations(true);
        simGraphs[PROXIMITY_GRAPH].addGraphBehavior(distGraphBeh);
        // DISEASE GRAPH SETUP...
        simGraphs[DISEASE_GRAPH] = new Graph();
        diseaseGraphBeh = new DiseaseGraphBehavior(disTransProb, disTransDistMax);
        diseaseGraphBeh.setPerformMetricCalculations(true);
        simGraphs[DISEASE_GRAPH].addGraphBehavior(diseaseGraphBeh);
        
        //
        if (timeSeriesUpdatesPerRealization > 0) {
            times = new float[timeSeriesUpdatesPerRealization];
            propInfectedTimeSeries = new float[timeSeriesUpdatesPerRealization][numberRealizations];
            simulationTimeSeries = new float[timeSeriesUpdatesPerRealization][8];
            //
            trialPatchStats = new float[10][numberRealizations];
            simulationPatchStats = new float[70]; // must increase size; to 60?
        } else {
            System.err.println("There are no time steps per realziation!");
            System.exit(1);
        }
    }
    
    /**
     * Runs the specified number of trials for the specified duration.  Once
     * this methods returns, the results are available for output to files.
     */
    void simulate() {
        // 
        for (currentRealization = 0; currentRealization < numberRealizations; currentRealization++) {
            // set up for new simulation/realization
            if (currentRealization == 0) {
                reinitializeForNextSimulation();
                notify(SimulationEngineObserverEvent.SIMULATION_START);
            } else {
                reinitializeForNextRealization();
            }
            notify(SimulationEngineObserverEvent.REALIZATION_START);
            // run simulation for time duration
            for (timeStepIndex = 0; timeStepIndex < timeStepsPerRealization; timeStepIndex++) {
                notify(SimulationEngineObserverEvent.ADVANCE_TIME_START);
                advanceSimulationTime();
                performEndOfTimeStepCalculations();
                realizationComplete = true;
                notify(SimulationEngineObserverEvent.ADVANCE_TIME_FINISH);
            }
            // end of trial output calculations
            performEndOfTrialCalculations();
            notify(SimulationEngineObserverEvent.REALIZATION_FINISH);
        }
        notify(SimulationEngineObserverEvent.SIMULATION_FINISH);
        // end of simulation output calculations
        performEndOfSimulationCalculations();
        notify(SimulationEngineObserverEvent.OUTPUTS_READY);
    }
    
    /**
     * 
     */
    private void createLandCover() {
        simStatusMessage = "Creating landscape...";
        FloatRaster fr1 = FloatRasterFunctions.fractalTerrain01(landcoverH, 1.0, rasterNumRows, rasterNumCols, 0.0, 0.0, rasterCellSize, Float.NaN);
        intRast[LANDCOV_RAST] = FloatRasterFunctions.createCategoricalMap(fr1, landcoverProps, landcovTypes);
        createCoreAreas(coreTypes);
        computeLandscapeMetrics();
    }
    
    /**
     * 
     * @param coreTypes
     * @param fixTypes
     * @param cellNeighb
     */
    private void createCoreAreas(int[] coreTypes) {
        simStatusMessage = "Creating core areas...";
        // just use cores = habitat
        intRast[CORE_RAST] = intRast[LANDCOV_RAST];
        intRast[CORE_RAST] = new IntegerRaster(intRast[LANDCOV_RAST].getNumberOfRows(), 
                intRast[LANDCOV_RAST].getNumberOfCols(), 
                intRast[LANDCOV_RAST].getXmin(), 
                intRast[LANDCOV_RAST].getYmin(), 
                intRast[LANDCOV_RAST].getCellSize(), 
                intRast[LANDCOV_RAST].getNoDataValue());
        intRast[CORE_RAST].setAllCellsToZero(true);
        for (int r = 0; r < intRast[CORE_RAST].getNumberOfRows(); r++) {
            for (int c = 0; c < intRast[CORE_RAST].getNumberOfCols(); c++) {
                intRast[CORE_RAST].setCellValue(r, c, 0, true);
                if (!intRast[LANDCOV_RAST].cellIsNoData(r, c)) {
                    for (int d = 0; d < coreTypes.length; d++) {
                        if (intRast[LANDCOV_RAST].getCellValue(r, c) == coreTypes[d]) {
                            intRast[CORE_RAST].setCellValue(r, c, 1, true);
                        }
                    }
                }
            }
        }
        // I could check to make sure there is some habitat
        // create unique IDs for patches
        intRast[PATCH_RAST] = IntegerRasterFunctions.assignUniquePatchNumbers(intRast[CORE_RAST], 1, FirstOrderNeighborhoods.MOORE);
        patchCats = new CategoryList(intRast[PATCH_RAST], false);
        // create patch event arrays
        patchAreas = new double[patchCats.getNumberOfCategories()];
        patchTransmissions = new boolean[patchCats.getNumberOfCategories()];
        propNumPatchesWithTransmissions = 0.0;
        propAreaPatchesWithTransmissions = 0.0;
        // this process for eliminating small patches should be an IntergerRasterFunctions method
        int catNum, catCells;
        double patchArea;
        for (int c = 0; c < patchCats.getNumberOfCategories(); c++) {
            // get category number for c
            catNum = patchCats.getCategoryIDforIndex(c);
            // get total cells with that category (patch) number
            catCells = patchCats.getTotalCountForCategory(catNum);
            // get the total area of the patch (how small is too small?)
            patchArea = ((double)catCells)*intRast[CORE_RAST].getCellSize()*intRast[CORE_RAST].getCellSize();
            // set values in patch event arrays
            patchAreas[c] = patchArea;
            patchTransmissions[c] = false;
        }
        // System.out.println("Patch ID layer results (after eliminating small patches):");
        // patchCats.display();
        diseaseGraphBeh.setPatchIDreference(intRast[PATCH_RAST]);
    }
    
    private void computeLandscapeMetrics() {
        int[] nonhabTypes = new int[1];
        nonhabTypes[0] = landcovTypes[0];
        int[] habTypes = new int[1];
        habTypes[0] = landcovTypes[1];
        System.out.println("Computing proportion habitat within 200m...");
        fltRast[PROP_200M_RAST] = FloatRasterFunctions.computeProportionWithinRadius(intRast[LANDCOV_RAST], habTypes, 200.0);
        System.out.println("Computing proportion habitat within 1000m...");
        fltRast[PROP_1000M_RAST] = FloatRasterFunctions.computeProportionWithinRadius(intRast[LANDCOV_RAST], habTypes, 1000.0);
        System.out.println("Computing distance to non-habitat...");
        fltRast[DIST_RAST] = FloatRasterFunctions.shortestDistance(intRast[LANDCOV_RAST], nonhabTypes);
    }
    
    private void zeroLocalResponseRasters() {
        //
        if (fltRast[USAGE_RAST] == null) {
            fltRast[USAGE_RAST] = new FloatRaster(rasterNumRows, rasterNumCols, 0.0, 0.0, rasterCellSize, Float.NaN);
        }
        fltRast[USAGE_RAST].setAllCellsToZero(true);
        //
        if (fltRast[CONTACT_ALL_RAST] == null) {
            fltRast[CONTACT_ALL_RAST] = new FloatRaster(rasterNumRows, rasterNumCols, 0.0, 0.0, rasterCellSize, Float.NaN);
        }
        fltRast[CONTACT_ALL_RAST].setAllCellsToZero(true);
        //
        if (fltRast[CONTACT_SI_RAST] == null) {
            fltRast[CONTACT_SI_RAST] = new FloatRaster(rasterNumRows, rasterNumCols, 0.0, 0.0, rasterCellSize, Float.NaN);
        }
        fltRast[CONTACT_SI_RAST].setAllCellsToZero(true);
    }
    
    private void updateLocalResponseRasters() {
        if (agentList != null){
            int tmpR, tmpC;
            float tmpVal;
            for (int a = 0; a < agentList.size(); a++) {
                tmpR = fltRast[CONTACT_SI_RAST].getGridRow(agentList.get(a).getLocation().getY());
                tmpC = fltRast[CONTACT_SI_RAST].getGridCol(agentList.get(a).getLocation().getX());
                //
                tmpVal = fltRast[USAGE_RAST].getCellValue(tmpR, tmpC) + 1.0f;
                fltRast[USAGE_RAST].setCellValue(tmpR, tmpC, tmpVal);
                //
                tmpVal = fltRast[CONTACT_ALL_RAST].getCellValue(tmpR, tmpC) + 
                        (float)(agentList.get(a).getNeighborsWithinDistance(disTransDistMax));
                fltRast[CONTACT_ALL_RAST].setCellValue(tmpR, tmpC, tmpVal);
                //
                tmpVal = fltRast[CONTACT_SI_RAST].getCellValue(tmpR, tmpC) + 
                        (float)(agentList.get(a).getNumberInfectiveWithinDistance(disTransDistMax));
                fltRast[CONTACT_SI_RAST].setCellValue(tmpR, tmpC, tmpVal);
            }
        }
    }
    
    private void generateLocalResponseSamples() {
        if (localResponseSamples == null) {
            int totalSamples = localSamplesPerTrial*numberRealizations;
            localResponseSamples = new float[totalSamples][9];
            localSampleIndex = 0;
        }
        //
        int tmpRow, tmpCol;
        float patchArea;
        int catNum, catCells;
        for (int s = 0; s < localSamplesPerTrial; s++) {
            // generate random coordinates
            tmpRow = RandomNumberGenerator.drawInteger(rasterNumRows);
            tmpCol = RandomNumberGenerator.drawInteger(rasterNumCols);
            // set sample values
            // [0] usage
            localResponseSamples[localSampleIndex][0] = fltRast[USAGE_RAST].getCellValue(tmpRow, tmpCol);
            // [1] allContacts
            localResponseSamples[localSampleIndex][1] = fltRast[CONTACT_ALL_RAST].getCellValue(tmpRow, tmpCol);
            // [2] siContacts
            localResponseSamples[localSampleIndex][2] = fltRast[CONTACT_SI_RAST].getCellValue(tmpRow, tmpCol);
            // [3] is habitat
            if (intRast[LANDCOV_RAST].getCellValue(tmpRow, tmpCol) == landcovTypes[1]) {
                localResponseSamples[localSampleIndex][3] = 1.0f;
            } else {
                localResponseSamples[localSampleIndex][3] = 0.0f;
            }
            // [4] is patch
            // [5] patch area
            if (intRast[PATCH_RAST].cellIsNoData(tmpRow, tmpCol)) {
                localResponseSamples[localSampleIndex][4] = 0.0f;
                localResponseSamples[localSampleIndex][5] = 0.0f;
            } else {
                localResponseSamples[localSampleIndex][4] = 1.0f;
                catNum = intRast[PATCH_RAST].getCellValue(tmpRow, tmpCol);
                catCells = patchCats.getTotalCountForCategory(catNum);
                patchArea = ((float)catCells)*((float)intRast[PATCH_RAST].getCellSize()*(float)intRast[PATCH_RAST].getCellSize());
                localResponseSamples[localSampleIndex][5] = patchArea;
            }
            // [6] propHab200m
            localResponseSamples[localSampleIndex][6] = fltRast[PROP_200M_RAST].getCellValue(tmpRow, tmpCol);
            // [7] propHab1000m
            localResponseSamples[localSampleIndex][7] = fltRast[PROP_1000M_RAST].getCellValue(tmpRow, tmpCol);
            // [8] distToHab
            localResponseSamples[localSampleIndex][8] = fltRast[DIST_RAST].getCellValue(tmpRow, tmpCol);
            //
            localSampleIndex++;
        }
    }
    
    private void computeProportionPatchesWithTransmissions() {
        // zero out proportions
        propNumPatchesWithTransmissions = 0.0;
        propAreaPatchesWithTransmissions = 0.0;
        //
        int patchNum, patchInd;
        for (int n = 0; n < simGraphs[DISEASE_GRAPH].getNumberOfNodes(); n++) {
            if ((simGraphs[DISEASE_GRAPH].getNode(n) != null) && (simGraphs[DISEASE_GRAPH].getNode(n).getNodeData() != null)) {
                patchNum = ((DiseaseGraphNodeData) simGraphs[DISEASE_GRAPH].getNode(n).getNodeData()).getTransmissionPatchID();
                patchInd = patchCats.getIndexForCategory(patchNum);
                if ((patchInd >= 0) && (patchInd < patchTransmissions.length)) {
                    patchTransmissions[patchInd] = true;
                }
            }
        }
        //
        double disArea = 0.0, totArea = 0.0, disNum = 0.0, totNum = 0.0;
        for (int p = 0; p < patchTransmissions.length; p++) {
            if (patchTransmissions[p]) {
                disArea += patchAreas[p];
                disNum += 1.0;
            }
            totArea += patchAreas[p];
            totNum += 1.0;
        }
        //
        propNumPatchesWithTransmissions = disNum/totNum;
        propAreaPatchesWithTransmissions = disArea/totArea;
    }
    
    /**
     * 
     */
    private void createRandomInitialLocations() {
        if ((intRast[CORE_RAST] != null) && (numberInitialLocations > 0)) {
            // DO A CHECK FOR THE THE EXISTENCE OF CORE AREAS... 
            CategoryList coreCats = new CategoryList(intRast[CORE_RAST], false);
            if (coreCats.getTotalCountForCategory(1) > numberInitialLocations) {
                double xmn = intRast[CORE_RAST].getXmin();
                double xmx = intRast[CORE_RAST].getXmax();
                double ymn = intRast[CORE_RAST].getYmin();
                double ymx = intRast[CORE_RAST].getYmax();

                double tmpX, tmpY;
                int tmpV;
                initialLocs.clearAllVertices();
                while (initialLocs.getSize() < numberInitialLocations) {
                    tmpX = (xmx - xmn) * RandomNumberGenerator.drawDouble() + xmn;
                    tmpY = (ymx - ymn) * RandomNumberGenerator.drawDouble() + ymn;
                    tmpV = intRast[CORE_RAST].getCellValue(tmpX, tmpY);
                    if (tmpV == 1) {
                        initialLocs.addVertex(new PointIn2D(tmpX, tmpY));
                    }
                }
            }
        }
    }
    
    /**
     * 
     */
    private void createInitialLocations() {
        //simStatusMessage = "Creating initial locations...";
        calculateNumberAgentsFromDensity();
        createRandomInitialLocations();
    }
    
    /**
     * In this simulation, each individual moves at the same time in random order.
     * Once each individual has moved, the agentGraph connections are reformed
     * based on distance (these form the basis of movement in response to other
     * agents).
     * 
     */
    public void advanceSimulationTime() {
        simStatusMessage = "Running simulation...";
        // generate the simulation events to be executed
        Agent tmpAgent = null;
        if (agentList.size() > 0) {
            currentTime += discreteTimeStep;
            randOrder = RandomNumberGenerator.permuteIntegers(simGraphs[PROXIMITY_GRAPH].getNumberOfNodes());
            for (int i = 0; i < agentList.size(); i++) {
                tmpAgent = agentList.get(randOrder[i]);
                tmpAgent.getAgentBehavior(moveBehaviorNum).executeBehavior(tmpAgent, currentTime);
            }
            // graph events
            simGraphs[PROXIMITY_GRAPH].getGraphBehavior(0).executeBehavior(simGraphs[PROXIMITY_GRAPH], currentTime);
            simGraphs[DISEASE_GRAPH].getGraphBehavior(0).executeBehavior(simGraphs[DISEASE_GRAPH], currentTime);
        }
    }
    
    /**
     * 
     * reset for new realization
     */
    public void reinitializeForNextRealization() {
        simStatusMessage = "Reinitializaing...";
        // reinitialize time
        currentTime = 0.0;
        timeStepIndex = 0;
        // reinitialize time series update vars
        timeSeriesUpdateIndex = 0;
        nextTimeSeriesUpdateTime = 0.0;
        //
        zeroLocalResponseRasters();
        // 
        realizationComplete = false;
        // land cover
        if (currentRealization % updateLandCoverInterval == 0) {
            System.out.println("creating new landscape on realization " + currentRealization);
            createLandCover();
        }
        // on first time step, create the initial locations
        if ((initialLocs == null) && (simGraphs[PROXIMITY_GRAPH] != null) && (simGraphs[DISEASE_GRAPH] != null)) {
            createInitialLocations();
        }
        //
        
        if ((initialLocs != null) && (simGraphs[PROXIMITY_GRAPH] != null) && (simGraphs[DISEASE_GRAPH] != null)) {
            agentList.clear();
            simGraphs[PROXIMITY_GRAPH].clearGraphNodesAndEdges();
            simGraphs[DISEASE_GRAPH].clearGraphNodesAndEdges();
            //
            createInitialLocations();
            // 
            int nrc = 12;
            distGraphBeh.setGridIndex(new GridIndex(getLandscapeMinimumBoundingBox(), nrc));
            //
            boolean isInitInfect = false;
            int numInitInfectiveCount = 0;
            int numInfective = (int)(Math.round(((double)initialLocs.getSize())*prInitInfect) + 0.01);
            // set up agents
            for (int i = 0; i < initialLocs.getSize(); i++) {
                // determine if agent as infectious at start using binomal process
                /* --
                if (RandomNumberGenerator.drawDouble() <= prInitInfect) {
                    isInitInfect = true;
                    numInitInfectiveCount++;
                } else {
                    isInitInfect = false;
                }
                -- */
                // since agents are assigned random initial locations, we can just
                // make the first numIfective infective
                if (i < numInfective) {
                    isInitInfect = true;
                    numInitInfectiveCount++;
                } else {
                    isInitInfect = false;
                }
                //
                DiseaseMoveAgent tmpAgent = new DiseaseMoveAgent(new PointIn2D(initialLocs.getPoint(i).getX(), initialLocs.getPoint(i).getY()), this, isInitInfect, intRast[PATCH_RAST].getCellValue(initialLocs.getPoint(i)));
                // add move behaviors (7)
                tmpAgent.addAgentBehavior(new MoveRuleSRWwResponse(lcovWts, landcovTypes));
                tmpAgent.addAgentBehavior(new MoveRuleCRWwResponse(lcovWts, landcovTypes, angleConcentration));
                tmpAgent.addAgentBehavior(new MoveRuleDBwResponse(lcovWts, landcovTypes, angleConcentration));
                tmpAgent.addAgentBehavior(new MoveRulePBwResponse(lcovWts, landcovTypes, initialLocs.getPoint(i), angleConcentration, logisticLocParam, logisticScaleParam, true)); // 1088.2 infl point relates to home range radius
                tmpAgent.addAgentBehavior(new MoveRulePBwResponse(lcovWts, landcovTypes, initialLocs.getPoint(i), angleConcentration, logisticLocParam, logisticScaleParam, false)); // infl point could relate to dispersal distance
                tmpAgent.addAgentBehavior(new MoveRuleConSpwResponse(lcovWts, landcovTypes, angleConcentration, logisticLocParam, logisticScaleParam, true));
                tmpAgent.addAgentBehavior(new MoveRuleConSpwResponse(lcovWts, landcovTypes, angleConcentration, logisticLocParam, logisticScaleParam, false)); 
                //
                agentList.add(tmpAgent);
                //
                simGraphs[PROXIMITY_GRAPH].addNode(new GraphNode(tmpAgent.getProximityNodeData()));
                simGraphs[DISEASE_GRAPH].addNode(new GraphNode(tmpAgent.getDiseaseNodeData()));
            }
            simGraphs[PROXIMITY_GRAPH].getGraphBehavior(0).executeBehavior(simGraphs[PROXIMITY_GRAPH], 0.0);
            // ANY DISEASE GRAPH BEHAVIOR?
            
            System.out.println(initialLocs.getSize() + " agents; " + numInitInfectiveCount + " initially infective.");
        }
    }
    
    /**
     * 
     * reset for a new simulation
     */
    public void reinitializeForNextSimulation() {
        reinitializeForNextRealization();
        finalOutputCalculated = false;
        localSampleIndex = 0;
    }
    
    /**
     * is done with current realization
     * @return true if has completed current realization.
     */
    public boolean hasCompletedRealization() {
        return realizationComplete;
    }
    
    /**
     * is done with last realizatioN
     * @return true if has completed simulation.
     */
    public boolean hasCompletedSimulation() {
        if (currentRealization < numberRealizations) {
            return false;
        } else {
            return true;
        }
    }
    
    /***************************************************************************
    * subject methods for observer pattern
    ***************************************************************************/
    
    /**
     * 
     * @param seo
     */
    public void attachObserver(SimulationEngineObserver seo) {
        if ((seo != null) && (!observerList.contains(seo))) {
            observerList.add(seo);
        }
    }
    
    /**
     * 
     * @param seo
     */
    public void detachObserver(SimulationEngineObserver seo) {
        if ((seo != null) && (observerList.contains(seo))) {
            observerList.remove(seo);
        }
    }
    
    /**
     * 
     * @param ev
     */
    public void notify(SimulationEngineObserverEvent ev) {
        for (int i = 0; i < observerList.size(); i++) {
            observerList.get(i).update(this, ev);
        }
    }
    
    /***************************************************************************
    * set simulation control parameters
    ***************************************************************************/
    /**
     * 
     * @param t
     */
    public void setSimTimeInterval(double t) {
        // do nothing
    }
    
    /**
     * 
     * @param t
     */
    public void setdiscreteTimeStep(double t) {
        if (t > 0.0) {
            discreteTimeStep = t;
        }
    }
    
    /**
     * 
     * @param t
     */
    public void setSimTimeDuration(double t) {
        if (t > 0.0) {
            maxTime = t;
        }
    }
    
    /**
     * 
     * @param n
     */
    public void setNumberOfRealizations(int n) {
        if (n > 0) {
            numberRealizations = n;
        }
    }
    
    /***************************************************************************
    * methods to check status
    ***************************************************************************/
    
    /**
     * 
     * @return current realization number.
     */
    public int getCurrentRealizationNumber() {
        return currentRealization;
    }
    
    /**
     * 
     * @return total number of realizations.
     */
    public int getNumberOfRealizations() {
        return numberRealizations;
    }
    
    /**
     * 
     * @return current time.
     */
    public double getCurrentTime() {
        return currentTime;
    }
    
    /**
     * 
     * @return time interval duration.
     */
    public double getTimeDuration() {
        return maxTime;
    }
    
    /**
     * 
     * @return
     */
    public String getStatusMessage() {
        return simStatusMessage;
    }
    
    /**
     * 
     * @return
     */
    public int getNumberOfSusceptibles() {
        return diseaseGraphBeh.getNumberSusceptible();
    }
    
    /**
     * 
     * @return
     */
    public int getNumberOfInfectious() {
        return diseaseGraphBeh.getNumberInfectious();
    }
    
    /***************************************************************************
    * set and retreive simulation data
    ***************************************************************************/
    // Graph: set, get, check for existence
    
    public void addGraph(Graph g, int i) {
        // DO SOMETHING
    }
    
    public Graph getGraph(int i) {
        if ((i>= 0) && (i < simGraphs.length)) {
            return simGraphs[i];
        } else {
            return null;
        }
    }
    
    public boolean isValidGraphIndex(int i) {
        if ((i>= 0) && (i < simGraphs.length)) {
            return true;
        } else {
            return false;
        }
    }
    
    // FloatRaster: set, get, check for existence
    
    public void addFloatRaster(FloatRaster f, int i) {
        // DO SOMETHING
    }
    
    public FloatRaster getFloatRaster(int i) {
        if ((i>= 0) && (i < fltRast.length)) {
            return fltRast[i];
        } else {
            return null;
        }
    }
    
    public boolean isValidFloatRasterIndex(int i) {
        if ((i>= 0) && (i < fltRast.length)) {
            return true;
        } else {
            return false;
        }
    }
    
    // IntegerRaster: set, get, check for existence
    
    public void addIntegerRaster(IntegerRaster f, int i) {
        // DO SOMETHING
    }
    
    public IntegerRaster getIntegerRaster(int i) {
        if ((i>= 0) && (i < intRast.length)) {
            return intRast[i];
        } else {
            return null;
        }
    }
    
    public boolean isValidIntegerRasterIndex(int i) {
        if ((i>= 0) && (i < intRast.length)) {
            return true;
        } else {
            return false;
        }
    }
    
    // PointLayerIn2D: set, get, check for existence
    
    public void addPointLayerIn2D(PointLayerIn2D p, int i) {
        // DO SOMETHING
    }
    
    public PointLayerIn2D getPointLayerIn2D(int i) {
        if ((i>= 0) && (i < pointLayers.length)) {
            return pointLayers[i];
        } else {
            return null;
        }
    }
    
    public boolean isValidPointLayerIn2DIndex(int i) {
        if ((i>= 0) && (i < pointLayers.length)) {
            return true;
        } else {
            return false;
        }
    }
    
    // PolyLineLayerIn2D: set, get, check for existence
    
    public void addPolyLineLayerIn2D(PolyLineLayerIn2D p, int i) {
        // DO SOMETHING
    }
    
    public PolyLineLayerIn2D getPolyLineLayerIn2D(int i) {
        if ((i>= 0) && (i < lineLayers.length)) {
            return lineLayers[i];
        } else {
            return null;
        }
    }
    
    public boolean isValidPolyLineLayerIn2DIndex(int i) {
        if ((i>= 0) && (i < lineLayers.length)) {
            return true;
        } else {
            return false;
        }
    }
    
    /***************************************************************************
    * calculate/write output
    ***************************************************************************/
    
    /**
     * NEED TO ADD CHECKS FOR NULL LAND COVER, CORES, ETC.
     */
    
    private void performEndOfTimeStepCalculations() {
        if ((times != null) && (propInfectedTimeSeries != null) &&
                (timeSeriesUpdateIndex < timeSeriesUpdatesPerRealization) &&
                (currentRealization < numberRealizations) &&
                (diseaseGraphBeh != null) && (distGraphBeh != null) &&
                (currentTime >= nextTimeSeriesUpdateTime)) {
            // update output
            times[timeSeriesUpdateIndex] = (float) diseaseGraphBeh.getEventTime();
            propInfectedTimeSeries[timeSeriesUpdateIndex][currentRealization] = diseaseGraphBeh.getProportionInfectious();
            // set info for next update
            nextTimeSeriesUpdateTime += timeSeriesUpdateInterval;
            timeSeriesUpdateIndex++;

        }
        //
        updateLocalResponseRasters();
    }
    
    public void performEndOfTrialCalculations() {
        simStatusMessage = "Performing calculations...";
        //
        // calculate patch transmission statistics...
        diseaseGraphBeh.calculatePatchTransmissionStatistics(simGraphs[DISEASE_GRAPH]);
        diseaseGraphBeh.calculateMeanTransmissionTimeAndDistance(simGraphs[DISEASE_GRAPH]);
        //
        float tmpNumPatches, tmpMeanPatchArea, tmpTotalPatchArea;
        if (patchCats != null) {
            int numPatches = patchCats.getNumberOfCategories();
            tmpNumPatches = (float)numPatches;
            tmpTotalPatchArea = 0.0f;
            float tmpCellArea = (float)(intRast[PATCH_RAST].getCellSize() * intRast[PATCH_RAST].getCellSize());
            for (int i = 0; i < numPatches; i++) {
                tmpTotalPatchArea += ((float)patchCats.getTotalCountForCategory(patchCats.getCategoryIDforIndex(i)))*tmpCellArea;
            }
            tmpMeanPatchArea = tmpTotalPatchArea/tmpNumPatches;
        } else {
            tmpNumPatches = Float.NaN;
            tmpMeanPatchArea = Float.NaN;
            tmpTotalPatchArea = Float.NaN;
        }
        //
        computeProportionPatchesWithTransmissions();
        // 
        trialPatchStats[0][currentRealization] = tmpNumPatches; // number of patches
        trialPatchStats[1][currentRealization] = tmpMeanPatchArea; // mean patch size
        trialPatchStats[2][currentRealization] = tmpTotalPatchArea; // total patch area
        trialPatchStats[3][currentRealization] = (float)diseaseGraphBeh.getProportionOfWithinPatchTransmissions(); // pr. trans. within
        trialPatchStats[4][currentRealization] = (float)diseaseGraphBeh.getProportionOfBetweenPatchTransmissions(); // pr. trans. between
        trialPatchStats[5][currentRealization] = (float)diseaseGraphBeh.getProportionOfOutsidePatchTransmissions(); // pr. trans. outside
        trialPatchStats[6][currentRealization] = diseaseGraphBeh.getMeanTimeBetweenTransmissions(); // mean trans time
        trialPatchStats[7][currentRealization] = diseaseGraphBeh.getMeanDistanceBetweenTransmissions(); // mean trans dist
        trialPatchStats[8][currentRealization] = (float)propNumPatchesWithTransmissions;
        trialPatchStats[9][currentRealization] = (float)propAreaPatchesWithTransmissions;
        //
        generateLocalResponseSamples();
    }
    
    
    private float[] computeSummaryStats(FloatComparable[] fca) {
        float[] res = new float[8]; // 05p, 25p, 50p, 75p, 95p, mn, var, prNotNaN
        // default is NaN
        for (int i = 0; i < res.length; i++) {
            res[i] = Float.NaN;
        }
        // sort the intputs
        if ((fca != null) && (fca.length > 0)) {
            int datCount = 0;
            float dataSum = 0.0f;
            SortUtilities.quickSort(fca);
            for (int j = 0; j < fca.length; j++) {
                // System.out.println(j + ": " + fca[j].getValue());
                if (!Float.isNaN(fca[j].getValue())) {
                    datCount++;
                    dataSum += fca[j].getValue();
                }
            }
            //
            if (datCount > 0) {
                //
                int ci05index = (int) Math.floor(0.05 * ((double) (datCount - 1)));
                int ci25index = (int) Math.floor(0.25 * ((double) (datCount - 1)));
                int ci50index = (int) Math.round(0.50 * ((double) (datCount - 1)));
                int ci75index = (int) Math.ceil(0.75 * ((double) (datCount - 1)));
                int ci95index = (int) Math.ceil(0.95 * ((double) (datCount - 1)));
                // correct out-of-bounds indices
                if (ci05index < 0) {
                    ci05index = 0;
                }
                if (ci25index < 0) {
                    ci25index = 0;
                }
                if (ci75index >= datCount) {
                    ci75index = datCount - 1;
                }
                if (ci95index >= datCount) {
                    ci95index = datCount - 1;
                }
                //
                float dataMean = dataSum / ((float) datCount);
                float dataVar = 0.0f;
                for (int j = 0; j < datCount; j++) {
                    dataVar += (fca[j].getValue() - dataMean) * (fca[j].getValue() - dataMean);
                }
                dataVar /= ((float) datCount);
                res[0] = fca[ci05index].getValue();
                res[1] = fca[ci25index].getValue();
                res[2] = fca[ci50index].getValue();
                res[3] = fca[ci75index].getValue();
                res[4] = fca[ci95index].getValue();
                res[5] = dataMean;
                res[6] = dataVar;
                res[7] = ((float) datCount) / ((float) fca.length);
            }
        }
        return res;
    }
    
    /**
     * NEED TO ADD CHECKS FOR NULL LAND COVER, CORES, ETC.
     */
    public void performEndOfSimulationCalculations() {
        // numberRealizations
        simStatusMessage = "Performing calculations...";
        // temp arry to store results
        float[] tmpResult = null;
        
        
        // time series results
        
        // arrays to stick things in for sorting
        FloatComparable[] tmpPropInf = new FloatComparable[numberRealizations];
        // 
        
        if (simulationTimeSeries != null) {
            for (int ts = 0; ts < timeSeriesUpdatesPerRealization; ts++) {
                simulationTimeSeries[ts][0] = times[ts];
                for (int rs = 0; rs < numberRealizations; rs++) {
                    // proportion infected
                    if (tmpPropInf[rs] == null) {
                        tmpPropInf[rs] = new FloatComparable(propInfectedTimeSeries[ts][rs]);
                    } else {
                        tmpPropInf[rs].setValue(propInfectedTimeSeries[ts][rs]);
                    }
                }
                // proportion infected
                tmpResult = computeSummaryStats(tmpPropInf);
                simulationTimeSeries[ts][1] = tmpResult[0];
                simulationTimeSeries[ts][2] = tmpResult[1];
                simulationTimeSeries[ts][3] = tmpResult[2];
                simulationTimeSeries[ts][4] = tmpResult[3];
                simulationTimeSeries[ts][5] = tmpResult[4];
                simulationTimeSeries[ts][6] = tmpResult[5];
                simulationTimeSeries[ts][7] = tmpResult[6];
            }
        }
        
        // arrays to stick things in for sorting
        FloatComparable[] tmpNumPatches = new FloatComparable[numberRealizations];
        FloatComparable[] tmpMeanPatchSize = new FloatComparable[numberRealizations];
        FloatComparable[] tmpTotalPatchSize = new FloatComparable[numberRealizations];
        FloatComparable[] tmpTransWithin = new FloatComparable[numberRealizations];
        FloatComparable[] tmpTransBetween = new FloatComparable[numberRealizations];
        FloatComparable[] tmpTransOutside = new FloatComparable[numberRealizations];
        FloatComparable[] tmpMeanTransTime = new FloatComparable[numberRealizations];
        FloatComparable[] tmpMeanTransDist = new FloatComparable[numberRealizations];
        FloatComparable[] tmpPropNumPatchTrans = new FloatComparable[numberRealizations];
        FloatComparable[] tmpPropAreaPatchTrans = new FloatComparable[numberRealizations];
        
        for (int rs = 0; rs < numberRealizations; rs++) {
            //
            // 0. number of patches
            if (tmpNumPatches[rs] == null) {
                tmpNumPatches[rs] = new FloatComparable(trialPatchStats[0][rs]);
            } else {
                tmpNumPatches[rs].setValue(trialPatchStats[0][rs]);
            }
            // 1. mean patch area
            if (tmpMeanPatchSize[rs] == null) {
                tmpMeanPatchSize[rs] = new FloatComparable(trialPatchStats[1][rs]);
            } else {
                tmpMeanPatchSize[rs].setValue(trialPatchStats[1][rs]);
            }
            // 2. total patch area
            if (tmpTotalPatchSize[rs] == null) {
                tmpTotalPatchSize[rs] = new FloatComparable(trialPatchStats[2][rs]);
            } else {
                tmpTotalPatchSize[rs].setValue(trialPatchStats[2][rs]);
            }
            // 3. proportion within
            if (tmpTransWithin[rs] == null) {
                tmpTransWithin[rs] = new FloatComparable(trialPatchStats[3][rs]);
            } else {
                tmpTransWithin[rs].setValue(trialPatchStats[3][rs]);
            }
            // 4. proportion between
            if (tmpTransBetween[rs] == null) {
                tmpTransBetween[rs] = new FloatComparable(trialPatchStats[4][rs]);
            } else {
                tmpTransBetween[rs].setValue(trialPatchStats[4][rs]);
            }
            // 5. proportion outside
            if (tmpTransOutside[rs] == null) {
                tmpTransOutside[rs] = new FloatComparable(trialPatchStats[5][rs]);
            } else {
                tmpTransOutside[rs].setValue(trialPatchStats[5][rs]);
            }
            // 6. mean transmission time
            if (tmpMeanTransTime[rs] == null) {
                tmpMeanTransTime[rs] = new FloatComparable(trialPatchStats[6][rs]);
            } else {
                tmpMeanTransTime[rs].setValue(trialPatchStats[6][rs]);
            }
            // 7. mean transmission distance
            if (tmpMeanTransDist[rs] == null) {
                tmpMeanTransDist[rs] = new FloatComparable(trialPatchStats[7][rs]);
            } else {
                tmpMeanTransDist[rs].setValue(trialPatchStats[7][rs]);
            }
            // 8.  proportion patches with transmissions
            if (tmpPropNumPatchTrans[rs] == null) {
                tmpPropNumPatchTrans[rs] = new FloatComparable(trialPatchStats[8][rs]);
            } else {
                tmpPropNumPatchTrans[rs].setValue(trialPatchStats[8][rs]);
            }
            // 9.  proportion of area of patches with transmissions
            if (tmpPropAreaPatchTrans[rs] == null) {
                tmpPropAreaPatchTrans[rs] = new FloatComparable(trialPatchStats[9][rs]);
            } else {
                tmpPropAreaPatchTrans[rs].setValue(trialPatchStats[9][rs]);
            }
        }
        
        // 0. number of patches
        tmpResult = computeSummaryStats(tmpNumPatches);
        simulationPatchStats[0] = tmpResult[0];
        simulationPatchStats[1] = tmpResult[1];
        simulationPatchStats[2] = tmpResult[2];
        simulationPatchStats[3] = tmpResult[3];
        simulationPatchStats[4] = tmpResult[4];
        simulationPatchStats[5] = tmpResult[5];
        simulationPatchStats[6] = tmpResult[6];
        // 1. mean patch area
        tmpResult = computeSummaryStats(tmpMeanPatchSize);
        simulationPatchStats[7] = tmpResult[0];
        simulationPatchStats[8] = tmpResult[1];
        simulationPatchStats[9] = tmpResult[2];
        simulationPatchStats[10] = tmpResult[3];
        simulationPatchStats[11] = tmpResult[4];
        simulationPatchStats[12] = tmpResult[5];
        simulationPatchStats[13] = tmpResult[6];
        // 2. total patch area
        tmpResult = computeSummaryStats(tmpTotalPatchSize);
        simulationPatchStats[14] = tmpResult[0];
        simulationPatchStats[15] = tmpResult[1];
        simulationPatchStats[16] = tmpResult[2];
        simulationPatchStats[17] = tmpResult[3];
        simulationPatchStats[18] = tmpResult[4];
        simulationPatchStats[19] = tmpResult[5];
        simulationPatchStats[20] = tmpResult[6];
        // 3. proportion within
        tmpResult = computeSummaryStats(tmpTransWithin);
        simulationPatchStats[21] = tmpResult[0];
        simulationPatchStats[22] = tmpResult[1];
        simulationPatchStats[23] = tmpResult[2];
        simulationPatchStats[24] = tmpResult[3];
        simulationPatchStats[25] = tmpResult[4];
        simulationPatchStats[26] = tmpResult[5];
        simulationPatchStats[27] = tmpResult[6];
        PropSimsWithTransmissions = tmpResult[7]; // keep track of number of sims with zero transmissions?
        // 4. proportion between
        tmpResult = computeSummaryStats(tmpTransBetween);
        simulationPatchStats[28] = tmpResult[0];
        simulationPatchStats[29] = tmpResult[1];
        simulationPatchStats[30] = tmpResult[2];
        simulationPatchStats[31] = tmpResult[3];
        simulationPatchStats[32] = tmpResult[4];
        simulationPatchStats[33] = tmpResult[5];
        simulationPatchStats[34] = tmpResult[6];
        // 5. proportion outside
        tmpResult = computeSummaryStats(tmpTransOutside);
        simulationPatchStats[35] = tmpResult[0];
        simulationPatchStats[36] = tmpResult[1];
        simulationPatchStats[37] = tmpResult[2];
        simulationPatchStats[38] = tmpResult[3];
        simulationPatchStats[39] = tmpResult[4];
        simulationPatchStats[40] = tmpResult[5];
        simulationPatchStats[41] = tmpResult[6];
        // 6. mean transmission time
        tmpResult = computeSummaryStats(tmpMeanTransTime);
        simulationPatchStats[42] = tmpResult[0];
        simulationPatchStats[43] = tmpResult[1];
        simulationPatchStats[44] = tmpResult[2];
        simulationPatchStats[45] = tmpResult[3];
        simulationPatchStats[46] = tmpResult[4];
        simulationPatchStats[47] = tmpResult[5];
        simulationPatchStats[48] = tmpResult[6];
        // 7. mean transmission distance
        tmpResult = computeSummaryStats(tmpMeanTransDist);
        simulationPatchStats[49] = tmpResult[0];
        simulationPatchStats[50] = tmpResult[1];
        simulationPatchStats[51] = tmpResult[2];
        simulationPatchStats[52] = tmpResult[3];
        simulationPatchStats[53] = tmpResult[4];
        simulationPatchStats[54] = tmpResult[5];
        simulationPatchStats[55] = tmpResult[6];
        // 8.  proportion patches with transmissions
        tmpResult = computeSummaryStats(tmpPropNumPatchTrans);
        simulationPatchStats[56] = tmpResult[0];
        simulationPatchStats[57] = tmpResult[1];
        simulationPatchStats[58] = tmpResult[2];
        simulationPatchStats[59] = tmpResult[3];
        simulationPatchStats[60] = tmpResult[4];
        simulationPatchStats[61] = tmpResult[5];
        simulationPatchStats[62] = tmpResult[6];
        // 9.  proportion of area of patches with transmissions
        tmpResult = computeSummaryStats(tmpPropAreaPatchTrans);
        simulationPatchStats[63] = tmpResult[0];
        simulationPatchStats[64] = tmpResult[1];
        simulationPatchStats[65] = tmpResult[2];
        simulationPatchStats[66] = tmpResult[3];
        simulationPatchStats[67] = tmpResult[4];
        simulationPatchStats[68] = tmpResult[5];
        simulationPatchStats[69] = tmpResult[6];
        //
        finalOutputCalculated = true;
        writeOutput();
    }
    
    /**
     * 
     */
    public void writeOutput() {
        // write time series to file
        writeTimeSeriesTabDelimOutput(simOutputFilenameBase + "-TimeSeries.txt");
        // write local landsape and contact/transmission samples
        this.writeLocalResponseOutputTabDelim(simOutputFilenameBase + "-LocalResponse.txt");
    }
    
    
    
    /**
     * 
     * @param ofileName
     */
    private void writeTimeSeriesTabDelimOutput(String ofileName) {
        if ((finalOutputCalculated) && (simulationTimeSeries != null)){
            // write out data...
            FileWriter writer1 = null;
            try {
                writer1 = new FileWriter(ofileName);
                writer1.write("time\t"); //
                writer1.write("propI05p\tpropI25p\tpropI50p\tpropI75p\tpropI95p\tpropImean\tpropIvar\n"); //
                
                for (int ntm = 0; ntm < timeSeriesUpdatesPerRealization; ntm++) {
                    System.out.println("Writing output for time " + simulationTimeSeries[ntm][0] + "...");
                    writer1.write(String.valueOf(simulationTimeSeries[ntm][0]) + "\t");
                    writer1.write(String.valueOf(simulationTimeSeries[ntm][1]) + "\t");
                    writer1.write(String.valueOf(simulationTimeSeries[ntm][2]) + "\t");
                    writer1.write(String.valueOf(simulationTimeSeries[ntm][3]) + "\t");
                    writer1.write(String.valueOf(simulationTimeSeries[ntm][4]) + "\t");
                    writer1.write(String.valueOf(simulationTimeSeries[ntm][5]) + "\t");
                    writer1.write(String.valueOf(simulationTimeSeries[ntm][6]) + "\t");
                    writer1.write(String.valueOf(simulationTimeSeries[ntm][7]) + "\n");
                }
                
                if (writer1 != null) {
                    writer1.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    // methods for contacts/transmissions/local landscape metrics samples
    
    
    
    /**
     * 
     * @return
     */
    String patchStatHeaderToTabDelimString() {
        String res = "sim\tnPatch05p\tnPatch25p\tnPatch50p\tnPatch75p\tnPatch95p\tnPatchMean\ttnPatchVar\t" +
                "mnPtchA05p\tmnPtchA25p\tmnPtchA50p\tmnPtchA75p\tmnPtchA95p\tmnPtchAmean\tmnPtchAvar\t" +
                "totPtchA05p\ttotPtchA25p\ttotPtchA50p\ttotPtchA75p\ttotPtchA95p\ttotPtchAmean\ttotPtchAvar\t" +
                "prSimsWithTrans\tprWithin05p\tprWithin25p\tprWithin50p\tprWithin75p\tprWithin95p\tprWithinMean\tprWithinVar\t" +
                "prBetwn05p\tprBetwn25p\tprBetwn50p\tprBetwn75p\tprBetwn95p\tprBetwnMean\tprBetwnVar\t" +
                "prOutsd05p\tprOutsd25p\tprOutsd50p\tprOutsd75p\tprOutsd95p\tprOutsdMean\tprOutsdVar\t" +
                "mnTranTime05p\tmnTranTime25p\tmnTranTime50p\tmnTranTime75p\tmnTranTime95p\tmnTranTimeMean\tmnTranTimeVar\t" +
                "mnTranDist05p\tmnTranDist25p\tmnTranDist50p\tmnTranDist75p\tmnTranDist95p\tmnTranDistMean\tmnTranDistVar\t" +
                "propI05p\tpropI25p\tpropI50p\tpropI75p\tpropI95p\tpropImean\tpropIvar\t" +
                "prPatchNumI05p\tprPatchNumI25p\tprPatchNumI50p\tprPatchNumI75p\tprPatchNumI95p\tprPatchNumImean\tprPatchNumIvar\t" +
                "prPatchAreaI05p\tprPatchAreaI25p\tprPatchAreaI50p\tprPatchAreaI75p\tprPatchAreaI95p\tprPatchAreaImean\tprPatchAreaIvar\n";
        return res;
    }
    
    /**
     * 
     * @return
     */
    String patchStatsToTabDelimString() {
        String res = null;
        if ((finalOutputCalculated) && (simulationPatchStats != null)) {
            res = simOutputFilenameBase + "\t" +
                    String.valueOf(simulationPatchStats[0]) + "\t" +
                    String.valueOf(simulationPatchStats[1]) + "\t" +
                    String.valueOf(simulationPatchStats[2]) + "\t" +
                    String.valueOf(simulationPatchStats[3]) + "\t" +
                    String.valueOf(simulationPatchStats[4]) + "\t" +
                    String.valueOf(simulationPatchStats[5]) + "\t" +
                    String.valueOf(simulationPatchStats[6]) + "\t" +
                    // 1. mean patch area
                    String.valueOf(simulationPatchStats[7]) + "\t" +
                    String.valueOf(simulationPatchStats[8]) + "\t" +
                    String.valueOf(simulationPatchStats[9]) + "\t" +
                    String.valueOf(simulationPatchStats[10]) + "\t" +
                    String.valueOf(simulationPatchStats[11]) + "\t" +
                    String.valueOf(simulationPatchStats[12]) + "\t" +
                    String.valueOf(simulationPatchStats[13]) + "\t" +
                    // 2. total patch area
                    String.valueOf(simulationPatchStats[14]) + "\t" +
                    String.valueOf(simulationPatchStats[15]) + "\t" +
                    String.valueOf(simulationPatchStats[16]) + "\t" +
                    String.valueOf(simulationPatchStats[17]) + "\t" +
                    String.valueOf(simulationPatchStats[18]) + "\t" +
                    String.valueOf(simulationPatchStats[19]) + "\t" +
                    String.valueOf(simulationPatchStats[20]) + "\t" +
                    // x. proportion with transmissions
                    String.valueOf(PropSimsWithTransmissions) + "\t" +
                    // 3. proportion within
                    String.valueOf(simulationPatchStats[21]) + "\t" +
                    String.valueOf(simulationPatchStats[22]) + "\t" +
                    String.valueOf(simulationPatchStats[23]) + "\t" +
                    String.valueOf(simulationPatchStats[24]) + "\t" +
                    String.valueOf(simulationPatchStats[25]) + "\t" +
                    String.valueOf(simulationPatchStats[26]) + "\t" +
                    String.valueOf(simulationPatchStats[27]) + "\t" +
                    // 4. proportion between
                    String.valueOf(simulationPatchStats[28]) + "\t" +
                    String.valueOf(simulationPatchStats[29]) + "\t" +
                    String.valueOf(simulationPatchStats[30]) + "\t" +
                    String.valueOf(simulationPatchStats[31]) + "\t" +
                    String.valueOf(simulationPatchStats[32]) + "\t" +
                    String.valueOf(simulationPatchStats[33]) + "\t" +
                    String.valueOf(simulationPatchStats[34]) + "\t" +
                    // 5. proportion outside
                    String.valueOf(simulationPatchStats[35]) + "\t" +
                    String.valueOf(simulationPatchStats[36]) + "\t" +
                    String.valueOf(simulationPatchStats[37]) + "\t" +
                    String.valueOf(simulationPatchStats[38]) + "\t" +
                    String.valueOf(simulationPatchStats[39]) + "\t" +
                    String.valueOf(simulationPatchStats[40]) + "\t" +
                    String.valueOf(simulationPatchStats[41]) + "\t" +
                    // 6. mean transmission time
                    String.valueOf(simulationPatchStats[42]) + "\t" +
                    String.valueOf(simulationPatchStats[43]) + "\t" +
                    String.valueOf(simulationPatchStats[44]) + "\t" +
                    String.valueOf(simulationPatchStats[45]) + "\t" +
                    String.valueOf(simulationPatchStats[46]) + "\t" +
                    String.valueOf(simulationPatchStats[47]) + "\t" +
                    String.valueOf(simulationPatchStats[48]) + "\t" +
                    // 7. mean transmission distance
                    String.valueOf(simulationPatchStats[49]) + "\t" +
                    String.valueOf(simulationPatchStats[50]) + "\t" +
                    String.valueOf(simulationPatchStats[51]) + "\t" +
                    String.valueOf(simulationPatchStats[52]) + "\t" +
                    String.valueOf(simulationPatchStats[53]) + "\t" +
                    String.valueOf(simulationPatchStats[54]) + "\t" +
                    String.valueOf(simulationPatchStats[55]) + "\t" +
                    // 8. population state at final time
                    String.valueOf(simulationTimeSeries[timeSeriesUpdatesPerRealization - 1][1])  + "\t" +
                    String.valueOf(simulationTimeSeries[timeSeriesUpdatesPerRealization - 1][2])  + "\t" +
                    String.valueOf(simulationTimeSeries[timeSeriesUpdatesPerRealization - 1][3])  + "\t" +
                    String.valueOf(simulationTimeSeries[timeSeriesUpdatesPerRealization - 1][4])  + "\t" +
                    String.valueOf(simulationTimeSeries[timeSeriesUpdatesPerRealization - 1][5])  + "\t" +
                    String.valueOf(simulationTimeSeries[timeSeriesUpdatesPerRealization - 1][6])  + "\t" +
                    String.valueOf(simulationTimeSeries[timeSeriesUpdatesPerRealization - 1][7])  + "\t" +
                    // variance?
                    // 9. 
                    String.valueOf(simulationPatchStats[56]) + "\t" +
                    String.valueOf(simulationPatchStats[57]) + "\t" +
                    String.valueOf(simulationPatchStats[58]) + "\t" +
                    String.valueOf(simulationPatchStats[59]) + "\t" +
                    String.valueOf(simulationPatchStats[60]) + "\t" +
                    String.valueOf(simulationPatchStats[61]) + "\t" +
                    String.valueOf(simulationPatchStats[62]) + "\t" +
                    // 10. 
                    String.valueOf(simulationPatchStats[63]) + "\t" +
                    String.valueOf(simulationPatchStats[64]) + "\t" +
                    String.valueOf(simulationPatchStats[65]) + "\t" +
                    String.valueOf(simulationPatchStats[66]) + "\t" +
                    String.valueOf(simulationPatchStats[67]) + "\t" +
                    String.valueOf(simulationPatchStats[68]) + "\t" +
                    String.valueOf(simulationPatchStats[69]) + "\n";
            
        }
        return res;
    }
    
    
    private void writeLocalResponseOutputTabDelim(String ofileName) {
        if ((finalOutputCalculated) && (localResponseSamples != null)) {
            FileWriter writer1 = null;
            try {
                writer1 = new FileWriter(ofileName);
                // table header
                writer1.write("usage\t");
                writer1.write("allCont\t");
                writer1.write("siCont\t");
                writer1.write("isHabitat\t");
                writer1.write("isPatch\t");
                writer1.write("patchArea\t");
                writer1.write("propHab200m\t");
                writer1.write("propHab1000m\t");
                writer1.write("distNonhabM");
                writer1.write("\n");
                int totalSamples = localSamplesPerTrial*numberRealizations;
                for (int s = 0; s < totalSamples; s++) {
                    // [0] usage
                    writer1.write(localResponseSamples[s][0] + "\t");
                    // [1] allContacts
                    writer1.write(localResponseSamples[s][1] + "\t");
                    // [2] siContacts
                    writer1.write(localResponseSamples[s][2] + "\t");
                    // [3] is habitat
                    writer1.write(localResponseSamples[s][3] + "\t");
                    // [4] is patch
                    writer1.write(localResponseSamples[s][4] + "\t");
                    // [5] patch area
                    writer1.write(localResponseSamples[s][5] + "\t");
                    // [6] propHab200m
                    writer1.write(localResponseSamples[s][6] + "\t");
                    // [7] propHab1000m
                    writer1.write(localResponseSamples[s][7] + "\t");
                    // [8] distToHab
                    writer1.write(localResponseSamples[s][8] + "\n");
                }
                //
                writer1.write("\n");
                //
                if (writer1 != null) {
                    writer1.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    
    /**
     * Set file name for time series output.
     * @param fn
     */
    public void setTimeSeriesOutputFileName(String fn) {
        if (fn != null) {
            simOutputFilenameBase = fn;
        }

    }
    
    
    /***************************************************************************
    * concrete class-specific data access
    ***************************************************************************/
    
    /**
     * 
     * @return landscape bounding box.
     */
    public BoxIn2D getLandscapeMinimumBoundingBox() {
        if (intRast[LANDCOV_RAST] != null) {
            landscapeMBB = new BoxIn2D(intRast[LANDCOV_RAST].getXmin(), intRast[LANDCOV_RAST].getYmin(), intRast[LANDCOV_RAST].getXmax(), intRast[LANDCOV_RAST].getYmax());
        } else {
            double yMax = ((double) rasterNumRows) * rasterCellSize;
            double xMax = ((double) rasterNumCols) * rasterCellSize;
            landscapeMBB = new BoxIn2D(0.0, 0.0, xMax, yMax);
        }
        return landscapeMBB;
    }
    
    /**
     * 
     * @return
     */
    public Graph getProximityNetwork() {
        return simGraphs[PROXIMITY_GRAPH];
    }
    
    /**
     * 
     * @return
     */
    public Graph getDiseaseNetwork() {
        return simGraphs[DISEASE_GRAPH];
    }
    
    /**
     * 
     * @return reference to initial locations.
     */
    public VertexListIn2D getInitialLocations() {
        return initialLocs;
    }
    
    /**
     * 
     * @return
     */
    public VertexListIn2D getCurrentLocations() {
        VertexListIn2D res = new VertexListIn2D();
        for (int i = 0; i < agentList.size(); i++) {
            res.addVertex(agentList.get(i).getLocation());
        }
        return res;
    }
    
    /**
     * 
     * @return
     */
    public VertexListIn2D getCurrentSusceptibleLocations() {
        VertexListIn2D res = new VertexListIn2D();
        DiseaseMoveAgent tmpA = null;
        for (int i = 0; i < agentList.size(); i++) {
            if (agentList.get(i) instanceof DiseaseMoveAgent) {
                tmpA = (DiseaseMoveAgent) agentList.get(i);
                if (tmpA.agentIsSusceptible()) {
                    res.addVertex(tmpA.getLocation());
                }
            }
        }
        return res;
    }
    
    /**
     * 
     * @return
     */
    public VertexListIn2D getCurrentInfectiousLocations() {
        VertexListIn2D res = new VertexListIn2D();
        DiseaseMoveAgent tmpA = null;
        for (int i = 0; i < agentList.size(); i++) {
            if (agentList.get(i) instanceof DiseaseMoveAgent) {
                tmpA = (DiseaseMoveAgent) agentList.get(i);
                if (tmpA.agentIsInfectious()) {
                    res.addVertex(tmpA.getLocation());
                }
            }
        }
        return res;
    }
    
    // GET CURRENT LOCATIONS...
    
    /**
     * USE ENUM???
     * @param mvNum
     */
    public void setMoveBehavior(int mvNum) {
        if ((mvNum >= 0) && (mvNum < 7)) {
            moveBehaviorNum = mvNum;
        }
    }
    
    /***************************************************************************
    * setters and getters for model parameters
    ***************************************************************************/
    
    /**
     * 
     * @param hurstExp
     * @param propHab
     * @param coreRadius
     */
    void setLandscapeParameters(double hurstExp, double propHab) {
        if(hurstExp >= 0.0) {
            landcoverH = hurstExp;
        }
        if ((propHab >= 0.0) && (propHab <= 1.0)) {
            landcoverProps[0] = 1.0 - propHab;
            landcoverProps[1] = propHab;
            if (useAgentDensity) {
                calculateNumberAgentsFromDensity();
            } else {
                calculateDensityFromNumberAgents();
            }
        }
    }
    
    /**
     * 
     * @param hurstExp
     */
    public void setHurstExponent(double hurstExp) {
        if(hurstExp >= 0.0) {
            landcoverH = hurstExp;
        }
    }
    
    /**
     * 
     * @param propHab
     */
    public void setProportionHabitat(double propHab) {
        if ((propHab >= 0.0) && (propHab <= 1.0)) {
            landcoverProps[0] = 1.0 - propHab;
            landcoverProps[1] = propHab;
            if (useAgentDensity) {
                calculateNumberAgentsFromDensity();
            } else {
                calculateDensityFromNumberAgents();
            }
        }
    }
    
    /**
     * 
     * @return
     */
    public double getHurstExponent() {
        return landcoverH;
    }
    
    /**
     * 
     * @return
     */
    public double getProportionHabitat() {
        return landcoverProps[1];
    }
    
    /**
     * 
     * @param initPropInfected
     * @param transProb
     */
    void setDiseaseParameters(double initPropInfected, double transProb, double transDist) {
        if ((initPropInfected >= 0.0) && (initPropInfected <= 1.0)) {
            prInitInfect = initPropInfected;
        }
        if ((transProb >= 0.0) && (transProb <= 1.0) && (transDist >= 0.0)) {
            disTransProb = transProb;
            disTransDistMax = transDist;
            diseaseGraphBeh.setTransmissionParameters(disTransProb, disTransDistMax);
        }
    }
    
    /**
     * 
     * @param initPropInfected
     */
    public void setInitialProportionInfective(double initPropInfected) {
        if ((initPropInfected >= 0.0) && (initPropInfected <= 1.0)) {
            prInitInfect = initPropInfected;
        }
    }

    /**
     * 
     * @param transProb
     */
    public void setTransmissionProbability(double transProb) {
        if ((transProb >= 0.0) && (transProb <= 1.0)) {
            disTransProb = transProb;
            diseaseGraphBeh.setTransmissionParameters(disTransProb, disTransDistMax);
        }
    }
    
    /**
     * 
     * @param transDist
     */
    public void setMaxDiseaseTransmissionDistance(double transDist) {
        if (transDist < 0.0) {
            disTransDistMax = 0.0;
        } else if (transDist > proxNetworkDistance) {
            disTransDistMax = proxNetworkDistance;
        } else {
            disTransDistMax = transDist;
        }
        diseaseGraphBeh.setTransmissionParameters(disTransProb, disTransDistMax);
    }
    
    /**
     * 
     * @return
     */
    public double getInitialProportionInfective() {
        return prInitInfect;
    }

    /**
     * 
     * @return
     */
    public double getTransmissionProbability() {
        return disTransProb;
    }
    
    public double getMaxTransmissionDistance() {
        return disTransDistMax;
    }
    
    /**
     * 
     * @param dst
     */
    public void setMaxInteractionDistance(double dst) {
        if (dst >= 0.0) {
            proxNetworkDistance = dst;
            if (disTransDistMax > proxNetworkDistance) {
                disTransDistMax = proxNetworkDistance;
                diseaseGraphBeh.setTransmissionParameters(disTransProb, disTransDistMax);
            }
            distGraphBeh.setMaximumDistance(proxNetworkDistance);
        }
    }
    
    /**
     * 
     * @return
     */
    public double getMaxInteractionDistance() {
        return proxNetworkDistance;
    }
    
    /**
     * 
     * @param nLocs
     * @param placeRandomly
     */
    public void setNumberOfInitialLocations(int nLocs) {
        if (nLocs > 0) {
            numberInitialLocations = nLocs;
            useAgentDensity = false;
            calculateDensityFromNumberAgents();
        }
    }
    
    /**
     * 
     * @param dens
     * @param placeRandomly
     */
    public void setDensityOfInitialLocations(double dens) {
        if (dens > 0.0) {
            agentDensity = dens;
            useAgentDensity = true;
            // following is based on binary landscape
            calculateNumberAgentsFromDensity();
        }
    }
    
    /**
     * 
     */
    private void calculateNumberAgentsFromDensity() {
        numberInitialLocations = (int) Math.round(agentDensity * landcoverProps[1] * rasterCellSize * rasterCellSize * ((double) rasterNumRows * rasterNumCols));
    }
    
    /**
     * 
     */
    private void calculateDensityFromNumberAgents() {
        double tmpAreaHabitat = landcoverProps[1] * rasterCellSize * rasterCellSize* ((double) rasterNumRows * rasterNumCols);
        agentDensity = ((double) numberInitialLocations) / tmpAreaHabitat;
    }
    
    /**
     * 
     * @return
     */
    public int getNumberOfAgents() {
        return numberInitialLocations;
    }
    
    /**
     * 
     * @return
     */
    public double getDensityOfAgentsPerHabitatArea() {
        return agentDensity;
    }
    
    /**
     * MAKE CONST FOR ARRAY INDEX
     * @param wt
     */
    public void setNonHabitatMoveWeight(double wt) {
        if ((wt >= 0.0) && (wt <= lcovWts[1])) {
            lcovWts[0] = wt;
        }
    }
    
    /**
     * 
     * @param cp
     */
    public void setAngleConcetrationParameter(double cp) {
        if (cp >= 0.0) {
            angleConcentration = cp;
        }
    }
    
    /**
     * 
     * @param lgLoc
     */
    public void setLogsticLocationParameter(double lgLoc) {
        logisticLocParam = lgLoc;
    }
    
    /**
     * 
     * @param lgScl
     */
    public void setLogisticScaleParameter(double lgScl) {
        logisticScaleParam = lgScl;
    }
    
    /**
     * MAKE CONST FOR ARRAY INDEX
     * @return
     */
    public double getHabitatMoveWeight() {
        return lcovWts[1];
    }
    
    /**
     * MAKE CONST FOR ARRAY INDEX
     * @return
     */
    public double getNonHabitatMoveWeight() {
        return lcovWts[0];
    }
    
    /**
     * 
     * @return
     */
    public double getAngleConcentrationParameter() {
        return angleConcentration;
    }
    
    /**
     * 
     * @return
     */
    public double getLogisticLocationParameter() {
        return logisticLocParam;
    }
    
    /**
     * 
     * @return
     */
    public double getLogisticScaleParameter() {
        return logisticScaleParam;
    }
    
    /**
     * 
     * @param fnbase
     */
    public void setOutputFileNameBase(String fnbase) {
        if (fnbase != null) {
            simOutputFilenameBase = fnbase;
        }
    }

}
