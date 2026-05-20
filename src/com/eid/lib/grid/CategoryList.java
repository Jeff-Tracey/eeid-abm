/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eid.lib.grid;

import java.util.*;
import com.eid.lib.stochastic.*;

/**
 *
 * @author Jeff A. Tracey
 * Copyright Jeff A. Tracey 2008 All rights reserved.
 */
public class CategoryList {

    private ArrayList<Integer> catNums = null;
    private ArrayList<Integer> totalCatCounts = null;
    private ArrayList<Integer> localCatCounts = null;

    /**
     * 
     * @param f
     * @param includeNoData
     */
    public CategoryList(IntegerRaster f, boolean includeNoData) {
        catNums = new ArrayList<Integer>();
        totalCatCounts = new ArrayList<Integer>();
        localCatCounts = new ArrayList<Integer>();
        if (f != null) {
            boolean isPresent = false;
            int cellCat;
            for (int r = 0; r < f.getNumberOfRows(); r++) {
                for (int c = 0; c < f.getNumberOfCols(); c++) {
                    if ((!f.cellIsNoData(r, c)) || (includeNoData)) {
                        cellCat = f.getCellValue(r, c);
                        isPresent = false;
                        for (int i = 0; i < catNums.size(); i++) {
                            if (catNums.get(i) == cellCat) {
                                // set newColor in mapColors
                                totalCatCounts.set(i, totalCatCounts.get(i) + 1);
                                isPresent = true;
                                break;
                            }
                        }
                        if (!isPresent) {
                            // add catID, newColor, catName
                            boolean isInserted = false;
                            for (int i = 0; i < catNums.size(); i++) {
                                if (cellCat < catNums.get(i)) {
                                    // insert before
                                    catNums.add(i, cellCat);
                                    totalCatCounts.add(i, 1);
                                    localCatCounts.add(i, 0);
                                    isInserted = true;
                                    break;
                                }
                            }
                            if (!isInserted) {
                                catNums.add(cellCat);
                                totalCatCounts.add(1);
                                localCatCounts.add(0);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 
     */
    public void setAllLocalCountsToZero() {
        for (int i = 0; i < localCatCounts.size(); i++) {
            localCatCounts.set(i, 0);
        }
    }
    
    /**
     * 
     * @param cat
     */
    public void incrementLocalCountForCategory(int cat) {
        for (int i = 0; i < localCatCounts.size(); i++) {
            if (catNums.get(i) == cat) {
                localCatCounts.set(i, (localCatCounts.get(i) + 1));
                break;
            }
        }
    }
    
    /**
     * 
     * @param cat
     * @return local (neighborhood) count of cells in category cat.
     */
    public int getLocalCountForCategory(int cat) {
        int res = 0;
        for (int i = 0; i < localCatCounts.size(); i++) {
            if (catNums.get(i) == cat) {
                res = localCatCounts.get(i);
                break;
            }
        }
        return res;
    }
    
    /**
     * 
     * @param cat
     * @return total count of cells in category cat.
     */
    public int getTotalCountForCategory(int cat) {
        int res = 0;
        for (int i = 0; i < totalCatCounts.size(); i++) {
            if (catNums.get(i) == cat) {
                res = totalCatCounts.get(i);
                break;
            }
        }
        return res;
    }
    
    /**
     * 
     * @param cat
     * @return the index of the category cat.  If cat is not in the list, -1 is
     * returned.
     */
    public int getIndexForCategory(int cat) {
        int res = -1;
        for (int i = 0; i < totalCatCounts.size(); i++) {
            if (catNums.get(i) == cat) {
                res = i;
                break;
            }
        }
        return res;
    }
    
    /**
     * 
     * @return returns the category with the largest local count.  If more than
     * one category is tied for the maximum count, one of them is randomly
     * selected.
     */
    public int getCategoryWithMaxLocalCount() {
        int max = 0;
        int maxCount = 0;
        int[] maxList = new int[localCatCounts.size()];
        int res = 0;
        // first pass, get the max
        for (int i = 0; i < localCatCounts.size(); i++) {
            if (localCatCounts.get(i) > max) {
                max = localCatCounts.get(i);
            }
        }
        // second pass, find all those with value equal to max
        for (int i = 0; i < localCatCounts.size(); i++) {
            if (localCatCounts.get(i) == max) {
                maxList[i] = 1;
                maxCount++;
            } else {
                maxList[i] = 0;
            }
        }
        // third pass, randomly select among those with count equal to max
        int r = RandomNumberGenerator.drawInteger(maxCount);
        maxCount = 0 ;
        for (int i = 0; i < localCatCounts.size(); i++) {
            if (maxList[i] == 1) {
                if (maxCount == r) {
                    res = catNums.get(i);
                    break;
                } else {
                    maxCount++;
                }
            }
        }
        return res;
    }
    
    /**
     * 
     * @return
     */
    public int getNumberOfCategories() {
        return catNums.size();
    }
    
    /**
     * 
     * @param i
     * @return
     */
    public int getCategoryIDforIndex(int i) {
        if ((i >= 0) && (i < catNums.size())) {
            return catNums.get(i);
        } else {
            return -1;
        }
    }
    
    /**
     * 
     */
    public void display() {
        System.out.println("Category List:");
        for (int i = 0; i < catNums.size(); i++) {
            System.out.println("category number: " + catNums.get(i) + ", count: " + totalCatCounts.get(i));
        }
    }
}
