/*******************************************************************************
Title:           ArrayUtility.java

Author:          Jeff Tracey
CS Login:        jtracey
Collaborators:   None

Due Date:        Monday, April 29, 2002
Completion Date: Monday, April 29, 2002

Course:          CS367, Spring 2002, Lecture 4
Instructor:      Peterson
Assignment:      Program 5 

Editor:          CodeWarrior on Windows/vi on Unix
Compiler:        CodeWarrior on Windows/javac on Unix
Platform:        Windows 2000/Unix (Solaris)
*******************************************************************************/

package com.eid.lib.sorting;

import java.io.*;
import java.util.Random;

/**
 * SortUtilities
 * @author jeff
 */
public class SortUtilities {
    private static int num_moves;
    /**
     * Constructor
     **/
    SortUtilities() {
        //
    }
    
    /**
     * Sorts the given array using the heap sort algorithm. Note: after this
     * method finishes the array is in sorted order.
     *
     * @param A- the array to sort
     **/
    public static void heapSort(Comparable [] A) {
        if (A != null) {
            int i;
            num_moves = 0;
            if (A.length > 1) {
                heapify(A);
                for (i = A.length - 1; i >= 1; --i) {
                    swap(A, 0, i); // A DATA MOVE
                    num_moves++;
                    moveDown(A, 0, i-1);
                }
            }
        }
    }
    
    /**
     * Private helper method for heapSort; establishes heap property of array using
     * Floyd's algorithm
     **/
    private static void heapify(Comparable [] A) {
        int i, n, lastNonLeaf;
        n = A.length;
        lastNonLeaf = n/2 - 1;
        for(i = lastNonLeaf; i >= 0; --i) {
            moveDown(A, i, n-1);
        }
    }
   
   /**
    * Private helper method for heapify and heapSort; moves item down heap until it
    * is greater than its children
    **/
    private static void moveDown(Comparable [] A, int first, int last) {
        int largest = 2*first + 1;
        while (largest <= last) {
            if (largest < last && (A[largest]).compareTo(A[largest+1]) < 0) {
                largest++;
            }
            if (((Comparable)A[first]).compareTo(A[largest]) < 0) {
                swap(A, first, largest); // A DATA MOVE
                num_moves++;
                first = largest;
                largest = 2*first + 1;
            }
            else {
                largest = last + 1;
            }
        }
    }
    
   /**
    * Private helper method for sorting functions; swaps array elements
    **/
    private static void swap(Comparable [] A, int i1, int i2) {
        Comparable tmp = A[i1];
        A[i1] = A[i2];
        A[i2] = tmp;
    }
    
    /**
     * Sorts the given array using the insertion sort algorithm. Note: after
     * this method finishes the array is in sorted order.
     *
     * @param A- the array to sort
     **/
    public static void insertionSort(Comparable [] A) {
        if (A != null) {
            Comparable tmp;
            int i, j;
            num_moves = 0;
            if (A.length > 1) {
                for (i = 1; i < A.length; i++) {
                    tmp = A[i];
                    for (j = i; j > 0 && tmp.compareTo(A[j-1]) < 0; j--) {
                        A[j] = A[j-1]; // A DATA MOVE
                        num_moves++;
                    }
                    A[j] = tmp; // A DATA MOVE
                    num_moves++;
                }
            }
        }
    }

    /**
     * Sorts the given array using the selection sort algorithm discussed in
     * the on-line notes (which does more data moves than the one from class).
     * Note: after this method finishes the array is in sorted order.
     *
     * @param A- the array to sort
     **/
    static void selectionSort(Comparable [] A) {
        if (A != null) {
            int i, j, lst;
            num_moves = 0;
            if (A.length > 1) {
                for (i = 0; i < A.length; i++) {
                    lst = i;
                    for (j = i+1; j < A.length; j++) {
                        if (A[j].compareTo(A[lst]) < 0) lst = j;
                    }
                    swap(A, lst, i); // A DATA MOVE
                    num_moves++;
                }
            }
        }
    }

    /**
     * Sorts the given array using the merge sort algorithm. Note: after
     * this method finishes the array is in sorted order.
     *
     * @param A- the array to sort
     **/
    public static void mergeSort(Comparable [] A) {
        if (A != null) {
            if (A.length > 1) {
                num_moves = 0;
                num_moves = mergeSort(A, 0, A.length-1);
            }
            else { // no point in sorting one element
                num_moves = 0;
            }
        }
    }
    
    /**
     * private overloaded version of mergeSort
     **/
    private static int mergeSort(Comparable [] A, int first, int last) {
        int tmp_moves = 0;
        if (first < last) {
            int mid = (first + last)/2;
            tmp_moves = tmp_moves + mergeSort(A, first, mid);
            tmp_moves = tmp_moves + mergeSort(A, mid+1, last);
            tmp_moves = tmp_moves + merge(A, first, last);
            return tmp_moves;
        }
        else {
            return 0;
        }
    }
    
    /**
     * private helper method for mergeSort
     **/
    private static int merge(Comparable [] A, int first, int last) {
        int mid = (first + last)/2;
        int i1 = 0, i2 = first, i3 = mid+1;
        int j;
        int tmp_moves = 0;
        Comparable[] tmp = new Comparable[last-first+1];
        
        while (i2 <= mid && i3 <= last) {
            if (A[i2].compareTo(A[i3]) < 0) {
                tmp[i1++] = A[i2++]; // A DATA MOVE
                tmp_moves++;
            }
            else {
                tmp[i1++] = A[i3++]; // A DATA MOVE
                tmp_moves++;
            }
        }
        while (i2 <= mid) {
            tmp[i1++] = A[i2++]; // A DATA MOVE
            tmp_moves++;
        }
        while (i3 <= last) {
            tmp[i1++] = A[i3++]; // A DATA MOVE
            tmp_moves++;
        }
        for (j = 0; j < last-first+1; j++) {
            A[first+j] = tmp[j]; // A DATA MOVE?
            tmp_moves++;
        }
        return tmp_moves;
    }

    /**
     * Sorts the given array using the quick sort algorithm. Note:
     * after this method finishes the array is in sorted order.
     *
     * @param A- the array to sort
     **/
    public static void quickSort(Comparable [] A) {
        if (A.length < 2) return;
        int max = 0;
        int i;
        num_moves = 0;
        for (i = 1; i < A.length; i++) {
            if (A[max].compareTo(A[i]) < 0) max = i;
        }
        swap(A, A.length-1, max); // A DATA MOVE
        num_moves++;
        num_moves = num_moves + quickSort(A, 0, A.length-2);
    }
    
    /**
     * Sorts the given array using the quick sort algorithm. Note:
     * after this method finishes the array is in sorted order.
     *
     * @param A- the array to sort
     **/
    private static int quickSort(Comparable [] A, int first, int last) {
        int lower = first + 1;
        int upper = last;
        int tmp_moves = 0;
        
        swap(A, first, (first+last)/2);
        tmp_moves++;
        Comparable bound = A[first];
        while (lower <= upper) {
            while (A[lower].compareTo(bound) < 0) {
                lower++;
            }
            while (bound.compareTo(A[upper]) < 0) {
                upper--;
            }
            if (lower < upper) {
                swap(A, lower++, upper--); // A DATA MOVE
                tmp_moves++;
            }
            else {
                lower++;
            }
        }
        swap(A, upper, first);
        if (first < upper-1) {
            tmp_moves = tmp_moves + quickSort(A, first, upper-1);
        }
        if (upper+1 < last) {
            tmp_moves = tmp_moves + quickSort(A, upper+1, last);
        }
        return tmp_moves;
    }
    

    /**
     * Returns the median of the given array. The median is the
     * item in the array that has the same number of items
     * greater than it and less than it. The array is not
     * changed as a result of calling this method.
     *
     * @param A - the array from which to find the median
     * @return the item that is the median of A
     **/
    public static Comparable median(Comparable [] A) {
        Comparable[] tmp = new Comparable[A.length];
        for (int i = 0; i < A.length; i++) tmp[i] = A[i];
        heapSort(tmp);
        return tmp[A.length/2+1];
    }

    /**
     * Returns the mode of the given array. The mode is
     * the largest multiplicity of any item in the array.
     * The multiplicity of an item is the number of times
     * is appears in the array. The array is not changed
     * as a result of calling this method.
     *
     * @param A - the array from which to find the mode
     * @return a string representation of this flower
     **/
     
     // NOTE: THIS ISN'T THE MODE -- THE MODE IS THE
     // CATEGORY WITH THE MOST OCCURRENCES, NOT THE
     // NUMBER OF OCCURRENCES IN THAT CATEGORY!
    public static int mode(Comparable [] A) {
        Comparable[] hist = new Comparable[A.length];  // array to hold categories
        int[] cnt = new int[A.length];                 // array to hold counts
        int num_bins = 0;                              // number of categories
        int i, j;                                      // loop indices
        boolean found;                                 // true if item found
        int max_freq = 0;                              // the highest frequency
        
        for (i = 0; i < A.length; i++) { // go through each element of A
            found = false;
            for (j = 0; j < num_bins; j++) { // go through each bin
                if (A[i].equals(hist[j])) {
                    cnt[j]++;
                    found = true;
                    if (cnt[j] > max_freq) max_freq = cnt[j];
                    break;
                }
            }
            if (!found) {
                hist[num_bins] = A[i];
                cnt[num_bins]++;
                if (cnt[num_bins] > max_freq) max_freq = 1;
                num_bins++;
            }
        }
        
        return max_freq;
    }
     
    /**
     * Sorts the given array using five different sorting algorithms
     * and prints out statistics.
     *
     * The sorts performed are: selection, insertion, merge, quick,
     * and heap. The statistics displayed for each sort are:
     * number of comparisons, number of data moves, and time (in
     * milliseconds).
     *
     * Note: each sort is given the same array (i.e. in the original
     * order) and the input array A is not changed by this method.
     *
     * @param A - the array to sort
     **/
    public static void runAllSorts(SortObject [] A) {
        SortObject [][] brr = new SortObject[5][A.length];
        int i;
        long t_in, t_out;
        long sortTime;
        for (i = 0; i < A.length; i++) {
            brr[0][i] = A[i];
            brr[1][i] = A[i];
            brr[2][i] = A[i];
            brr[3][i] = A[i];
            brr[4][i] = A[i];
        }
        
        // PRINT HEADER
        System.out.println("Sort\t\t\tCompares\tData moves\tMillisecs");
        
        // SELECTION SORT
        brr[2][0].resetCompares();
        t_in = System.currentTimeMillis();
        selectionSort(brr[0]);
        t_out = System.currentTimeMillis();
        sortTime = t_out - t_in;
        System.out.println("Selection sort:\t\t" + brr[0][0].getCompares() + "\t\t" + 
            num_moves + "\t\t" + sortTime);
        
        // INSERTION SORT
        brr[1][0].resetCompares();
        t_in = System.currentTimeMillis();
        insertionSort(brr[1]);
        t_out = System.currentTimeMillis();
        sortTime = t_out - t_in;
        System.out.println("Insertion sort:\t\t" + brr[1][0].getCompares() + "\t\t" + 
            num_moves + "\t\t" + sortTime);
        
        // MERGE SORT
        brr[3][0].resetCompares();
        t_in = System.currentTimeMillis();
        mergeSort(brr[2]);
        t_out = System.currentTimeMillis();
        sortTime = t_out - t_in;
        System.out.println("Merge sort:\t\t" + brr[2][0].getCompares() + "\t\t" + 
            num_moves + "\t\t" + sortTime);
        
        // QUICK SORT
        brr[4][0].resetCompares();
        t_in = System.currentTimeMillis();
        quickSort(brr[3]);
        t_out = System.currentTimeMillis();
        sortTime = t_out - t_in;
        System.out.println("Quick sort:\t\t" + brr[3][0].getCompares() + "\t\t" + 
            num_moves + "\t\t" + sortTime);
        
        // HEAP SORT
        brr[0][0].resetCompares();
        t_in = System.currentTimeMillis();
        heapSort(brr[4]);
        t_out = System.currentTimeMillis();
        sortTime = t_out - t_in;
        System.out.println("Heap sort:\t\t" + brr[4][0].getCompares() + "\t\t" + 
            num_moves + "\t\t" + sortTime);
    }
   
   /**
    * main for testing
    **/
   public static void main(String [] args) throws IOException {
           // Create the input array of unsorted objects.
        SortObject[] arr = new SortObject[100000];
        int seed = 232323;
        int i;

        // It is important to give the seed so you can reproduce results.
        Random random = new Random(seed);
        for (i = 0; i < arr.length; i++) {
            arr[i] = new SortObject(random.nextInt());
        }
        
        runAllSorts(arr);
        median(arr);
        mode(arr);
   }
}


