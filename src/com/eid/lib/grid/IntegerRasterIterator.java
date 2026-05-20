/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.grid;

import java.util.*;

import com.eid.lib.geometricprimitives.*;
import com.eid.lib.vectormodels.*;

/**
 *
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
class IntegerRasterIterator implements RasterIterator {
    private boolean usesList; /** true if uses list of RasterCellIndex, false if uses index range */
    private RasterCellIndex upperLeftCell = null;
    private RasterCellIndex lowerRightCell = null;
    private RasterCellIndex currentCell = null;
    private ArrayList<RasterCellIndex> indexList = null;
    private int currentIndex;
    
    public boolean hasNext() {
        if (usesList) {
            if (currentIndex < indexList.size()) {
                return true;
            } else {
                return false;
            }
        } else {
            if (lowerRightCell.compareTo(currentCell) >= 0) {
                return true;
            } else {
                return false;
            }
        }
    }
    
    public RasterCellIndex next() {
        RasterCellIndex res = null;
        if (usesList) {
            res = indexList.get(currentIndex);
            if (hasNext()) {
                currentIndex++;
            }
        } else {
            res = new RasterCellIndex(currentCell.getRow(), currentCell.getCol());
            if (hasNext()) {
                if (currentCell.getCol() < lowerRightCell.getCol()) {
                    currentCell.incrementCol();
                } else {
                    currentCell.incrementRow();
                    currentCell.setCol(upperLeftCell.getCol());
                }
            }
        }
        return res;
    }
    
    public void start() {
        if (usesList) {
            currentIndex = 0;
        } else {
            currentCell.setCol(upperLeftCell.getCol());
            currentCell.setRow(upperLeftCell.getRow());
        }
    }
    
    @Override public String toString() {
        String res = "";
        if (usesList) {
            for (int i = 0; i < indexList.size(); i++) {
                res += i + "\t" + indexList.get(i).toString();
            }
        } else {
            res += "Upper Left Cell Index:\n\t" + upperLeftCell.toString() + "Lower Right Cell Index:\n\t" + lowerRightCell.toString();
        }
        return res;
    }
}
