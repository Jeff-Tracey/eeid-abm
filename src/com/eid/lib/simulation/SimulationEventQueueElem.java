/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.simulation;

/**
 * A linked-list element containing a simulation event in the queue.
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class SimulationEventQueueElem implements Comparable {
    private SimulationEventQueueElem prevElem = null;
    private SimulationEventQueueElem nextElem = null;
    private SimulationEvent event = null;
    
    /**
     * 
     */
    public SimulationEventQueueElem() {
        prevElem = null;
        nextElem = null;
        event = null;
    }
    
    /**
     * 
     * @param e
     */
    public SimulationEventQueueElem(SimulationEvent e) {
        prevElem = null;
        nextElem = null;
        event = e;
    }
    
    /**
     * 
     * @param n
     * @param e
     */
    public SimulationEventQueueElem(SimulationEventQueueElem p, SimulationEventQueueElem n, SimulationEvent e) {
        prevElem = p;
        nextElem = n;
        event = e;
    }
    
    /**
     * 
     * @return the reference to the simulation event.
     */
    public SimulationEvent getEvent() {
        return event;
    }
    
    /**
     * 
     * @return the reference to the previous linked-list element.
     */
    public SimulationEventQueueElem getPreviousElement() {
        return prevElem;
    }
    
    /**
     * 
     * @return the reference to the next linked-list element.
     */
    public SimulationEventQueueElem getNextElement() {
        return nextElem;
    }
    
    /**
     * 
     * @param e
     */
    public void setEvent(SimulationEvent e) {
        event = e;
    }
    
    /**
     * 
     * @param n
     */
    public void setPreviousElement(SimulationEventQueueElem p) {
        prevElem = p;
    }
    
    /**
     * 
     * @param n
     */
    public void setNextElement(SimulationEventQueueElem n) {
        nextElem = n;
    }
    
    /**
     * Compares SimulationEventQueueElem to see who goes first in queue.  A
     * lower value (-1) means that this object should go first.  Note that if
     * a random ordering is desired for events that occur at the same time,
     * the event prioirty should be assigned to accomplsih this.
     * @param s
     * @return
     */
    public int compareTo(Object obj) throws ClassCastException {
        if (obj instanceof SimulationEventQueueElem) {
            SimulationEventQueueElem s = (SimulationEventQueueElem) obj;
            if (event.getTime() < s.getEvent().getTime()) {
                return -1;
            } else if (event.getTime() > s.getEvent().getTime()) {
                return 1;
            } else {
                if (event.getPriority() > s.getEvent().getPriority()) {
                    return -1; // a lower value means you go first
                } else if (event.getPriority() < s.getEvent().getPriority()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } else {
            throw new ClassCastException();
        }
    }
}
