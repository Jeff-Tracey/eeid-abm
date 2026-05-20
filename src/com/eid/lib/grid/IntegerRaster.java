/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.grid;

import java.util.*;
import com.eid.lib.geometricprimitives.*;


/**
 * 
 * 
 * @author jeff
 */
public class IntegerRaster {
    protected int nRows;
    protected int nCols;
    protected double xLL;
    private double yLL;
    private double cellSz;
    private int noDat;
    protected int[][] data;
    private ArrayList<Integer> fixedCategories = null;

    /**
     * 
     */
    public IntegerRaster() {
        nRows = 0;
        nCols = 0;
        xLL = 0.0;
        yLL = 0.0;
        cellSz = 0.0;
        noDat = -9999;
        data = null;
        fixedCategories = new ArrayList<Integer>();
    }
    
    /**
     * 
     * @param nr
     * @param nc
     * @param xl
     * @param yl
     * @param cSz
     * @param nodata
     */
    public IntegerRaster(int nr, int nc, double xl, double yl, double cSz, int nodata) {
        if ((nr > 0) && (nc > 0) && (cSz > 0.0)) {
            nRows = nr;
            nCols = nc;
            xLL = xl;
            yLL = yl;
            cellSz = cSz;
            noDat = nodata;
            data = new int[nRows][nCols];
            for (int r = 0; r < nRows; r++) {
                for (int c = 0; c < nCols; c++) {
                    data[r][c] = noDat;
                }
            }
        } else {
            // error or default construction
            nRows = 0;
            nCols = 0;
            xLL = 0.0;
            yLL = 0.0;
            cellSz = 0.0;
            noDat = -9999;
            data = null;
        }
        fixedCategories = new ArrayList<Integer>();
    }
    
    /**
     * 
     * @return The number of columns in the raster.
     */
    public int getNumberOfCols() {
        return nCols;
    }
    
    /**
     * 
     * @return The number of rows in the raster.
     */
    public int getNumberOfRows() {
        return nRows;
    }
    
    /**
     * 
     * @return the cell size.
     */
    public double getCellSize() {
        return cellSz;
    }
    
    /**
     * 
     * @return no (missing) data value.
     */
    public int getNoDataValue() {
        return noDat;
    }
    
    /**
     * 
     * @return the minimum x-coordinate.
     */
    public double getXmin() {
        return xLL;
    }
    
    /**
     * 
     * @return the maximum x-coordinate.
     */
    public double getXmax() {
        return xLL + ((double)nCols)*cellSz;
    }
    
    /**
     * 
     * @return the minimum y-coordinate.
     */
    public double getYmin() {
        return yLL;
    }
    
    /**
     * 
     * @return the maximum y-coordinate.
     */
    public double getYmax() {
        return yLL + ((double)nRows)*cellSz;
    }
    
    /**
     * 
     * @param ycoord
     * @return The row index corresponding to the y-coordinate.  If the
     * y-coordinate is off the raster, -1 will be returned.
     */
    public int getGridRow(double ycoord) {
        if ((ycoord >= yLL) && (ycoord <= (yLL + cellSz*(int)nRows))) {
            return (int)Math.floor((double)nRows - (ycoord - yLL)/cellSz); // if cell 0,0 is in lower left corner
        } else {
            return -1; // ERROR CODE
        }
    }
    
    /**
     * 
     * @param xcoord
     * @return The column index corresponding to the x-coordinate.  If the
     * x-coordinate is off the raster, -1 will be returned
     */
    public int getGridCol(double xcoord) {
        if ((xcoord >= xLL) && (xcoord <= (xLL + cellSz*(int)nCols))) {
            return (int)(Math.floor((xcoord - xLL)/cellSz)); //
        } else {
            return -1; // ERROR CODE
        }
    }
    
    /**
     * 
     * @param c
     * @return the x-coordinate for the center of column c.
     */
    public double getXcoordFromCol(int c) {
        double res;
        if ((c >= 0) && (c < nCols)) {
            res = xLL + cellSz * (double) c + 0.5 * cellSz;
        } else { // out-of-bounds
            res = Double.NaN;
        }
        return res;
    }
    
    /**
     * 
     * @param r
     * @return the y-coordinate for the center of row r.
     */
    public double getYcoordFromRow(int r) {
        double res;
        if ((r >= 0) && (r < nRows)) {
            res = yLL + cellSz * ((double) nRows - (double) r - 0.5);
        } else { // out-of-bounds
            res = Double.NaN;
        }
        return res;
    }
    
    /**
     * 
     * @param r
     * @param c
     * @return The value of the cell (r, c), or no data if r or c is out-of-bounds.
     */
    public int getCellValue(int r, int c) {
        if ((r >= 0) && (r < nRows) && (c >= 0) && (c < nCols)) {
            return data[r][c];
        } else {
            return noDat;
        }
    }
    
    /**
     * 
     * @param x
     * @param y
     * @return The value of the cell containing the coordinates (x, y), or
     * no data if (x, y) is off the raster.
     */
    public int getCellValue(double x, double y) {
        int r = getGridRow(y);
        int c = getGridCol(x);
        if ((r >= 0) && (r < nRows) && (c >= 0) && (c < nCols)) {
            return data[r][c];
        } else {
            return noDat;
        }
    }
    
    
    
    /**
     * 
     * @param p
     * @return
     */
    public int getCellValue(PointIn2D p) {
        if (p != null) {
            return getCellValue(p.getX(), p.getY());
        } else {
            return noDat;
        }
    }
    
    /***************************************************************************
     * METHODS TO RESTRICT EDITS TO RASTERS
     **************************************************************************/
    
    /**
     * 
     * @param catID
     * @return true if can change category catID, false otherwise.
     */
    private boolean canChangeCategory(int catID) {
        boolean res = true;
        for (int i = 0; i < fixedCategories.size(); i++) {
            if (fixedCategories.get(i) == catID) {
                res = false;
                break;
            }
        }
        return res;
    }
    
    /**
     * 
     * @param r
     * @param c
     * @return true if can change the value in cell (r, c), false otherwise.
     */
    boolean canChangeCategoryInCell(int r, int c) {
        if ((r >= 0) && (r < nRows) && (c >= 0) && (c < nCols)) {
            if (canChangeCategory(data[r][c])) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    
    
    /**
     * 
     */
    void clearFixedCategories() {
        fixedCategories.clear();
    }
    
    /**
     * 
     * @param catID
     */
    void addFixedCategory(int catID) {
        if (canChangeCategory(catID)) {
            fixedCategories.add(catID);
        }
    }
    
    /**
     * 
     * @param r
     * @param c
     * @return true if cell contains no (missing) data value, false otherwise.
     */
    public boolean cellIsNoData(int r, int c) {
        if ((r >= 0) && (r < nRows) && (c >= 0) && (c < nCols)) {
            if (data[r][c] == noDat) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    
    
    
    /**
     * 
     * @param r
     * @param c
     * @param v
     */
    public void setCellValue(int r, int c, int v, boolean changeNoData) {
        // r, c is check to be in-bounds in both methods in condition that follows
        if (((changeNoData) || (!cellIsNoData(r, c))) && (canChangeCategoryInCell(r, c))) {
            data[r][c] = v;
        } // else do nothing
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param v
     */
    void setCellValue(double x, double y, int v, boolean changeNoData) {
        int r = getGridRow(y);
        int c = getGridCol(x);
        setCellValue(r, c, v, changeNoData);
    }
    
    /**
     * 
     * @param setNoData
     */
    public void setAllCellsToZero(boolean setNoData) {
        for (int r = 0; r < nRows; r++) {
            for (int c = 0; c < nCols; c++) {
                if ((!cellIsNoData(r, c)) || (setNoData)){
                    data[r][c] = 0;
                }
            }
        }
    }
    
}
