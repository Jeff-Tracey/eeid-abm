/*******************************************************************************
Title:           <input>
Files:           <input>

Author:          Jeff Tracey
CS Login:        jtracey
Collaborators:   None

Due Date:        Monday, April 29, 2002
Completion Date: <input>

Course:          CS367, Spring 2002, Lecture 4
Instructor:      Peterson
Assignment:      Program 5 

Editor:          CodeWarrior on Windows/vi on Unix
Compiler:        CodeWarrior on Windows/javac on Unix
Platform:        Windows 2000/Unix (Solaris)
*******************************************************************************/

package com.eid.lib.sorting;

/**
 * A SortObject is essentially an integer wrapper class that keeps track of
 * the number of comparisons that are done on SortObjects.
 *
 * @author Rebecca Hasti (hasti@cs.wisc.edu),
 *         copyright 2001, all rights reserved
 **/
public class SortObject implements Comparable {
    private static int compares = 0; // the comparison counter
    private int data;                // data for each SortObject object
    
    /**
     * Resets the comparison counter to 0.
     **/
    static void resetCompares() { compares = 0; }


    /**
     * Returns the value of the comparison counter.
     * @return the value of the comparison counter
     **/
    static int getCompares() { return compares; }


    /**
     * Creates a new SortObject with the given value.
     * @param data the integer value for this object
     **/
    SortObject(int data) { this.data = data; }


    /**
     * Returns the integer data value for this SortObject.
     * @return the integer data value for this SortObject
     **/
    public int getData() { return data; }


    /**
     * Compares this SortObject to the one given.  If the given
     * object is not a SortObject, a ClassCastException is thrown.
     * If it is, the comparison is done and the comparison counter
     * is incremented.
     *
     * @param obj the item to compare to
     * @return < 0, 0, > 0 depending on whether this SortObject is
     *         less than, equal to, or greater than the one given
     **/
    public int compareTo(Object obj) {
        int otherData = ((SortObject)obj).getData();
        compares++;
        if (data == otherData)     return 0;
        else if (data < otherData) return -1;
        else                       return 1;
    }


    /**
     * Returns true if the given SortObject has the same integer
     * data value as this one.
     * @return true if the integer data values are the same
     **/
    public boolean equals(SortObject obj) { return obj.getData() == data; }


    /**
     * Returns a String representation, in this case a String 
     * containing the integer data value.
     * @return a String representation suitable for printing
     **/
    public String toString() { return "" + data; }
}
