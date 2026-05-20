/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.gridindex;

import java.util.*;

import com.eid.lib.geometricprimitives.*;
import com.eid.lib.stochastic.*;

/**
 *
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class GridIndex {
    private BoxIn2D spaceMBB = null;
    private int numRowCol = 0;
    private double cellSizeX, cellSizeY;
    private GridIndexCell[] cells = null;
    
    /**
     * THIS REQUIRES SOME DEFINITE CHECKING OF ARGS...
     * @param b
     * @param nrc
     */
    public GridIndex(BoxIn2D b, int nrc) {
        spaceMBB = b;
        numRowCol = nrc;
        cellSizeX = spaceMBB.getWidth()/((double)numRowCol);
        cellSizeY = spaceMBB.getHeight()/((double)numRowCol);
        cells = new GridIndexCell[numRowCol*numRowCol];
        int k;
        double xmn, xmx, ymn, ymx;
        for (int r = 0; r < numRowCol; r++) {
            for (int c = 0; c < numRowCol; c++) {
                k = getIndexFromRowCol(r, c);
                xmn = ((double)r)*cellSizeX + spaceMBB.getXmin();
                xmx = ((double)(r + 1))*cellSizeX + spaceMBB.getXmin();
                ymn = ((double)(numRowCol - c - 1))*cellSizeY  + spaceMBB.getYmin();
                ymx = ((double)(numRowCol - c))*cellSizeY  + spaceMBB.getYmin();
                cells[k] = new GridIndexCell(xmn, ymn, xmx, ymx);
            }
        }
    }
    
    public void insert(SpatialAccessMethods o) {
        if (spaceMBB.intersectsBox(o.getBoundingBox())) {
            // get the range of cells covered by the object's MBB
            int rMin = getRowFromYCoord(o.getBoundingBox().getYmax());
            if (rMin < 0) {
                rMin = 0;
            }
            int rMax = getRowFromYCoord(o.getBoundingBox().getYmin());
            if ((rMax >= numRowCol) || (rMax == -1)) {
                rMax = numRowCol - 1;
            }
            int cMin = getColFromXCoord(o.getBoundingBox().getXmin());
            if (cMin < 0) {
                cMin = 0;
            }
            int cMax = getColFromXCoord(o.getBoundingBox().getXmax());
            if ((cMax >= numRowCol) || (cMax == -1)) {
                cMax = numRowCol - 1;
            }
            // System.out.println("rMin = " + rMin + ", rMax = " + rMax + ", cMin = " + cMin + ", cMax = " + cMax);
            int k;
            for (int r = rMin; r <= rMax; r++) {
                for (int c = cMin; c <= cMax; c++) {
                    k = getIndexFromRowCol(r, c);
                    cells[k].addCellObject(o);
                }
            }
        //
        }
    }
    
    public void clearAllObjects() {
        for (int i = 0; i < cells.length; i++) {
            cells[i].removeAllObjects();
        }
    }
    
    private int getColFromXCoord(double x) {
        int c = -1;
        if ((x >= spaceMBB.getXmin()) && (x <= spaceMBB.getXmax())) {
            c = (int)(Math.floor((x - spaceMBB.getXmin())/cellSizeX)); //
        }
        return c;
    }
    
    private int getRowFromYCoord(double y) {
        int r = -1;
        if ((y >= spaceMBB.getYmin()) && (y <= spaceMBB.getYmax())) {
            r = (int)Math.floor((double)numRowCol - (y - spaceMBB.getYmin())/cellSizeY); // if cell 0,0 is in lower left corner
        }
        return r;
    }
    
    private int getIndexFromRowCol(int r, int c) {
        int k = r*numRowCol + c;
        return k;
    }
    
    private int getIndexFromCoords(double x, double y) {
        return getIndexFromRowCol(getRowFromYCoord(y), getColFromXCoord(x));
    }
    
    /**
     * 
     * @param p
     * @return
     */
    public ArrayList<SpatialAccessMethods> getObjectsContainingPoint(PointIn2D p) {
        ArrayList<SpatialAccessMethods> res = null;
        if (p != null) {
            int k = getIndexFromCoords(p.getX(), p.getY());
            // may want to check r, c first
            if ((k >= 0) && (k < cells.length)) {
                int nObj = cells[k].getNumberOfObjectsInCell();
                if (nObj > 0) {
                    res = new ArrayList<SpatialAccessMethods>();
                    for (int i = 0; i < nObj; i++) {
                        if (cells[k].cellObjectContainsPoint(i, p)) {
                            res.add(cells[k].getCellObject(i));
                        }
                    }
                    if (res.size() == 0) {
                        res = null;
                    }
                }
            } else {
                System.out.println("Warning in GridIndex.getObjectsContainingPoint() - index out of bounds.");
                System.out.println("\t" + p.toString());
            }
        }
        return res;
    }
    
    public void display() {
        int k;
        int totalObjects = 0;
        System.out.println("Grid Index Object:");
        for (int r = 0; r < numRowCol; r++) {
            for (int c = 0; c < numRowCol; c++) {
                k = getIndexFromRowCol(r, c);
                System.out.println(r + ", " + c + " (" + k + ") has " + cells[k].getNumberOfObjectsInCell() + " objects");
                totalObjects += cells[k].getNumberOfObjectsInCell();
            }
        }
        System.out.println("There are " + totalObjects + " total objects stored in the grid index.");
    }
    
    /**
     * Test
     * @param args
     */
    public static void main(String[] args) {
        double x_min = -15.0;
        double y_min = -10.0;
        double x_max = 15.0;
        double y_max = 10.0;
        double rad = 2.0;
        double tmp_x, tmp_y;
        int numTests = 40;
        GridIndex testGrid = new GridIndex(new BoxIn2D(x_min, y_min, x_max, y_max), 5);
        ArrayList<TestCellObject> testObjs = new ArrayList<TestCellObject>();
        for (int i = 0; i < numTests; i++) {
            tmp_x = (x_max - x_min)*RandomNumberGenerator.drawDouble() + x_min;
            tmp_y = (y_max - y_min)*RandomNumberGenerator.drawDouble() + y_min;
            testObjs.add(new TestCellObject(new PointIn2D(tmp_x, tmp_y), rad, i));
        }
        for (int i = 0; i < testObjs.size(); i++) {
            testGrid.insert(testObjs.get(i));
        }
        ArrayList<SpatialAccessMethods> resObjs = null;
        TestCellObject tmpObj = null;
        for (int i = 0; i < testObjs.size(); i++) {
            System.out.println("object " + i + " has location:");
            testObjs.get(i).getLocation().display();
            resObjs = testGrid.getObjectsContainingPoint(testObjs.get(i).getLocation());
            for (int k = 0; k < resObjs.size(); k++) {
                tmpObj = (TestCellObject)resObjs.get(k);
                if (tmpObj != testObjs.get(i)) {
                    System.out.println("object " + i + " intersects with object " + tmpObj.getDataID());
                    tmpObj.getLocation().display();
                }
            }
        }
    }
}
