/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.agents;
import com.eid.lib.simulation.*;

/**
 * Template Pattern for agent behavior.  A concrete behavior class should
 * also implement the SimulationEvent interface and be added to the queue
 * to be executed.
 * 
 * 
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public abstract class MoveBehaviorRuleTemplate {
    // add a method to set behavior parameters from EA chromosome!!!!
    
    public final void executeBehavior(Agent a, double eventTime) {
        // 1.  sense environment
        senseEnvironment(a);        
        // 2.  set decision rule inputs from goals, state, and perceptions (a hook)
        setDecisionRuleInputs(a);
        // 3.  make decision
        makeDecision(a, eventTime);
        // 4.  update state immediately or schedule event
        updateState(a, eventTime); 
        // 5. a hook for logging events
        if (doLogBehaviorEvents()) {
            logBehaviorEvent();
        }
        
    }
    
    /**
     * Collected information from agents, objects, and fields in the agent's
     * environment.
     */
    public abstract void senseEnvironment(Agent a);
    
    /**
     * A hook for a method to transfer information on agent state, perceptions,
     * and goals to the decision rule.
     */
    public void setDecisionRuleInputs(Agent a) { /* hook */ };
    
    /**
     * In some models, this step may also include generating anticipated outcomes
     * for each decision rule, and then selecting the rule upon which the decision
     * will be made based on the anticipated outcome.
     * 
     * In some models, this method might be implemented as a point in which
     * participants can make decisions that are excecuted by an agent respresenting
     * them in a simulation.
     */
    public abstract void makeDecision(Agent a, double eventTime);
    
    /**
     * This method notifies the agent to update its state without going through
     * the SimulationEventQueue, and provides the data from this class to do so.
     */
    public abstract void updateState(Agent a, double eventTime);
    
    /**
     * 
     */
    public void logBehaviorEvent() {};
    
    /**
     * Event logger will probably a Singleton
     * @return
     */
    public boolean doLogBehaviorEvents() {
        return false;
    }
    
}
