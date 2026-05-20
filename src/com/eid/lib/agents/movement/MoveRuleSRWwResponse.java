/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents.movement;

import com.eid.lib.geometricprimitives.*;
import com.eid.lib.agents.*;
import com.eid.lib.stochastic.*;

/**
 *
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class MoveRuleSRWwResponse extends MoveBehaviorRuleTemplate {
    private double deltaX, deltaY;
    private int selectedIndex = -1;
    private double[] siteProbs = null;
    private static final int[] changeRow = {0, -1, -1, -1, 0, 1, 1, 1, 0}; // E, NE, N, NW, W, SW, S, SE, 0
    private static final int[] changeCol = {1, 1, 0, -1, -1, -1, 0, 1, 0};
    private static final double[] probMod = {1.414, 1, 1.414, 1, 1.414, 1, 1.414, 1, 1.414}; // correct for diff in move distance to cells
    private double[] landcovWts = null;
    private int[] landcovTypes = null;
    
    public MoveRuleSRWwResponse(double[] lcWts, int[] lcTps) {
        siteProbs = new double[9];
        landcovWts = lcWts;
        landcovTypes = lcTps;
        if ((landcovWts == null) || (landcovTypes == null) || (landcovWts.length != landcovTypes.length)) {
            System.err.println("Warning: TestAgentMoveBehavior is not configured correctly.");
            System.exit(1);
        }
    }
    
    /**
     * Collected information from agents, objects, and fields in the agent's
     * environment.
     */
    public void senseEnvironment(Agent a) {
        //System.out.println("Test: Sensing environment...");
        if ((a != null) && (a.getSimulationReference().getIntegerRaster(0) != null)) {
            int initC = a.getSimulationReference().getIntegerRaster(0).getGridCol(a.getLocation().getX());
            int initR = a.getSimulationReference().getIntegerRaster(0).getGridRow(a.getLocation().getY());
            int tmpCellVal = a.getSimulationReference().getIntegerRaster(0).getNoDataValue();
            double testSum = 0.0;
            for (int i = 0; i < siteProbs.length; i++) {
                if (a.getSimulationReference().getIntegerRaster(0).cellIsNoData(initR + changeRow[i], initC + changeCol[i])) {
                    siteProbs[i] = 0.0;
                } else {
                    siteProbs[i] = 0.0;
                    tmpCellVal = a.getSimulationReference().getIntegerRaster(0).getCellValue(initR + changeRow[i], initC + changeCol[i]);
                    for (int j = 0; j < landcovWts.length; j++) {
                        if (tmpCellVal == landcovTypes[j]) {
                            siteProbs[i] = probMod[i] * landcovWts[j];
                            testSum += siteProbs[i];
                        }
                    }
                }
            }
            // 
            if ((testSum <= 0.0) || (Double.isNaN(testSum))) {
                System.err.println("Probability of movement for agent " + a.getAgentID() + " is 0 in all directions.");
                System.err.println("Previous selected index: " + selectedIndex);
                for (int i = 0; i < siteProbs.length; i++) {
                    System.err.println("\tr = " + changeRow[i] + ", c = " + changeCol[i] + ", pr(site) = " + siteProbs[i]);
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
            if (a.getSimulationReference().getIntegerRaster(0) != null) { // the 0 index is hard coded...
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
