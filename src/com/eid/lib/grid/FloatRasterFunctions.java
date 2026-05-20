/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.grid;

import java.util.*;
import java.util.Scanner;
// import java.util.Formatter;
import java.io.*;
import java.nio.*;

import com.eid.lib.geometricprimitives.*;
import com.eid.lib.vectormodels.*;
import com.eid.lib.sorting.*;
import com.eid.lib.stochastic.*;

/**
 * Static methods to create FloatRasters or apply transformations to existing
 * ones.  Also handles file IO.
 * 
 * NOTE: MUST ADD CHECKS FOR NULL ARGS...
 * 
 * I WANT TO MAKE A METHOD THAT ITERATES THROUGH ALL CELLS AND TAKES A FUNCTION
 * OBJECT (IMPLEMENTS AN INTERFACE) AS AN ARGUMENT WHICH PERFORMS OPERATIONS
 * ON THE RASTER, THEN EACH SPECIFIC METHOD ONLY HAS TO CALL THE ITERATOR
 * WITH THE FUNCTION OBJECT (VISITOR PATTERN?)
 * 
 * @author jeff
 */
public class FloatRasterFunctions {
    
    
    
    
    
    
    
    // METHOD TO WRITE TO GRASS RASTER FORMAT (SO CAN VIEW WITH QUANTUUM GIS/GRASS)
    
    /**
     * 
     * @param value
     * @return byte-swapped int.
     */
    private static int swap(int value) {
        int b1 = (value >> 0) & 0xff;
        int b2 = (value >> 8) & 0xff;
        int b3 = (value >> 16) & 0xff;
        int b4 = (value >> 24) & 0xff;

        return b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0;
    }
    
    /**
     * 
     * @param value
     * @return byte-swapped float.
     */
    private static float swap(float value) {
        int intValue = Float.floatToIntBits(value);
        intValue = swap(intValue);
        return Float.intBitsToFloat(intValue);
    }
    
    /**
     * 
     * @param ir
     * @param types
     * @param rad
     * @return
     */
    public static FloatRaster computeProportionWithinRadius(IntegerRaster ir, int[] types, double rad) {
        FloatRaster res = null;
        if ((ir != null) && (types != null) && (types.length > 0) && (rad > 0.0)) {
            res = new FloatRaster(ir.getNumberOfRows(), ir.getNumberOfCols(), ir.getXmin(), ir.getYmin(), ir.getCellSize(), Float.NaN);
            int cellCount = 0, typeCount = 0;
            int nCells = (int)(Math.ceil(rad/res.getCellSize()));
            int minR, maxR, minC, maxC;
            PointIn2D focalCellCent = null, neighCellCent = null;
            for (int r= 0; r < res.getNumberOfRows(); r++) {
                //
                for (int c = 0; c < res.getNumberOfCols(); c++) {
                    //
                    focalCellCent = new PointIn2D(res.getXcoordFromCol(c), res.getYcoordFromRow(r));
                    //
                    cellCount = 0;
                    typeCount = 0;
                    // go through neighborhood
                    minR = r - nCells;
                    if (minR < 0) {
                        minR = 0;
                    }
                    maxR = r + nCells;
                    if (maxR >= res.getNumberOfRows()) {
                        maxR = res.getNumberOfRows() - 1;
                    }
                    minC = c - nCells;
                    if (minC < 0) {
                        minC = 0;
                    }
                    maxC = c + nCells;
                    if (maxC >= res.getNumberOfCols()) {
                        maxC = res.getNumberOfCols() - 1;
                    }
                    //
                    for (int ri = minR; ri <= maxR; ri++) {
                        for (int ci = minC; ci <= maxC; ci++) {
                            neighCellCent = new PointIn2D(res.getXcoordFromCol(ci), res.getYcoordFromRow(ri));
                            if (focalCellCent.withinDistanceOf(neighCellCent, rad)) {
                                cellCount++;
                                for (int t = 0; t < types.length; t++) {
                                    if (ir.getCellValue(ri, ci) == types[t]) {
                                        typeCount++;
                                    }
                                }
                            }
                        }
                    }
                    if (cellCount > 0) {
                        res.setCellValue(r, c, ((float)typeCount)/((float)cellCount));
                    }
                }
            }
        }
        return res;
    }
    
    /**
     * 
     * @param ir
     * @param types
     * @return
     */
    public static FloatRaster shortestDistance(IntegerRaster ir, int[] types) {
        FloatRaster res = null;
        if ((ir != null) && (types != null) && (types.length > 0)) {
            res = new FloatRaster(ir.getNumberOfRows(), ir.getNumberOfCols(), ir.getXmin(), ir.getYmin(), ir.getCellSize(), Float.NaN);
            FloatRaster tmp = new FloatRaster(ir.getNumberOfRows(), ir.getNumberOfCols(), ir.getXmin(), ir.getYmin(), ir.getCellSize(), Float.NaN);
            //
            int[] deltaRow = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] deltaCol = {-1, 0, 1, -1, 1, -1, 0, 1};
            float[] weightAdjs = {1.414f, 1.0f, 1.414f, 1.0f, 1.0f, 1.414f, 1.0f, 1.414f};
            int currIter = 0;
            long totalCells = res.getNumberOfRows() * res.getNumberOfCols();
            long numSourceCells = 0;
            long numChanges = 1;
            float minCost, tmpCost;
            // set all cells corresponding to a value of type in ir to 0
            for (int r = 0; r < res.getNumberOfRows(); r++) {
                for (int c = 0; c < res.getNumberOfCols(); c++) {
                    for (int t = 0; t < types.length; t++) {
                        if (ir.getCellValue(r, c) == types[t]) {
                            res.setCellValue(r, c, 0.0f);
                            numSourceCells++;
                        }
                    }
                    tmp.setCellValue(r, c, Float.NaN);
                }
            }
            // now propagate distance
            // while there are cells left, propagate costs...
            if (numSourceCells > 0) {
                while ((currIter < totalCells) && (numChanges > 0)) {
                    numChanges = 0;
                    for (int r = 0; r < res.getNumberOfRows(); r++) {
                        for (int c = 0; c < res.getNumberOfCols(); c++) {
                            // go through neighbors
                            minCost = Float.MAX_VALUE;
                            for (int i = 0; i < 8; i++) {
                                if (!res.cellIsNoData(r + deltaRow[i], c + deltaCol[i])) {
                                    tmpCost = res.getCellValue(r + deltaRow[i], c + deltaCol[i]) + weightAdjs[i] * ((float)res.getCellSize());
                                    if (tmpCost < minCost) {
                                        minCost = tmpCost;
                                    }
                                }
                            }
                            // set tmp value
                            if (((minCost < res.getCellValue(r, c)) || (res.cellIsNoData(r, c)))) {
                                tmp.setCellValue(r, c, minCost);
                            }
                        }
                    }
                    // put values from tmp in output raster and set tmp back to NaN
                    for (int r = 0; r < res.getNumberOfRows(); r++) {
                        for (int c = 0; c < res.getNumberOfCols(); c++) {
                            if (!Float.isNaN(tmp.getCellValue(r, c))) {
                                res.setCellValue(r, c, tmp.getCellValue(r, c));
                                tmp.setCellValue(r, c, Float.NaN);
                                numChanges++;
                            }
                        }
                    }
                    currIter++;
                    System.out.println("cells changed = " + numChanges);
                }
            }
            tmp = null;
        }
        return res;
    }
    
    
    
    
    
    
    
    
    
    /**
     * Support method for fractalTerrain01
     * @param f
     * @param b
     * @return
     */
    private static float getDiamondCornerMean(FloatRaster f, BoxIn2D b) {
        float res = Float.NaN;
        if ((f != null) && (b != null)) {
            float tmpSum = 0.0f;
            float tmpCnt = 0.0f;
            // NW
            if (!f.cellIsNoData(b.getNWcorner().getX(), b.getNWcorner().getY())) {
                tmpSum += f.getCellValue(b.getNWcorner().getX(), b.getNWcorner().getY());
                tmpCnt += 1.0f;
            }
            // NE
            if (!f.cellIsNoData(b.getNEcorner().getX(), b.getNEcorner().getY())) {
                tmpSum += f.getCellValue(b.getNEcorner().getX(), b.getNEcorner().getY());
                tmpCnt += 1.0f;
            }
            // SW
            if (!f.cellIsNoData(b.getSWcorner().getX(), b.getSWcorner().getY())) {
                tmpSum += f.getCellValue(b.getSWcorner().getX(), b.getSWcorner().getY());
                tmpCnt += 1.0f;
            }
            // SE
            if (!f.cellIsNoData(b.getSEcorner().getX(), b.getSEcorner().getY())) {
                tmpSum += f.getCellValue(b.getSEcorner().getX(), b.getSEcorner().getY());
                tmpCnt += 1.0f;
            }
            if (tmpCnt > 0.0f) {
                res = tmpSum/tmpCnt;
            }
        }
        return res;
    }
    
    /**
     * Support method for fractalTerrain01
     * @param f
     * @param b
     * @return
     */
    private static float getSquareCornerMean(FloatRaster f, BoxIn2D b) {
        float res = Float.NaN;
        if ((f != null) && (b != null)) {
            float tmpSum = 0.0f;
            float tmpCnt = 0.0f;
            // N
            if (!f.cellIsNoData(b.getNorthEdgeMidpoint().getX(), b.getNorthEdgeMidpoint().getY())) {
                tmpSum += f.getCellValue(b.getNorthEdgeMidpoint().getX(), b.getNorthEdgeMidpoint().getY());
                tmpCnt += 1.0f;
            }
            // S
            if (!f.cellIsNoData(b.getSouthEdgeMidpoint().getX(), b.getSouthEdgeMidpoint().getY())) {
                tmpSum += f.getCellValue(b.getSouthEdgeMidpoint().getX(), b.getSouthEdgeMidpoint().getY());
                tmpCnt += 1.0f;
            }
            // E
            if (!f.cellIsNoData(b.getEastEdgeMidpoint().getX(), b.getEastEdgeMidpoint().getY())) {
                tmpSum += f.getCellValue(b.getEastEdgeMidpoint().getX(), b.getEastEdgeMidpoint().getY());
                tmpCnt += 1.0f;
            }
            // W
            if (!f.cellIsNoData(b.getWestEdgeMidpoint().getX(), b.getWestEdgeMidpoint().getY())) {
                tmpSum += f.getCellValue(b.getWestEdgeMidpoint().getX(), b.getWestEdgeMidpoint().getY());
                tmpCnt += 1.0f;
            }
            if (tmpCnt > 0.0f) {
                res = tmpSum / tmpCnt;
            }
        }
        return res;
    }
    
    /**
     * 
     * @param h
     * @param var0
     * @param nr
     * @param nc
     * @param xll
     * @param yll
     * @param cellsz
     * @param nodat
     * @return
     */
    public static FloatRaster fractalTerrain01(double h, double var0, int nr, int nc, double xll, double yll, double cellsz, float nodat) {
        FloatRaster res = null;
        // check args (replace below)
        if (true) {
            int tmpDim = nr;
            if (nc > nr) {
                tmpDim = nc;
            }
            FloatRaster resTmp = new FloatRaster(tmpDim, tmpDim, xll, yll, cellsz, nodat);
            // starting box w/ offset from cell centers
            double x1 = resTmp.getXcoordFromCol(0) + 0.25*resTmp.getCellSize();
            double x2 = resTmp.getXcoordFromCol(tmpDim - 1) + 0.25*resTmp.getCellSize();
            double y1 = resTmp.getYcoordFromRow(tmpDim - 1) + 0.25*resTmp.getCellSize();
            double y2 = resTmp.getYcoordFromRow(0) + 0.25*resTmp.getCellSize();
            // calculate step sizes
            double xStep = x2 - x1;
            double yStep = y2 - y1;
            double xTemp, yTemp;
            BoxIn2D tmpBox1 = new BoxIn2D(x1, y1, x2, y2);
            BoxIn2D tmpBox2 = new BoxIn2D(x1, y1, x2, y2);
            PointIn2D tmpPoint = null;
            float newValue;
            //
            double var = var0;
            double htSq = (resTmp.getYmax() - resTmp.getYmin()) * (resTmp.getYmax() - resTmp.getYmin());
            double bsSq = (resTmp.getXmax() - resTmp.getXmin()) * (resTmp.getXmax() - resTmp.getXmin());
            double rSq = htSq + bsSq;
            double propConst = var0 / Math.pow(rSq, h);
            double mnRsq;
            
            int k = 1;
            // assign value to corners
            newValue = (float)RandomNumberGenerator.rNormal(0.0, var);
            resTmp.setCellValue(x1, y1, newValue);
            newValue = (float)RandomNumberGenerator.rNormal(0.0, var);
            resTmp.setCellValue(x2, y1, newValue);
            newValue = (float)RandomNumberGenerator.rNormal(0.0, var);
            resTmp.setCellValue(x1, y2, newValue);
            newValue = (float)RandomNumberGenerator.rNormal(0.0, var);
            resTmp.setCellValue(x2, y2, newValue);
            //
            while ((xStep >= cellsz) || (yStep >= cellsz)) {
                // adjust variance
                //var /= Math.pow(2.0, 2.0 * h); // fine if square raster...
                
                // 1.  diamond steps
                xTemp = x1;
                while (xTemp <= x2) {
                    yTemp = y1;
                    while (yTemp <= y2) {
                        tmpBox1.reset(xTemp, yTemp, xTemp + xStep, yTemp + yStep);
                        tmpPoint = tmpBox1.getCenterPoint();
                        
                        // adjust variance
                        mnRsq = rSq / Math.pow(2.0, (2.0 * (double) k));
                        var = propConst * Math.pow(mnRsq, h);
                        
                        if ((resTmp.getGridCol(tmpPoint.getX()) >= 0) && (resTmp.getGridCol(tmpPoint.getX()) < resTmp.getNumberOfCols()) && (resTmp.getGridRow(tmpPoint.getY()) >= 0) && (resTmp.getGridRow(tmpPoint.getY()) < resTmp.getNumberOfRows()) && (resTmp.cellIsNoData(tmpPoint.getX(), tmpPoint.getY()))) {
                            newValue = getDiamondCornerMean(resTmp, tmpBox1) + (float) RandomNumberGenerator.rNormal(0.0, var);
                            if (!Float.isNaN(newValue)) {
                                resTmp.setCellValue(tmpPoint.getX(), tmpPoint.getY(), newValue);
                            }
                        }
                        yTemp += yStep;
                    }
                    xTemp += xStep;
                }

                // 2.  square steps
                xTemp = x1;
                while (xTemp <= x2) {
                    yTemp = y1;
                    while (yTemp <= y2) {
                        
                        // adjust variance
                        mnRsq = rSq / Math.pow(2.0, (2.0 * (double) k));
                        var = propConst * Math.pow(mnRsq, h);
                        
                        tmpBox1.reset(xTemp, yTemp, xTemp + xStep, yTemp + yStep);
                        // N
                        tmpPoint = tmpBox1.getNorthEdgeMidpoint();
                        if ((resTmp.getGridCol(tmpPoint.getX()) >= 0) && (resTmp.getGridCol(tmpPoint.getX()) < resTmp.getNumberOfCols()) && (resTmp.getGridRow(tmpPoint.getY()) >= 0) && (resTmp.getGridRow(tmpPoint.getY()) < resTmp.getNumberOfRows()) && (resTmp.cellIsNoData(tmpPoint.getX(), tmpPoint.getY()))) {
                            tmpBox2.reset(tmpPoint.getX() - 0.5 * xStep, tmpPoint.getY() - 0.5 * yStep, tmpPoint.getX() + 0.5 * xStep, tmpPoint.getY() + 0.5 * yStep);
                            newValue = getSquareCornerMean(resTmp, tmpBox2) + (float) RandomNumberGenerator.rNormal(0.0, var);
                            if (!Float.isNaN(newValue)) {
                                resTmp.setCellValue(tmpPoint.getX(), tmpPoint.getY(), newValue);
                            }
                        }
                        // S
                        tmpPoint = tmpBox1.getSouthEdgeMidpoint();
                        if ((resTmp.getGridCol(tmpPoint.getX()) >= 0) && (resTmp.getGridCol(tmpPoint.getX()) < resTmp.getNumberOfCols()) && (resTmp.getGridRow(tmpPoint.getY()) >= 0) && (resTmp.getGridRow(tmpPoint.getY()) < resTmp.getNumberOfRows()) && (resTmp.cellIsNoData(tmpPoint.getX(), tmpPoint.getY()))) {
                            tmpBox2.reset(tmpPoint.getX() - 0.5 * xStep, tmpPoint.getY() - 0.5 * yStep, tmpPoint.getX() + 0.5 * xStep, tmpPoint.getY() + 0.5 * yStep);
                            newValue = getSquareCornerMean(resTmp, tmpBox2) + (float) RandomNumberGenerator.rNormal(0.0, var);
                            if (!Float.isNaN(newValue)) {
                                resTmp.setCellValue(tmpPoint.getX(), tmpPoint.getY(), newValue);
                            }
                        }
                        // E
                        tmpPoint = tmpBox1.getEastEdgeMidpoint();
                        if ((resTmp.getGridCol(tmpPoint.getX()) >= 0) && (resTmp.getGridCol(tmpPoint.getX()) < resTmp.getNumberOfCols()) && (resTmp.getGridRow(tmpPoint.getY()) >= 0) && (resTmp.getGridRow(tmpPoint.getY()) < resTmp.getNumberOfRows()) && (resTmp.cellIsNoData(tmpPoint.getX(), tmpPoint.getY()))) {
                            tmpBox2.reset(tmpPoint.getX() - 0.5 * xStep, tmpPoint.getY() - 0.5 * yStep, tmpPoint.getX() + 0.5 * xStep, tmpPoint.getY() + 0.5 * yStep);
                            newValue = getSquareCornerMean(resTmp, tmpBox2) + (float) RandomNumberGenerator.rNormal(0.0, var);
                            if (!Float.isNaN(newValue)) {
                                resTmp.setCellValue(tmpPoint.getX(), tmpPoint.getY(), newValue);
                            }
                        }
                        // W
                        tmpPoint = tmpBox1.getWestEdgeMidpoint();
                        if ((resTmp.getGridCol(tmpPoint.getX()) >= 0) && (resTmp.getGridCol(tmpPoint.getX()) < resTmp.getNumberOfCols()) && (resTmp.getGridRow(tmpPoint.getY()) >= 0) && (resTmp.getGridRow(tmpPoint.getY()) < resTmp.getNumberOfRows()) && (resTmp.cellIsNoData(tmpPoint.getX(), tmpPoint.getY()))) {
                            tmpBox2.reset(tmpPoint.getX() - 0.5 * xStep, tmpPoint.getY() - 0.5 * yStep, tmpPoint.getX() + 0.5 * xStep, tmpPoint.getY() + 0.5 * yStep);
                            newValue = getSquareCornerMean(resTmp, tmpBox2) + (float) RandomNumberGenerator.rNormal(0.0, var);
                            if (!Float.isNaN(newValue)) {
                                resTmp.setCellValue(tmpPoint.getX(), tmpPoint.getY(), newValue);
                            }
                        }
                        yTemp += yStep;
                    }
                    xTemp += xStep;
                }
                
                // update step sizes
                xStep *= 0.5;
                yStep *= 0.5;
                k++;
            }
            res = new FloatRaster(nr, nc, xll, yll, cellsz, nodat);
            for (int r = 0; r < nr; r++) {
                for (int c = 0; c < nc; c++) {
                    res.setCellValue(r, c, resTmp.getCellValue(r, c));
                }
            }
        }
        return res;
    }
    
    /**
     * Assign a proportion p of the values of f to category IDs in vals
     * @param f
     * @param p
     * @param vals
     * @return
     */
    public static IntegerRaster createCategoricalMap(FloatRaster f, double[] p, int[] vals) {
        IntegerRaster res = null;
        if ((f != null) && (p != null) && (p.length > 0) && (vals != null) && (vals.length == p.length)) {
            boolean passCheck = true;
            double sumVal = 0.0;
            for (int k = 0; k < p.length; k++) {
                if (p[k] < 0.0) {
                    passCheck = false;
                }
                sumVal += p[k];
            }
            if (passCheck) {
                //
                for (int k = 0; k < p.length; k++) {
                    p[k] = p[k]/sumVal;
                    // System.out.println("proportion for category " + vals[k] + " = " + p[k]);
                }
                //
                res = new IntegerRaster(f.getNumberOfRows(), f.getNumberOfCols(), f.getXmin(), f.getYmin(), f.getCellSize(), -9999);
                int numCells = f.getNumberOfDataCells();
                FloatRasterSortElement[] sortList = new FloatRasterSortElement[numCells];
                int listIndex = 0;
                for (int r = 0; r < f.getNumberOfRows(); r++) {
                    for (int c = 0; c < f.getNumberOfCols(); c++) {
                        if (!f.cellIsNoData(r, c)) {
                            sortList[listIndex] = new FloatRasterSortElement(new RasterCellIndex(r, c), f.getCellValue(r, c));
                            listIndex++;
                        }

                    }
                }
                SortUtilities.quickSort(sortList);
                int[] numCat = new int[p.length];
                int cumSum = 0;
                for (int k = 0; k < p.length; k++) {
                    numCat[k] = cumSum + (int) (p[k] * ((double) numCells));
                    cumSum = numCat[k];
                    // System.out.println("cumulative count for category " + vals[k] + " = " + numCat[k]);
                }
                //
                int valIndex;
                for (int i = 0; i < sortList.length; i++) {
                    valIndex = 0;
                    while ((numCat[valIndex] < i) && (valIndex < numCat.length)) {
                        valIndex++;
                    }
                    res.setCellValue(sortList[i].getRasterCellIndex().getRow(), sortList[i].getRasterCellIndex().getCol(), vals[valIndex], true);
                }
            }
        }
        return res;
    }
    
    
    // some other methods to add...
    // void addValueToIntersectedCells(edge & e, float v, double boxBuff=0.001, bool changeMissing=false);
    // void replaceMaxValueInIntersectedCells(edge & e, float v, double boxBuff=0.001, bool changeMissing=false); // replace only if v is greater than current value
    // void replaceValueInCellsInRange(float min, float max, float v); //
    // double calculateProductOfIntersectedCells(edge & e, int q, double boxBuff);
    // WRITE OUT ASCII RASTER FILES
    
}
