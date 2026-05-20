/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.app.diseasesim;

import java.io.*;
import java.nio.*;

import com.eid.lib.simulation.*;

/**
 * This program iterates through specified parameter values, runs the disease
 * simulation, and write output to files.
 * 
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class DiseaseSim01 implements SimulationEngineObserver {
    // fixed parameters
    private int numberOfTrials;
    private double timeStepDuration;
    private double maxTime;
    private double agentDensityInPatch;
    private double maxDiseaseTransmissionDist;
    private double initialProbabilityInfective;
    
    // variable parameters
    private double[] hurstExponents = null; /** Hurst exponent */
    private double[] propHabitat = null; /** proportion habitat */
    private int[] moveModelNum = null; /** move rule index */
    private double[] moveModelParam = null; /** move rule variable parameter */
    private double[] nonhabitatPref = null; /** non-habitat preference; habitatPref = 1.0 always */
    private double[] diseaseTransProb = null; /** disease transmission prob */
    
    //
    private int totalNumberOfParameterCombos;
    private int timeStepCount;
    
    /**
     * 
     */
    public DiseaseSim01() {
        //
        numberOfTrials = 500;
        timeStepDuration = 0.0208333;
        maxTime = 200.0;
        agentDensityInPatch = 0.00000035;
        maxDiseaseTransmissionDist = 50.0;
        initialProbabilityInfective = 0.01; // run with 0.01 and 0.05
        //
        timeStepCount = 0; //
        
        // ---------------------------------------------------------------------
        // Set up simulation parameter combinations
        // ---------------------------------------------------------------------
        double[] hurst = {0.1, 0.3, 0.5, 0.7, 0.9};                        // 5
        double[] hab = {0.1, 0.3, 0.5, 0.7, 0.9};                          // 5
        int[] moveNum = {1, 1, 1, 1, 3, 3, 3};                             // 7
        double[] movePar = {0.0, 2.0, 5.0, 10.0, 500.0, 1000.0, 1500.0};
        double[] nonhab = {0.0, 0.01, 0.05, 1.0};                          // 4
        double[] trans = {0.01, 0.1, 0.3333};                              // 3
        //
        totalNumberOfParameterCombos = hurst.length * hab.length * 
                moveNum.length * nonhab.length * trans.length;          // 2100
        //
        hurstExponents = new double[totalNumberOfParameterCombos]; //    H
        propHabitat = new double[totalNumberOfParameterCombos]; //    p
        moveModelNum = new int[totalNumberOfParameterCombos];
        moveModelParam = new double[totalNumberOfParameterCombos];
        nonhabitatPref = new double[totalNumberOfParameterCombos];
        diseaseTransProb = new double[totalNumberOfParameterCombos];
        //
        int parIndex = 0;
        for (int p1 = 0; p1 < hurst.length; p1++) {
            for (int p2 = 0; p2 < hab.length; p2++) {
                for (int p3 = 0; p3 < moveNum.length; p3++) {
                    for (int p4 = 0; p4 < nonhab.length; p4++) {
                        for (int p5 = 0; p5 < trans.length; p5++) {
                            hurstExponents[parIndex] = hurst[p1];
                            propHabitat[parIndex] = hab[p2];
                            moveModelNum[parIndex] = moveNum[p3];
                            moveModelParam[parIndex] = movePar[p3];
                            nonhabitatPref[parIndex] = nonhab[p4];
                            diseaseTransProb[parIndex] = trans[p5];
                            parIndex++;
                        }
                    }
                }
            }
        }
    }
    
    // getters...
    
    public int getNumberOfTrials() {
        return numberOfTrials;
    }
    
    public double getTimeStepDuration() {
        return timeStepDuration;
    }
    
    public double getMaxTime() {
        return maxTime;
    }  
    
    public int getNumberParameterCombinations() {
        return totalNumberOfParameterCombos;
    }
    
    // get parameters for combination i...
    
    public double getHurstExponent(int i) {
        double res = Double.NaN;
        if ((i >= 0) && (i < totalNumberOfParameterCombos)) {
            res = hurstExponents[i];
        }
        return res;
    }
    
    public double getProportionHabitat(int i) {
        double res = Double.NaN;
        if ((i >= 0) && (i < totalNumberOfParameterCombos)) {
            res = propHabitat[i];
        }
        return res;
    }
    
    public int getMoveModelNumber(int i) {
        int res = -1;
        if ((i >= 0) && (i < totalNumberOfParameterCombos)) {
            res = moveModelNum[i];
        }
        return res;
    }
    
    public double getMoveModelParameter(int i) {
        double res = Double.NaN;
        if ((i >= 0) && (i < totalNumberOfParameterCombos)) {
            res = moveModelParam[i];
        }
        return res;
    }
    
    public double getNonHabitatPreference(int i) {
        double res = Double.NaN;
        if ((i >= 0) && (i < totalNumberOfParameterCombos)) {
            res = nonhabitatPref[i];
        }
        return res;
    }
    
    public double getDiseaseTransmissionProbability(int i) {
        double res = Double.NaN;
        if ((i >= 0) && (i < totalNumberOfParameterCombos)) {
            res = diseaseTransProb[i];
        }
        return res;
    }
    
    // GET FIXED PARAMETERS...
    
    public double getAgentDensityInPatches() {
        return agentDensityInPatch;
    }
    
    public double getMaxDiseaseTransmissionDistance() {
        return maxDiseaseTransmissionDist;
    }
    
    public double getInitialProbabilityInfective() {
        return initialProbabilityInfective;
    }
    
    
    public String parameterHeaderToHTMLtable() {
        String res = "<tr><th>baseName</th><th>" +
        "numTrials</th><th>" +
        "timeStepDays</th><th>" +
        "maxTimeDays</th><th>" +
        "agentDens</th><th>" +
        "maxTransDist</th><th>" +
        "initProbInf</th><th>" +
        "hurstExp</th><th>" +
        "propHab</th><th>" +
        "moveModel</th><th>" +
        "moveParam</th><th>" +
        "nonhabPref</th><th>" +
        "disTransProb</th></tr>\n";
        return res;
    }
    
    public String parameterHeaderToTabDelim() {
        String res ="baseName\t" +
        "numTrials\t" +
        "timeStepDays\t" +
        "maxTimeDays\t" +
        "agentDens\t" +
        "maxTransDist\t" +
        "initProbInf\t" +
        "hurstExp\t" +
        "propHab\t" +
        "moveModel\t" +
        "moveParam\t" +
        "nonhabPref\t" +
        "disTransProb\n";
        return res;
    }
    
    public String parameterCombinationToHTML(int i, String baseFN) {
        // add check baseFN != null
        String res = "<tr>";
        if ((i >= 0) && (i < totalNumberOfParameterCombos)) {
            res += "<td>" + baseFN + "</td>";
            res += "<td>" + numberOfTrials + "</td>";
            res += "<td>" + timeStepDuration + "</td>";
            res += "<td>" + maxTime + "</td>";
            res += "<td>" + agentDensityInPatch + "</td>";
            res += "<td>" + maxDiseaseTransmissionDist + "</td>";
            res += "<td>" + initialProbabilityInfective + "</td>";
            res += "<td>" + hurstExponents[i] + "</td>";
            res += "<td>" + propHabitat[i] + "</td>";
            res += "<td>" + moveModelNum[i] + "</td>";
            res += "<td>" + moveModelParam[i] + "</td>";
            res += "<td>" + nonhabitatPref[i] + "</td>";
            res += "<td>" + diseaseTransProb[i] + "</td>";
        }
        res += "</tr>\n";
        return res;
    }
    
    public String parameterCombinationToTabDelim(int i, String baseFN) {
        // add check baseFN != null
        String res = "";
        if ((i >= 0) && (i < totalNumberOfParameterCombos)) {
            res += baseFN + "\t";
            res += numberOfTrials + "\t";
            res += timeStepDuration + "\t";
            res += maxTime + "\t";
            res += agentDensityInPatch + "\t";
            res += maxDiseaseTransmissionDist + "\t";
            res += initialProbabilityInfective + "\t";
            res += hurstExponents[i] + "\t";
            res += propHabitat[i] + "\t";
            res += moveModelNum[i] + "\t";
            res += moveModelParam[i] + "\t";
            res += nonhabitatPref[i] + "\t";
            res += diseaseTransProb[i];
        }
        res += "\n";
        return res;
    }
    
    /**
     * We rely on the observer to pull the information it needs from the subject
     * using the SimulationEngineBehavior interface methods.
     * @param seb
     * @param ev
     */
    public void update(SimulationEngineBehavior seb, SimulationEngineObserverEvent ev) {
        if ((seb != null) && (ev != null)) {
            switch (ev) {
                case ADVANCE_TIME_START:
                    // System.out.println("\tadvance time start.  Number of agents = " + seb.getGraph(0).getNumberOfNodes()); // TEMP
                    timeStepCount++;
                    break;
                case ADVANCE_TIME_FINISH:
                    if (timeStepCount % 1000 == 0) {
                        System.out.println("\tCurrent time = " + seb.getCurrentTime()); // TEMP
                    }
                    break;
                case REALIZATION_START:
                    System.out.println("Current realzation = " + seb.getCurrentRealizationNumber()); // TEMP
                    timeStepCount = 0;
                    break;
                case REALIZATION_FINISH:
                    // System.out.println("realization finish..."); // TEMP
                    break;
                case SIMULATION_START:
                    System.out.println("simulation start..."); // TEMP
                    timeStepCount = 0;
                    break;
                case SIMULATION_FINISH:
                    System.out.println("simulation finish..."); // TEMP
                    break;
                case OUTPUTS_READY:
                    System.out.println("output ready..."); // TEMP
                    break;
                default:
                    //
            }
        }
    }
    
    /**
     * This method is provided in case the observer needs a reference to the
     * subject.  For example, the observer could call the detachObserver()
     * method on itself if the observer is going to be deleted so we don't
     * end up with a memory leak or control when it does and does not get
     * updates.
     * @param seb
     */
    public void setSubject(SimulationEngineBehavior seb) {
        //
    }
    
    // within this class, we are going to create an output file (a) with parameters
    // and a simulation number ID and (b) with patch transmission stats
    
    // within the simulation engine we are going to write output files for (a)
    // time series and (b) samples of local landscape metrics and contacts and
    // transmission events
    
    public static void main(String[] args) {
        // create new sim main object
        DiseaseSim01 simObj = new DiseaseSim01();
        
        // create new disease simulation engine
        DiseaseSim01engine dSim = new DiseaseSim01engine(simObj.getNumberOfTrials(), simObj.getTimeStepDuration(), simObj.getMaxTime());
        dSim.attachObserver(simObj);
        
        // set fixed parameters
        dSim.setDensityOfInitialLocations(simObj.getAgentDensityInPatches()); // 0.35 * 10-6 bcats / m2
        dSim.setLandscapeParameters(0.01, 0.35);
        dSim.setMaxInteractionDistance(simObj.getMaxDiseaseTransmissionDistance()); // 200.0
        dSim.setDiseaseParameters(simObj.getInitialProbabilityInfective(), 0.0, 
                simObj.getMaxDiseaseTransmissionDistance());
        
        // command line args for first and last index of parameter combos to run...
        int pStart = 0, pFinish = 0;
        if (args.length == 2) {
            try {
                pStart = Integer.parseInt(args[0]);
                pFinish = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Argument must be an integer");
                System.exit(1);
            }
        } else {
            pStart = 0;
            // pFinish = 0; // to run a test
            pFinish = simObj.getNumberParameterCombinations() - 1; // to run all
        }
        //
        String outputFileNameBase = null;
        // open file for patch stats output
        FileWriter writer1 = null;
        try {
            writer1 = new FileWriter(String.format("%s%04d%s%04d%s", "./output/DiseaseSim-init01-PatchStatsFm", pStart, "To", pFinish, ".txt"));
            // header
            writer1.write(dSim.patchStatHeaderToTabDelimString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        // open file for parameters
        FileWriter writer2 = null;
        try {
            writer2 = new FileWriter(String.format("%s%04d%s%04d%s", "./output/DiseaseSim-init01-Paramters", pStart, "To", pFinish, ".txt"));
            // header
            writer2.write(simObj.parameterHeaderToTabDelim());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        // start timing
        long startTime = System.currentTimeMillis();
        // run simulation
        for (int p = pStart; p <= pFinish; p++) {
            // set parameters in simulation object
            dSim.setHurstExponent(simObj.getHurstExponent(p));
            dSim.setProportionHabitat(simObj.getProportionHabitat(p));
            dSim.setNonHabitatMoveWeight(simObj.getNonHabitatPreference(p));
            dSim.setMoveBehavior(simObj.getMoveModelNumber(p));
            if (simObj.getMoveModelNumber(p) == 1) {
                dSim.setAngleConcetrationParameter(simObj.getMoveModelParameter(p));
            } else if (simObj.getMoveModelNumber(p) == 3) {
                dSim.setAngleConcetrationParameter(2.0);// set concentration...
                dSim.setLogsticLocationParameter(simObj.getMoveModelParameter(p));
            }
            dSim.setDiseaseParameters(simObj.getInitialProbabilityInfective(), 
                    simObj.getDiseaseTransmissionProbability(p), 
                    simObj.getMaxDiseaseTransmissionDistance());
            
            // output file names
            outputFileNameBase = String.format("%s%04d", "./output/DiseaseSim-init01-", p);
            System.out.println("--------------------------------------------------------");
            System.out.println("****** Output filename base = " + outputFileNameBase + " ******");
            System.out.println("--------------------------------------------------------");
            dSim.setOutputFileNameBase(outputFileNameBase);
            
            // simulate
            dSim.simulate();
            
            // produce patch stats output for this sim
            try {
                writer1.write(dSim.patchStatsToTabDelimString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // write out parameter values for this sim
            try {
                 writer2.write(simObj.parameterCombinationToTabDelim(p, outputFileNameBase));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        // report run time
        long finishTime = System.currentTimeMillis();
        long runMinutes = (finishTime - startTime)/60000;
        long runSeconds = ((finishTime - startTime)%60000)/1000;
        System.out.println("The simulations ran for " + runMinutes + " minutes and " + runSeconds + " seconds.");
        
        // close the patch stats output file
        try {
            writer1.write("\n");
            if (writer1 != null) {
                writer1.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // close simulation parameters file
        try {
            writer2.write("\n");
            if (writer2 != null) {
                writer2.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
