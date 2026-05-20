/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents.movement;

import com.eid.lib.geometricprimitives.*;
import com.eid.lib.agents.graph.contact.*;
import com.eid.lib.agents.*;
import com.eid.lib.stochastic.*;

/**
 *
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class MoveRuleConSpwResponse extends MoveBehaviorRuleTemplate {
    private PointIn2D neighbLoc = null;
    private double preferredAngle = 0.0;
    private double angleWeightParamMax = 2.0;
    private double angleWeightParamInfl = 1088.2; // mean bobcat HR radius = 1088.2 m
    private double angleWeightParamSlp = 0.5;
    private double angleWeightParamCurr = 0.0;
    private boolean attractive = true;
    private double deltaX, deltaY;
    private int selectedIndex = -1;
    // 
    private static final int[] changeRow = {0, -1, -1, -1, 0, 1, 1, 1};
    private static final int[] changeCol = {1, 1, 0, -1, -1, -1, 0, 1};
    private static final double[] siteAngles = {0.00 * Math.PI, 0.25 * Math.PI, 0.50 * Math.PI, 0.75 * Math.PI, 1.00 * Math.PI, 1.25 * Math.PI, 1.50 * Math.PI, 1.75 * Math.PI};
    private static final double[] probMod = {1.414, 1, 1.414, 1, 1.414, 1, 1.414, 1}; // correct for diff in move distance to cells
    // array to store probability values for angle, habitat, and site pref values
    private double[] anglePref = null;
    private double[] habitatPref = null;
    private double[] siteProbs = null;
    // 
    private double[] landcovWts = null;
    private int[] landcovTypes = null;
    
    public MoveRuleConSpwResponse(double[] lcWts, int[] lcTps, double parMx, double parInf, double parSlp, boolean isAttr) {
        preferredAngle = siteAngles[RandomNumberGenerator.drawInteger(siteAngles.length)];
        // should check the following
        angleWeightParamMax = parMx;
        angleWeightParamInfl = parInf;
        angleWeightParamSlp = parSlp;
        attractive = isAttr;
        siteProbs = new double[8];
        landcovWts = lcWts;
        landcovTypes = lcTps;
        if ((landcovWts == null) || (landcovTypes == null) || (landcovWts.length != landcovTypes.length)) {
            System.out.println("Warning: TestAgentMoveBehavior is not configured correctly.");
        }
    }
    
    /**
     * Collected information from agents, objects, and fields in the agent's
     * environment.
     */
    public void senseEnvironment(Agent a) {
        //System.out.println("Test: Sensing environment...");
        if ((a != null) && (a.getSimulationReference().getIntegerRaster(0) != null)) {
            double tmpDist, dx, dy, tmpExp, tmpWt, tmpCos, tmpSin;
            ContactGraphEdgeData tmpED = null;
            angleWeightParamCurr = 0.0;
            tmpCos = 0.0;
            tmpSin = 0.0;
            int numNeighb = 0;
            int angleCount = 0;
            if ((a.getNodeData(0) != null) && (a.getNodeData(0) instanceof ContactGraphNodeData)){
                numNeighb = a.getNodeData(0).getNodeReference().getNodeDegreeOut();
            }
            // compute preferred angle and concentration parameter
            if (numNeighb > 0) {
                for (int i = 0; i < numNeighb; i++) {
                    if ((a.getNodeData(0).getNodeReference() != null) && 
                            (a.getNodeData(0).getNodeReference().getEdgeOut(i) != null) && 
                            (a.getNodeData(0).getNodeReference().getEdgeOut(i).getEdgeData() instanceof ContactGraphEdgeData) &&
                            (a.getNodeData(0).getNodeReference().getEdgeOut(i).getToNode().getNodeData() != null) &&
                            (a.getNodeData(0).getNodeReference().getEdgeOut(i).getToNode().getNodeData().getLocation() != null)
                            ) {
                        neighbLoc = a.getNodeData(0).getNodeReference().getEdgeOut(i).getToNode().getNodeData().getLocation();
                        tmpED = (ContactGraphEdgeData)a.getNodeData(0).getNodeReference().getEdgeOut(i).getEdgeData();
                        if (attractive) {
                            dx = tmpED.getDeltaX();
                            dy = tmpED.getDeltaY();
                        } else {
                            dx = -tmpED.getDeltaX();
                            dy = -tmpED.getDeltaY();
                        }
                        tmpDist = tmpED.getDistance();
                        //System.out.println("dx = " + dx + ", dy = " + dy + ", dist = " + tmpDist); // FOR TESTING
                        
                        if (tmpDist > 0.0) {
                            preferredAngle = Math.atan2(dy, dx);
                            angleCount++;
                            //
                            tmpExp = Math.exp(angleWeightParamSlp * (tmpDist - angleWeightParamInfl));
                            if (Double.isInfinite(tmpExp)) {
                                tmpExp = Double.MAX_VALUE;
                            }
                            tmpWt = 0.99 * angleWeightParamMax * tmpExp / (1.0 + tmpExp) + 0.01;
                            if (Double.isInfinite(tmpWt)) {
                                tmpWt = angleWeightParamMax;
                            }
                            tmpWt = angleWeightParamMax - tmpWt;
                            tmpCos += tmpWt * Math.cos(preferredAngle);
                            tmpSin += tmpWt * Math.sin(preferredAngle);
                        }
                    } // end check if refs not null
                } // end neighbor loop
            }
            // if no neighbors, or all were at the same location as agent
            if ((numNeighb == 0) || (angleCount == 0)) {
                preferredAngle = 2.0 * Math.PI * RandomNumberGenerator.drawDouble();
                tmpCos = angleWeightParamMax * Math.cos(preferredAngle);
                tmpSin = angleWeightParamMax * Math.sin(preferredAngle);
            }
            preferredAngle = Math.atan2(tmpSin, tmpCos);
            angleWeightParamCurr = Math.sqrt(tmpCos*tmpCos + tmpSin*tmpSin);
            
            int initC = a.getSimulationReference().getIntegerRaster(0).getGridCol(a.getLocation().getX());
            int initR = a.getSimulationReference().getIntegerRaster(0).getGridRow(a.getLocation().getY());
            int tmpCellVal = a.getSimulationReference().getIntegerRaster(0).getNoDataValue();

            // assign relative probabilities to eight neighboring cells
            double testSum = 0.0, angPrSum = 0.0, angExpArg = 0.0;
            for (int i = 0; i < (siteProbs.length - 1); i++) {
                // set all tmp to zero
                anglePref[i] = 0.0; habitatPref[i] = 0.0; siteProbs[i] = 0.0;
                // compute angle preference
                angExpArg = angleWeightParamCurr * (Math.cos(siteAngles[i] - preferredAngle) - 1.0);
                anglePref[i] = Math.exp(angExpArg);
                angPrSum += anglePref[i];
                // compute habitat preference
                tmpCellVal = a.getSimulationReference().getIntegerRaster(0).getCellValue(initR + changeRow[i], initC + changeCol[i]);
                if (tmpCellVal != a.getSimulationReference().getIntegerRaster(0).getNoDataValue()) {
                    for (int j = 0; j < landcovWts.length; j++) {
                        if (tmpCellVal == landcovTypes[j]) {
                            habitatPref[i] = landcovWts[j];
                        }
                    }
                }
                // compute final relative probability
                siteProbs[i] = probMod[i] * anglePref[i] * habitatPref[i];
                testSum += siteProbs[i];
            }
            // set the relative probability of non-movement (stay in current cell)
            if (angPrSum > 0.0) {
                //
                anglePref[siteProbs.length - 1] = 0.0; habitatPref[siteProbs.length - 1] = 0.0; siteProbs[siteProbs.length - 1] = 0.0;
                //
                anglePref[siteProbs.length - 1] = angPrSum/((double)(siteProbs.length - 1));
                //
                tmpCellVal = a.getSimulationReference().getIntegerRaster(0).getCellValue(initR, initC);
                for (int j = 0; j < landcovWts.length; j++) {
                    if (tmpCellVal == landcovTypes[j]) {
                        habitatPref[siteProbs.length - 1] = landcovWts[j];
                    }
                }
                siteProbs[siteProbs.length - 1] = probMod[siteProbs.length - 1] * habitatPref[siteProbs.length - 1] * anglePref[siteProbs.length - 1];
                testSum += siteProbs[siteProbs.length - 1];
            }
            // 
            if ((testSum <= 0.0) || (Double.isNaN(testSum))) {
                System.err.println("Probability of movement for agent " + a.getAgentID() + " is 0 in all directions.");
                System.err.println("Preferred angle: " + preferredAngle);
                System.err.println("Concentration parameter: " + angleWeightParamCurr);
                System.err.println("Previous selected index: " + selectedIndex);
                for (int i = 0; i < siteProbs.length; i++) {
                    System.err.println("\tr = " + changeRow[i] + ", c = " + changeCol[i] + ", pr(a) = " + anglePref[i] + ", pr(h) = " + habitatPref[i] + ", pr(site) = " + siteProbs[i]);
                }
                System.exit(1);
            }
        }
    }
    
    /**
     * In some models, this step may also include generating anticipated outcomes
     * for each decision rule, and then selecting the rule upon which the decision
     * will be made based on the anticipated outcome.
     * 
     * In some models, this method might be implemented as a point in which
     * participants can make decisions that are excecuted by an agent respresenting
     * them in a simulation.
     */
    public void makeDecision(Agent a, double eventTime) {
        if (a != null) {
            int initC = a.getSimulationReference().getIntegerRaster(0).getGridCol(a.getLocation().getX());
            int initR = a.getSimulationReference().getIntegerRaster(0).getGridRow(a.getLocation().getY());
            selectedIndex = RandomNumberGenerator.rSample(siteProbs);
            int newC = initC + changeCol[selectedIndex];
            int newR = initR + changeRow[selectedIndex];
            if (a.getSimulationReference().getIntegerRaster(0) != null) {
                deltaX = a.getSimulationReference().getIntegerRaster(0).getXcoordFromCol(newC);
                deltaY = a.getSimulationReference().getIntegerRaster(0).getYcoordFromRow(newR);
            } else {
                System.err.println("Reference to land cover layer is null.");
                System.exit(1);
            }
        }
    }
    
    /**
     * This method notifies the agent to update its state without going through
     * the SimulationEventQueue, and provides the data from this class to do so.
     */
    public void updateState(Agent a, double eventTime) {
        a.setLocation(new PointIn2D(deltaX, deltaY));
    }
    
}
