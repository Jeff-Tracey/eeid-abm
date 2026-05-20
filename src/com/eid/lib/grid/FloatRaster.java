/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.grid;

import java.util.*;
import com.eid.lib.geometricprimitives.*;

/**
 *
 * @author jeff
 */
public class FloatRaster {
    protected int nRows;
    protected int nCols;
    protected double xLL;
    protected double yLL;
    protected double cellSz;
    protected float noDat;
    protected float[][] data;

    /**
     * 
     */
    public FloatRaster() {
        nRows = 0;
        nCols = 0;
        xLL = 0.0;
        yLL = 0.0;
        cellSz = 0.0;
        noDat = -9999.0f;
        data = null;
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
    public FloatRaster(int nr, int nc, double xl, double yl, double cSz, float nodata) {
        if ((nr > 0) && (nc > 0) && (cSz > 0.0)) {
            nRows = nr;
            nCols = nc;
            xLL = xl;
            yLL = yl;
            cellSz = cSz;
            noDat = nodata;
            data = new float[nRows][nCols];
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
            noDat = -9999.0f;
            data = null;
        }
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
     * @return cell size.
     */
    public double getCellSize() {
        return cellSz;
    }
    
    /**
     * 
     * @return minimum x-coordinate.
     */
    public double getXmin() {
        return xLL;
    }
    
    /**
     * 
     * @return maximum x-coordinate.
     */
    public double getXmax() {
        return xLL + ((double)nCols)*cellSz;
    }
    
    /**
     * 
     * @return minimum y-coordinate.
     */
    public double getYmin() {
        return yLL;
    }
    
    /**
     * 
     * @return maximum y-coordinate.
     */
    public double getYmax() {
        return yLL + ((double)nRows)*cellSz;
    }
    
    /**
     * 
     * @param ycoord
     * @return The row index corresponding to the y-coordinate.
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
     * @return The column index corresponding to the x-coordinate.
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
     * @return the x-coordinate for the center of the cells in column c.
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
     * @return the y-coordinate for the center of the cells in row r.
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
     * @return The value of the cell (r, c) or no data if r or c is out-of-bounds.
     */
    public float getCellValue(int r, int c) {
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
     * @return The value of the cell containing the coordinates (x, y) or no
     * data if (x, y) is off the raster.
     */
    public float getCellValue(double x, double y) {
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
     * @param r
     * @param c
     * @return The value of the cell (r, c) or no data if r or c is out-of-bounds.
     */
    public float getCellValue(RasterCellIndex ri) {
        if (ri != null) {
            return getCellValue(ri.getRow(), ri.getCol());
        } else {
            return noDat;
        }
    }
    
    /**
     * 
     * @param p
     * @return
     */
    public float getCellValue(PointIn2D p) {
        if (p != null) {
            return getCellValue(p.getX(), p.getY());
        } else {
            return noDat;
        }
    }
    
    /**
     * Returns true of the cell is no data.
     * @param r
     * @param c
     * @return true if the cell is a no (missing) data value, false otherwise.
     */
    public boolean cellIsNoData(int r, int c) {
        if ((r >= 0) && (r < nRows) && (c >= 0) && (c < nCols)) {
            if ((data[r][c] == noDat) || (Float.isNaN(data[r][c]))) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    
    /**
     * Returns true of the cell is no data.
     * @param x
     * @param y
     * @return true if the cell is a no (missing) data value, false otherwise.
     */
    public boolean cellIsNoData(double x, double y) {
        int r = getGridRow(y);
        int c = getGridCol(x);
        return cellIsNoData(r, c);
    }
    
    /**
     * 
     * @return the no (missing) data value.
     */
    public float getNoDataValue() {
        return noDat;
    }
    
    /**
     * 
     * @return The minimum value in the raster (no data excluded).
     */
    public float getMin() {
        float res = 0.0f;
        boolean firstIsSet = false;
        for (int r = 0; r < nRows; r++) {
            for (int c = 0; c < nCols; c++) {
                if ((data[r][c] < res) || (!firstIsSet)) {
                    if (data[r][c] != noDat) {
                        res = data[r][c];
                        firstIsSet = true;
                    }
                }
            }
        }
        return res;
    }
    
    /**
     * 
     * @return The maximum value in the raster (no data excluded).
     */
    public float getMax() {
        float res = 0.0f;
        boolean firstIsSet = false;
        for (int r = 0; r < nRows; r++) {
            for (int c = 0; c < nCols; c++) {
                if ((data[r][c] > res) || (!firstIsSet)) {
                    if (data[r][c] != noDat) {
                        res = data[r][c];
                        firstIsSet = true;
                    }
                }
            }
        }
        return res;
    }
    
    /**
     * 
     * @return The maximum value minus the minimum value in the raster (no
     * data excluded).
     */
    public float getRange() {
        float res = getMax() - getMin();
        return res;
    }
    
    /**
     * Returns the number of cells that are not no data.
     * @return
     */
    public int getNumberOfDataCells() {
        int res = 0;
        for (int r = 0; r < nRows; r++) {
            for (int c = 0; c < nCols; c++) {
                if (!cellIsNoData(r, c)) {
                    res++;
                }
            }
        }
        return res;
    }
    
    /**
     * 
     * @param r
     * @param c
     * @param v
     */
    public void setCellValue(int r, int c, float v) {
        if ((r >= 0) && (r < nRows) && (c >= 0) && (c < nCols)) {
            data[r][c] = v;
        } // else do nothing
    }
    
    /**
     * 
     * @param x
     * @param y
     * @param v
     */
    public void setCellValue(double x, double y, float v) {
        int r = getGridRow(y);
        int c = getGridCol(x);
        if ((r >= 0) && (r < nRows) && (c >= 0) && (c < nCols)) {
            data[r][c] = v;
        } // else do nothing
    }
    
    /**
     * 
     * @param ci
     * @param v
     */
    public void setCellValue(RasterCellIndex ci, float v) {
        int r = ci.getRow();
        int c = ci.getCol();
        if ((r >= 0) && (r < nRows) && (c >= 0) && (c < nCols)) {
            data[r][c] = v;
        } // else do nothing
    }
    
    /**
     * 
     * @param setNoData
     */
    public void setAllCellsToZero(boolean setNoData) {
        for (int r = 0; r < nRows; r++) {
            for (int c = 0; c < nCols; c++) {
                if ((!cellIsNoData(r, c)) || (setNoData)){
                    data[r][c] = 0.0f;
                }
            }
        }
    }
    
}
