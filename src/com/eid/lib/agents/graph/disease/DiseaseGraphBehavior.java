/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents.graph.disease;

import com.eid.lib.agents.DiseaseMoveAgent;
import java.util.*;

import com.eid.lib.agents.*;
import com.eid.lib.agents.movement.*;
import com.eid.lib.geometricprimitives.*;
import com.eid.lib.agents.graph.*;
import com.eid.lib.agents.graph.contact.*;
import com.eid.lib.grid.*;
import com.eid.lib.stochastic.*;


/**
 *
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class DiseaseGraphBehavior extends GraphBehaviorTemplate {
    // 
    // attributes
    private boolean calculateMetrics;
    // disease transmission parameters...
    private double probOfTransmission;
    private double maxTransmissionDist; // ADD TO DECOUPLE FROM PROXIMITY GRAPH DIST
    //
    private IntegerRaster patchIDref;
    //
    private double behaviorTime;
    //
    private int numNodes;
    private int numSusceptible;
    private int numInfectious;
    //
    private int numcontactsSI;
    private int numTransmissions;
    //
    private int numHomePatchTranmssions;
    private int numOtherPatchTransmissions;
    private int numOutsidePatchTransmissions;
    private double propHomePatchTranmssions;
    private double propOtherPatchTransmissions;
    private double propOutsidePatchTransmissions;
    private float meanTransmissionDistance;
    private float meanTransmissionTime;
    // arrays of locations for events...
    
    /**
     * 
     */
    public DiseaseGraphBehavior() {
        // add checks...
        probOfTransmission = 0.0;
        calculateMetrics = false;
    }
    
    /**
     * 
     * @param transRt
     * @param maxTransDist
     */
    public DiseaseGraphBehavior(double transRt, double maxTransDist) {
        // add checks...
        //ProximityNodeDataIndex = proxIndex;
        //DiseaseNodeDataIndex = disIndex;
        //
        if ((transRt >= 0.0) && (transRt <= 1.0)) {
            probOfTransmission = transRt;
        } else if (transRt > 1.0) {
            probOfTransmission = 1.0;
        } else {
            probOfTransmission = 0.0;
        }
        //
        if (maxTransDist > 0.0) {
            maxTransmissionDist = maxTransDist;
        } else {
            maxTransmissionDist = 0.0;
        }
        calculateMetrics = false;
        numSusceptible = 0;
        numcontactsSI = 0;
        numTransmissions = 0;
    }
    
    /**
     * This is a mess (it may work, but it's ugly)...
     * @param g
     */
    public void updateGraph(Graph g, double eventTime) {
        if (g != null) {
            behaviorTime = eventTime;
            numNodes = g.getNumberOfNodes();
            numSusceptible = 0;
            numcontactsSI = 0;
            numTransmissions = 0;
            int numInfectiveNeighbors = 0;
            int patchID = -1;
            DiseaseGraphNodeData focalDisNodeDat = null, otherDisNodeDat = null;
            DiseaseMoveAgent tmpAgent = null;
            double prNoInfection;
            int sourceIndex;
            
            //
            for (int n = 0; n < numNodes; n++) {
                // get disease data for focal agent n
                if (g.getNode(n).getNodeData() instanceof DiseaseGraphNodeData) {
                    focalDisNodeDat = (DiseaseGraphNodeData) g.getNode(n).getNodeData();
                    if (focalDisNodeDat.isSusceptible()) {
                        numSusceptible++;
                        // get focal agent ref
                        if (focalDisNodeDat.getAgentReference() != null) {
                            tmpAgent = (DiseaseMoveAgent)focalDisNodeDat.getAgentReference();
                            // get number of infective agents within max transmission distance
                            numInfectiveNeighbors = tmpAgent.getNumberInfectiveWithinDistance(maxTransmissionDist);
                            if (numInfectiveNeighbors > 0) {
                                // System.out.println("number infective neighbors = " + numInfectiveNeighbors);
                                numcontactsSI += numInfectiveNeighbors;
                                prNoInfection = Math.pow((1.0 - probOfTransmission), (double) numInfectiveNeighbors);
                                // System.out.println("prob of no infection = " + prNoInfection);
                                // determine if infection occurs
                                if (RandomNumberGenerator.drawDouble() > prNoInfection) {
                                    // determine from which infective
                                    sourceIndex = RandomNumberGenerator.drawInteger(numInfectiveNeighbors);
                                    // System.out.println("index of infection source agent = " + sourceIndex);
                                    otherDisNodeDat = tmpAgent.getDiseaseNodeForNthInfectiveWithinDistance(sourceIndex, maxTransmissionDist);
                                    // update disease network
                                    if (otherDisNodeDat != null) {
                                        //
                                        if (patchIDref != null) {
                                            patchID = patchIDref.getCellValue(focalDisNodeDat.getAgentReference().getLocation());
                                        } else {
                                            patchID = IntegerRasterFunctions.NULL_PATCH_ID;
                                        }
                                        focalDisNodeDat.setToInfectious(otherDisNodeDat.getTransmissionTreeID(), eventTime, focalDisNodeDat.getAgentReference().getLocation(), patchID);
                                        // create edge
                                        // System.out.println("adding edge to disease network");
                                        g.addEdge(new GraphEdge(otherDisNodeDat.getNodeReference(), focalDisNodeDat.getNodeReference(), true, new DiseaseGraphEdgeData()));
                                    }
                                    numTransmissions++;
                                }
                            }
                        } //
                    } // 
                } // 
            } // end for each node
            numInfectious = numNodes - numSusceptible;
            //System.out.println("num S = " + numSusceptible + ", num I = " + numInfectious);
        }
    }
    
    /**
     * 
     * @param transRt
     * @param maxTransDist
     */
    public void setTransmissionParameters(double transRt, double maxTransDist) {
        //
        if ((transRt >= 0.0) && (transRt <= 1.0)) {
            probOfTransmission = transRt;
        } else if (transRt > 1.0) {
            probOfTransmission = 1.0;
        } else {
            probOfTransmission = 0.0;
        }
        //
        if (maxTransDist > 0.0) {
            maxTransmissionDist = maxTransDist;
        } else {
            maxTransmissionDist = 0.0;
        }
    }
    
    /**
     * 
     * @param g
     */
    public void calculateMetrics(Graph g, double eventTime) {
        behaviorTime = eventTime;
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
    public int getNumberSusceptible() {
        return numSusceptible;
    }
    
    /**
     * 
     * @return
     */
    public int getNumberInfectious() {
        return numInfectious;
    }
    
    /**
     * 
     * @return
     */
    public int getNumberSIcontacts() {
        return numcontactsSI;
    }
    
    /**
     * 
     * @return
     */
    public float getNumberSIcontactsPerS() {
        if (numSusceptible > 0) {
            return ((float)numcontactsSI)/((float)numSusceptible);
        } else {
            return 0.0f;
        }
    }
    
    /**
     * 
     * @return
     */
    public int getNumTransmissions() {
        return numTransmissions;
    }
    
    public float getNumTransmissionsPerAgent() {
        if (numNodes > 0) {
            return ((float)numTransmissions)/((float)numNodes);
        } else {
            return 0.0f;
        }
    }
    
    /**
     * get the total infectious individuals during last network update divided 
     * by the total population size
     * @return
     */
    public float getProportionInfectious() {
        return ((float)numInfectious)/((float)numNodes); // TEMP
    }
    
    /**
     * 
     * @return Shannon's diversity index (H) for the proportion of agents in the
     * S and I states.
     */
    public float getSIentropy() {
        double propI = 0.0, propS = 0.0;
        if ((numInfectious > 0) && (numSusceptible > 0)) {
            propI = ((double)numInfectious)/((double)numNodes);
            propS = ((double)numSusceptible)/((double)numNodes);
            return -1.0f*((float)(propI*Math.log(propI) + propS*Math.log(propS)));
        } else if ((numInfectious == 0) && (numSusceptible > 0)) {
            propS = ((double)numSusceptible)/((double)numNodes);
            return -1.0f*((float)(propS*Math.log(propS)));
        } else if ((numSusceptible == 0) && (numInfectious > 0)) {
            propI = ((double)numInfectious)/((double)numNodes);
            return -1.0f*((float)(propI*Math.log(propI)));
        } else {
            return Float.NaN;
        }
    }
    
    /**
     * 
     * @return
     */
    public double getEventTime() {
        return behaviorTime;
    }
    
    /**
     * 
     * @param ir
     */
    public void setPatchIDreference(IntegerRaster ir) {
        patchIDref = ir;
    }
    
    /**
     * WHAT RESULT IF NO TRANSMISSIONS HAVE OCCURRED?  In this case, NaN is
     * set for the proportion of within, between, and outside patch transmissions.
     * This can have implications if these results are used in other computations.
     * @param g
     */
    public void calculatePatchTransmissionStatistics(Graph g) {
        if (g != null) {
            // save them in instance variables...
            int numTrees = DiseaseGraphNodeData.getNumberOfInitialInfective();
            int[][] initPatchInfo = new int[numTrees][4]; // patchID, within count, between count, outside count
            // initialize array values
            for (int i = 0; i < numTrees; i++) {
                initPatchInfo[i][0] = -1; // initialPatchID
                initPatchInfo[i][1] = 0;  // count within initial patch transmission
                initPatchInfo[i][2] = 0;  // count between patch transmission
                initPatchInfo[i][3] = 0;  // outside of patch transmission
            }
            // assign initial patchIDs for each tree
            DiseaseGraphNodeData tmpNodeData = null;
            for (int n = 0; n < g.getNumberOfNodes(); n++) {
                if ((g.getNode(n) != null) && (g.getNode(n).getNodeData() != null) && (g.getNode(n).getNodeData() instanceof DiseaseGraphNodeData)) {
                    tmpNodeData = (DiseaseGraphNodeData)g.getNode(n).getNodeData();
                    if ((tmpNodeData.startedAsInfective())  && (tmpNodeData.getLocation() != null)) {
                        initPatchInfo[tmpNodeData.getTransmissionTreeID()][0] = tmpNodeData.getTransmissionPatchID();
                    }
                }
            }
            // 
            for (int n = 0; n < g.getNumberOfNodes(); n++) {
                if ((g.getNode(n) != null) && (g.getNode(n).getNodeData() != null) && (g.getNode(n).getNodeData() instanceof DiseaseGraphNodeData)) {
                    tmpNodeData = (DiseaseGraphNodeData)g.getNode(n).getNodeData();
                    if ((!tmpNodeData.startedAsInfective()) && (tmpNodeData.getLocation() != null)) {
                        // count transmission events in each category
                        if (tmpNodeData.getTransmissionPatchID() == initPatchInfo[tmpNodeData.getTransmissionTreeID()][0]) {
                            initPatchInfo[tmpNodeData.getTransmissionTreeID()][1]++;
                        } else if (tmpNodeData.getTransmissionPatchID() == IntegerRasterFunctions.NULL_PATCH_ID) {
                            initPatchInfo[tmpNodeData.getTransmissionTreeID()][3]++;
                        } else {
                            initPatchInfo[tmpNodeData.getTransmissionTreeID()][2]++;
                        }
                    }
                }
            }
            // sum everything up
            numHomePatchTranmssions = 0;
            numOtherPatchTransmissions = 0;
            numOutsidePatchTransmissions = 0;
            
            for (int i = 0; i < numTrees; i++) {
                numHomePatchTranmssions += initPatchInfo[i][1];
                numOtherPatchTransmissions += initPatchInfo[i][2];
                numOutsidePatchTransmissions += initPatchInfo[i][3];
            }
            int totalTrans = numHomePatchTranmssions + numOtherPatchTransmissions + numOutsidePatchTransmissions;
            if (totalTrans > 0) {
                propHomePatchTranmssions = ((double)numHomePatchTranmssions)/((double)totalTrans);
                propOtherPatchTransmissions = ((double)numOtherPatchTransmissions)/((double)totalTrans);
                propOutsidePatchTransmissions = ((double)numOutsidePatchTransmissions)/((double)totalTrans);
            } else {
                propHomePatchTranmssions = Double.NaN;
                propOtherPatchTransmissions = Double.NaN;
                propOutsidePatchTransmissions = Double.NaN;
            }
            
            // DO I WANT OTHER INFO, LIKE THE RANGE, MIN, MAX, ETC. FOR EACH TREE?
            
            // the following is for testing...
            /*
            System.out.println("Patch transmission statistics:");
            System.out.println("number within patch = " + numHomePatchTranmssions);
            System.out.println("number between patch = " + numOtherPatchTransmissions);
            System.out.println("number outside patch = " + numOutsidePatchTransmissions);
            System.out.println("proportion within patch = " + propHomePatchTranmssions);
            System.out.println("proportion between patch = " + propOtherPatchTransmissions);
            System.out.println("proportion outside patch = " + propOutsidePatchTransmissions);
            */
        }
    }
    
    /**
     * WHAT RESULT IF NO TRANSMISSIONS HAVE OCCURRED?  In this case, an NaN
     * result is set for both the mean distance and time between transmissions.
     * This has implications if the results will be used in other computations.
     * @param g
     */
    public void calculateMeanTransmissionTimeAndDistance(Graph g) {
        // meanTransmissionDistance;
        // meanTransmissionTime;
        if (g != null) {
            DiseaseGraphEdgeData tmpEdgeData = null;
            float tmpEdgeCount = 0.0f;
            float tmpTimeSum = 0.0f;
            float tmpDistSum = 0.0f;
            int numEdges = g.getNumberOfEdges();
            for (int i = 0; i < numEdges; i++) {
                if ((g.getEdge(i) != null) && (g.getEdge(i).getEdgeData() != null) && (g.getEdge(i).getEdgeData() instanceof DiseaseGraphEdgeData)) {
                    tmpEdgeData = (DiseaseGraphEdgeData)g.getEdge(i).getEdgeData();
                    tmpEdgeCount += 1.0;
                    tmpTimeSum += tmpEdgeData.getElapsedTimeBetweenTransmissions();
                    tmpDistSum += tmpEdgeData.getEuclideanDistanceBetweenTransmissions();
                }
            }
            if (tmpEdgeCount > 0.0) {
                meanTransmissionDistance = tmpDistSum / tmpEdgeCount;
                meanTransmissionTime = tmpTimeSum / tmpEdgeCount;
            } else {
                meanTransmissionDistance = Float.NaN;
                meanTransmissionTime = Float.NaN;
            }
        } else {
            meanTransmissionDistance = Float.NaN;
            meanTransmissionTime = Float.NaN;
        }
        /*
        System.out.println("Transmission Statistics:");
        System.out.println("Mean time between transmission = " + meanTransmissionTime);
        System.out.println("Mean distance between transmission = " + meanTransmissionDistance);
        */
    }
    
    /**
     * 
     * @return
     */
    public int getNumberOfWithinPatchTransmissions() {
        return numHomePatchTranmssions;
    }
    
    /**
     * 
     * @return
     */
    public int getNumberOfBetweenPatchTransmissions() {
        return numOtherPatchTransmissions;
    }
    
    /**
     * 
     * @return
     */
    public int getNumberOfOutsidePatchTransmissions() {
        return numOutsidePatchTransmissions;
    }
    
    /**
     * 
     * @return
     */
    public double getProportionOfWithinPatchTransmissions() {
        return propHomePatchTranmssions;
    }
    
    /**
     * 
     * @return
     */
    public double getProportionOfBetweenPatchTransmissions() {
        return propOtherPatchTransmissions;
    }
    
    /**
     * 
     * @return
     */
    public double getProportionOfOutsidePatchTransmissions() {
        return propOutsidePatchTransmissions;
    }
    
    /**
     * 
     * @return
     */
    public float getMeanDistanceBetweenTransmissions() {
        return meanTransmissionDistance;
    }
    
    /**
     * 
     * @return
     */
    public float getMeanTimeBetweenTransmissions() {
        return meanTransmissionTime;
    }
}
