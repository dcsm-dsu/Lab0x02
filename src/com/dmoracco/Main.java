package com.dmoracco;

import java.util.Random;

public class Main {

    public static void main(String[] args) {

        int N = 15;
        int k = 10;
        int minV = 65;
        int maxV = 70;

        ValidateSortList();

        try{

            byte[][] randomList = new byte[N][];
            randomList = GenerateTestList(N, k, minV, maxV);


        } catch(Exception e){
            System.out.println("Error generating random arrays: " + e.getMessage());
        }


    }

    static void PrintList(byte[][] list, int N, int k){

        for (int i = 0; i < N; i++){
            for (int j = 0; j < k; j++){
                System.out.print((char)list[i][j] );
            }
            System.out.print(" ");
        }
        System.out.println();

    }

    public static byte[][] GenerateTestList(int N, int k, int minV, int maxV){

        if (minV < 1 || maxV > 255) throw new IllegalArgumentException("GenerateTestList: bad min/max range");

        Random r = new Random();
        byte[][] newList = new byte[N][];
        byte[] newArray;
        byte randomByte;

        // Generate N random byte arrays of size k (plus terminator)
        for (int i = 0; i < N; i++){
            // Generate random byte array
            newArray = new byte[k+1];
            for (int j = 0; j < k; j++){
                randomByte = (byte) (r.nextInt(maxV-minV) + minV+1);
                newArray[j] = (byte) randomByte;
            }
            // terminate array
            newArray[k] = 0;
            // add to array list.
            newList[i] = newArray;
        }

        return newList;
    }

    static void ValidateSortList(){
        int N = 10;
        int k = 4;
        int minV = 65;
        int maxV = 70;

        byte[][] randomList;
        byte[][] insertionList;
        byte[][] mergeList;
        byte[][] quickList;
        byte[][] radixList;

        randomList = GenerateTestList(N, k, minV, maxV);
        insertionList = randomList;
        mergeList = randomList;
        quickList = randomList;
        radixList = randomList;

        System.out.print("Unsorted: ");
        PrintList(randomList, N, k);

        InsertionSort(insertionList, k);
        System.out.print("Insertn : ");
        PrintList(insertionList, N, k);

        System.out.print("Merged  : ");
        MergeSort(mergeList, k);
        PrintList(mergeList, N, k);

        System.out.print("Quicksrt: ");
        QuickSort(quickList, 0, N-1);
        PrintList(mergeList, N, k);

/*
        System.out.print("Radixsrt: ");
        RadixSort(radixList, N, 1, k);
        PrintList(mergeList, N, k);
*/

    }

    static void InsertionSort(byte[][] list, int k){

        int arrayNumber = 1;
        int letter = 1;
        int j;
        byte[] tempArray = new byte[k+1];

        // loop through list
        while (arrayNumber < list.length){
           j = arrayNumber;
           // Determine if swap is needed and move to ordered location
           while (j > 0 && needsSwapped(list[j-1], list[j])){
               //swap
               tempArray = list[j-1];
               list[j-1] = list[j];
               list[j] = tempArray;
               // decrement j
               j--;
           }
           // increment arrayNumber
           arrayNumber++;
        }
    }

    static byte[][] MergeSort(byte[][] list, int k){

        int size = list.length;

        // One or less is sorted by definition
        if (size <= 1) return list;

        // Create new arrays
        int split = (int) Math.ceil(size/2.0); // get proper size for odd N, 2.0 cause int arithmetic apparently...
        byte[][] left = new byte[split][];
        byte[][] right = new byte[size/2][];

        // Split list
        int l = 0;
        int r = 0;
        for (int i = 0; i < size; i++){
            if (i < split) left[l++] = list[i];
            else right[r++] = list[i];
        }

        // Recursive calls
        left = MergeSort(left, k);
        right = MergeSort(right, k);

        // Merge sorted lists
        return mergeArrays(left, right, k);

        // Psuedocode from https://en.wikipedia.org/wiki/Merge_sort
    }

    static byte[][] mergeArrays(byte[][] left, byte[][] right, int k){

        int l = 0, r = 0, m = 0;
        int lsize = left.length;
        int rsize = right.length;
        byte[][] mergedList = new byte[lsize + rsize][];

        // iterate through list until either list is complete
        while (l < lsize && r < rsize){
            // determine order to fill mergedList
            if (needsSwapped(right[r], left[l])) mergedList[m++] = left[l++];
            else mergedList[m++] = right[r++];
        }

        // Handle remainder for odd N lists
        while (l < lsize) mergedList[m++] = left[l++];
        while (r < rsize) mergedList[m++] = right[r++];

        return mergedList;
    }

    static void QuickSort(byte[][] list, int lo, int hi){
        if (lo < hi){
            int p = partition(list, lo, hi);
            QuickSort(list, lo, p-1);
            QuickSort(list, p, hi);
        }
    }

    static int partition(byte[][] list, int lo, int hi){
        byte[] pivot = list[hi];
        int i = lo;

        for (int j = lo; j <= hi; j++){
            if (isLessThan(list[j], pivot)){
                swap(list, i, j);
                i++;
            }
            swap(list, i, hi);
        }

        return i;
    }

    static void swap(byte[][] list, int a, int b){
        byte[] temp = list[a];
        list[a] = list[b];
        list[b] = temp;
    }

    static boolean isLessThan(byte[] previousList, byte[] currentList){
        if (previousList == currentList) return false;
        int i = -1;
        // return immediately if out of order, otherwise increment through array
        do {
            i++;
            if (previousList[i] < currentList[i]) return true;
        } while (i < previousList.length && previousList[i] == currentList[i]);

        return false;
    }

    static void RadixSort(byte[][] list, int N, int d, int k){
        // Mostly from Creel video.

        byte[][] newList = new byte[N][];
        int counterSize = (int) Math.pow(256.0, d);
        int[] counter = new int[counterSize];

        for (int shift = 0, s = 0; shift < k; shift++, s++){

            for (int i = 0; i < counterSize; i++){
                counter[i] = 0;
            }

            for (int j = 0; j < N; j++){
                counter[(int)list[j][s]]++;
            }

            for (int p = 1; p < 256; p++){
                counter[p] += counter[p-1];
            }

            for (int b = N-1; b >= 0; b--){
                int index = list[b][s];
                newList[--counter[index]] = list[b];
            }

            list = newList;
        }
    }

    static boolean needsSwapped(byte[] previousList, byte[] currentList){
        int i = -1;
        // return immediately if out of order, otherwise increment through array
        do {
            i++;
            if (previousList[i] > currentList[i]) return true;
        } while (i < previousList.length && previousList[i] == currentList[i]);

        return false;
    }
}
