/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.simulation;

/**
 * Command object for a command pattern for a simulation event prioity queue
 * @author  Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public interface SimulationEvent {
    public void execute(); /** execute the event. */
    public void cancel(); /** do not excute, but take care of any loose ends. */
    public double getTime(); /** get event time so it can be placed in the queue. */
    public int getPriority(); /** get event priority so it can be placed in the queue. */
    public void setEventTime(double t); /** set the time the event occurs */
}
