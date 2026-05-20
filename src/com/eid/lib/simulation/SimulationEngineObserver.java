/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.simulation;

/**
 *
 * @author  Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public interface SimulationEngineObserver {
    /**
     * We rely on the observer to pull the information it needs from the subject
     * using the SimulationEngineBehavior interface methods.
     * @param seb
     * @param ev
     */
    public void update(SimulationEngineBehavior seb, SimulationEngineObserverEvent ev);
    
    /**
     * This method is provided in case the observer needs a reference to the
     * subject.  For example, the observer could call the detachObserver()
     * method on itself if the observer is going to be deleted so we don't
     * end up with a memory leak or control when it does and does not get
     * updates.
     * @param seb
     */
    public void setSubject(SimulationEngineBehavior seb);
}
