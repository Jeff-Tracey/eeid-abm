/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.grid;

import java.util.*;

/**
 * Static methods to create IntegerRasters or apply transformations to existing
 * ones.  Also handles file IO.
 * 
 * I NEED AN ITERATOR TO LOOP THROUGH THE CELLS OF THE RASTER AND APPLY
 * FUNCTIONS (SEE NEURAL NET VERSION I MADE)
 * I WANT TO MAKE A METHOD THAT ITERATES THROUGH ALL CELLS AND TAKES A FUNCTION
 * OBJECT (IMPLEMENTS AN INTERFACE) AS AN ARGUMENT WHICH PERFORMS OPERATIONS
 * ON THE RASTER, THEN EACH SPECIFIC METHOD ONLY HAS TO CALL THE ITERATOR
 * WITH THE FUNCTION OBJECT (VISITOR PATTERN?)
 * 
 * @author jeff
 */
public class IntegerRasterFunctions {
    public static final int NULL_PATCH_ID = -1;
    
    /**
     * Assigns unique integer from 1,...,N to each contiguous patch of type
     * patchTypeID.  All other cells assigned no data value.  Uses a span-based
     * algorithm.
     * @param initRast
     * @param patchTypeID
     * @return
     */
    public static IntegerRaster assignUniquePatchNumbers(IntegerRaster patchRast, int patchTypeID, FirstOrderNeighborhoods neighbType) {
        IntegerRaster res = null;
        if (patchRast != null) {
            // create output raster
            res = new IntegerRaster(patchRast.getNumberOfRows(), 
                    patchRast.getNumberOfCols(), 
                    patchRast.getXmin(), 
                    patchRast.getYmin(), 
                    patchRast.getCellSize(), 
                    NULL_PATCH_ID);
            ArrayList<RasterCellIndex> runStack = new ArrayList<RasterCellIndex>();
            RasterCellIndex tmpRastIndex = null;
            
            // get count of patch cells
            int patchCellCount = 0;
            for (int r = 0; r < patchRast.getNumberOfRows(); r++) {
                for (int c = 0; c < patchRast.getNumberOfCols(); c++) {
                    if (patchRast.getCellValue(r, c) == patchTypeID) {
                        res.setCellValue(r, c, 0, true);
                        patchCellCount++;
                    }
                }
            }
            //System.out.println("Number of patch cells = " + patchCellCount);
            
            //
            int currPatchID = 1; // the current ID number being assigned to patches
            boolean seedNotFound = true;
            int indNum = 0;
            int currR, currC;
            boolean onUpperRun = false;
            boolean onLowerRun = false;
            int tmpCol;
            while (patchCellCount > 0) {
                // find starting point and add to stack
                indNum = 0;
                seedNotFound = true;
                while ((seedNotFound) && (indNum < res.getNumberOfRows()*res.getNumberOfCols())){
                    currR = indNum/res.getNumberOfCols();
                    currC = indNum%res.getNumberOfCols();
                    if (res.getCellValue(currR, currC) == 0) {
                        runStack.add(new RasterCellIndex(currR, currC));
                        seedNotFound = false;
                    }
                    indNum++;
                } // end while patch seed not found
                
                // fill the patch
                while (runStack.size() > 0) {
                    // pop an index off the stack
                    tmpRastIndex = runStack.get(runStack.size() - 1);
                    runStack.remove(tmpRastIndex);
                    if (res.getCellValue(tmpRastIndex.getRow(), tmpRastIndex.getCol()) == 0) {
                        // find the left-most column
                        while (res.getCellValue(tmpRastIndex.getRow(), tmpRastIndex.getCol() - 1) == 0) {
                            tmpRastIndex.setCol(tmpRastIndex.getCol() - 1);
                        }
                        
                        // fill current run, and add starting points for new runs to stack
                        onUpperRun = false;
                        onLowerRun = false;
                        // check diagonal if Moore neighborhood
                        if (neighbType == FirstOrderNeighborhoods.MOORE) {  // check upper and lower left
                            tmpCol = tmpRastIndex.getCol() - 1;
                            if ((tmpCol >= 0) && (tmpCol < res.getNumberOfCols())) {
                                if (res.getCellValue(tmpRastIndex.getRow() - 1, tmpCol) == 0) {
                                    if (!onUpperRun) {
                                        runStack.add(new RasterCellIndex(tmpRastIndex.getRow() - 1, tmpCol));
                                        onUpperRun = true;
                                    }
                                } else {
                                    onUpperRun = false;
                                }
                                // check for lower run
                                if (res.getCellValue(tmpRastIndex.getRow() + 1, tmpCol) == 0) {
                                    if (!onLowerRun) {
                                        runStack.add(new RasterCellIndex(tmpRastIndex.getRow() + 1, tmpCol));
                                        onLowerRun = true;
                                    }
                                } else {
                                    onLowerRun = false;
                                }
                            }
                        }
                        
                        while (res.getCellValue(tmpRastIndex.getRow(), tmpRastIndex.getCol()) == 0) {
                            res.setCellValue(tmpRastIndex.getRow(), tmpRastIndex.getCol(), currPatchID, false);
                            patchCellCount--;
                            //System.out.println("Number of patch cells = " + patchCellCount);
                            
                            if ( (neighbType == FirstOrderNeighborhoods.VON_NEUMAN) || (neighbType == FirstOrderNeighborhoods.MOORE)) {
                                // check for upper run
                                if (res.getCellValue(tmpRastIndex.getRow() - 1, tmpRastIndex.getCol()) == 0) {
                                    if (!onUpperRun) {
                                        runStack.add(new RasterCellIndex(tmpRastIndex.getRow() - 1, tmpRastIndex.getCol()));
                                        onUpperRun = true;
                                    }
                                } else {
                                    onUpperRun = false;
                                }

                                // check for lower run
                                if (res.getCellValue(tmpRastIndex.getRow() + 1, tmpRastIndex.getCol()) == 0) {
                                    if (!onLowerRun) {
                                        runStack.add(new RasterCellIndex(tmpRastIndex.getRow() + 1, tmpRastIndex.getCol()));
                                        onLowerRun = true;
                                    }
                                } else {
                                    onLowerRun = false;
                                }
                            }
                            
                            // go to next column
                            tmpRastIndex.setCol(tmpRastIndex.getCol() + 1);
                        }
                        // check diagonal if Moore neighborhood
                        if (neighbType == FirstOrderNeighborhoods.MOORE) { // check upper and lower right
                            tmpCol = tmpRastIndex.getCol() + 0; // SHOULD ALREADY HAVE ADVANCED ONE COL PAST RUN
                            if ((tmpCol >= 0) && (tmpCol < res.getNumberOfCols())) {
                                // check for upper run
                                if (res.getCellValue(tmpRastIndex.getRow() - 1, tmpCol) == 0) {
                                    if (!onUpperRun) {
                                        runStack.add(new RasterCellIndex(tmpRastIndex.getRow() - 1, tmpCol));
                                        onUpperRun = true;
                                    }
                                } else {
                                    onUpperRun = false;
                                }
                                // check for lower run
                                if (res.getCellValue(tmpRastIndex.getRow() + 1, tmpCol) == 0) {
                                    if (!onLowerRun) {
                                        runStack.add(new RasterCellIndex(tmpRastIndex.getRow() + 1, tmpCol));
                                        onLowerRun = true;
                                    }
                                } else {
                                    onLowerRun = false;
                                }
                            }
                        }
                    }
                } // end while stack size > 0
                
                // start new patch
                currPatchID++;
                //System.out.println("On patch ID = " + currPatchID); // FOR TESTING
            } // end while patch cell count > 0
        }
        return res;
    }
    
    
    
    
    
    
    
    // some other methods to add...
    // setAllCellsToZero(bool changeMissing = false);          // set all cells to 0.0
    // setAllCellsToNoData();                                  // set all cells to no data
    // void incrementValueInIntersectedCells(edge & e, double boxBuff=0.001, bool changeMissing=false); //
    // long countMatchValueInIntersectedCells(edge & e, int q, double boxBuff=0.001);     //
    // long countNumberOfInIntersectedCells(edge & e, double boxBuff=0.001);              //
    // void replaceValueInIntersectedCells(edge & e, int q, double boxBuff=0.001, bool changeMissing=false); //
    // algorithms to test for percolation, sizes of patches, etc.
    // WRITE OUT ASCII RASTER FILES
}
