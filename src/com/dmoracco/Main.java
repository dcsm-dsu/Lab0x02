package com.dmoracco;

import java.util.Random;

import static com.dmoracco.GetCpuTime.getCpuTime;

public class Main {

    public static void main(String[] args) {

        ValidateSortList();

        runTimeTests(3000000, 1, Integer.MAX_VALUE);
    }

    static void PrintList(byte[][] list, int N, int k) {

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < k; j++) {
                System.out.print((char) list[i][j]);
            }
            System.out.print(" ");
        }
        System.out.println();

    }

    public static byte[][] GenerateTestList(int N, int k, int minV, int maxV) {

        if (minV < 1 || maxV > 255) throw new IllegalArgumentException("GenerateTestList: bad min/max range");

        Random r = new Random();
        byte[][] newList = new byte[N][];
        byte[] newArray;
        byte randomByte;

        // Generate N random byte arrays of size k (plus terminator)
        for (int i = 0; i < N; i++) {
            // Generate random byte array
            newArray = new byte[k + 1];
            for (int j = 0; j < k; j++) {
                randomByte = (byte) (Math.abs(r.nextInt(maxV - minV) + minV));
                newArray[j] = randomByte;
            }
            // terminate array
            newArray[k] = 0;
            // add to array list.
            newList[i] = newArray;
        }

        return newList;
    }

    static void ValidateSortList() {
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

        System.out.printf("\nSHORT LIST VALIDATION:\n\n");
        System.out.print("Unsorted: ");
        PrintList(randomList, N, k);

        InsertionSort(insertionList, k);
        System.out.print("Insertn : ");
        PrintList(insertionList, N, k);

        System.out.print("Merged  : ");
        MergeSort(mergeList, k);
        PrintList(mergeList, N, k);

        System.out.print("Quicksrt: ");
        QuickSort(quickList, 0, N - 1);
        PrintList(quickList, N, k);

        System.out.print("Radixsrt: ");
        RadixSort(radixList, N, 1, k);
        PrintList(radixList, N, k);

        k = k * 4;
        N = 10000;
        minV = 1;
        maxV = 255;

        randomList = GenerateTestList(10000, k, minV, maxV);
        quickList = randomList;
        mergeList = randomList;
        insertionList = randomList;
        radixList = randomList;

        boolean[] testresults = new boolean[4];
        for (int z = 0; z < 4; z++) {
            testresults[z] = true;
        }

        System.out.printf("\nLARGE SORT VALIDATION:\n");

        InsertionSort(insertionList, k);
        MergeSort(mergeList, k);
        QuickSort(quickList, 0, N - 1);
        RadixSort(radixList, N, 1, k);

        for (int t = 1; t < N; t++) {
            if (testresults[0] == true && !isLessThan(insertionList[t - 1], insertionList[t])) {
                //System.out.println("FAILED AT " + t);
                testresults[0] = false;
            }
        }

        System.out.print("Insertn : ");
        if (testresults[0] == true) {
            System.out.println("GOOD");
        } else {
            System.out.println("FAIL");
        }
        System.out.print("Merged  : ");
        if (testresults[1] == true) {
            System.out.println("GOOD");
        } else {
            System.out.println("FAIL");
        }
        System.out.print("Quicksrt: ");
        if (testresults[2] == true) {
            System.out.println("GOOD");
        } else {
            System.out.println("FAIL");
        }
        System.out.print("Radixsrt: ");
        if (testresults[3] == true) {
            System.out.println("GOOD");
        } else {
            System.out.println("FAIL");
        }

    }

    static void InsertionSort(byte[][] list, int k) {

        int arrayNumber = 1;
        int letter = 1;
        int j;
        byte[] tempArray = new byte[k + 1];

        // loop through list
        while (arrayNumber < list.length) {
            j = arrayNumber;
            // Determine if swap is needed and move to ordered location
            while (j > 0 && needsSwapped(list[j - 1], list[j])) {
                //swap
                tempArray = list[j - 1];
                list[j - 1] = list[j];
                list[j] = tempArray;
                // decrement j
                j--;
            }
            // increment arrayNumber
            arrayNumber++;
        }
    }

    static byte[][] MergeSort(byte[][] list, int k) {

        int size = list.length;

        // One or less is sorted by definition
        if (size <= 1) return list;

        // Create new arrays
        int split = (int) Math.ceil(size / 2.0); // get proper size for odd N, 2.0 cause int arithmetic apparently...
        byte[][] left = new byte[split][];
        byte[][] right = new byte[size / 2][];

        // Split list
        int l = 0;
        int r = 0;
        for (int i = 0; i < size; i++) {
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

    static byte[][] mergeArrays(byte[][] left, byte[][] right, int k) {

        int l = 0, r = 0, m = 0;
        int lsize = left.length;
        int rsize = right.length;
        byte[][] mergedList = new byte[lsize + rsize][];

        // iterate through list until either list is complete
        while (l < lsize && r < rsize) {
            // determine order to fill mergedList
            if (needsSwapped(right[r], left[l])) mergedList[m++] = left[l++];
            else mergedList[m++] = right[r++];
        }

        // Handle remainder for odd N lists
        while (l < lsize) mergedList[m++] = left[l++];
        while (r < rsize) mergedList[m++] = right[r++];

        return mergedList;
    }

    static void QuickSort(byte[][] list, int lo, int hi) {
        if (lo < hi) {
            int p = partition(list, lo, hi);
            QuickSort(list, lo, p - 1);
            QuickSort(list, p + 1, hi);
        }
    }

    static int partition(byte[][] list, int lo, int hi) {
        byte[] pivot = list[hi];
        int i = lo;

        for (int j = lo; j <= hi; j++) {
            if (isLessThan(list[j], pivot)) {
                swap(list, i, j);
                i++;
            }
            swap(list, i, hi);
        }

        return i;
    }

    static void swap(byte[][] list, int a, int b) {
        byte[] temp = list[a];
        list[a] = list[b];
        list[b] = temp;
    }

    static boolean isLessThan(byte[] previousList, byte[] currentList) {
        if (previousList == currentList) return false;
        int i = -1;
        // return immediately if out of order, otherwise increment through array
        do {
            i++;
            if (previousList[i] < currentList[i]) return true;
        } while (i < previousList.length && previousList[i] == currentList[i]);

        return false;
    }

    static void RadixSort(byte[][] list, int N, int d, int k) {
        // Mostly from Creel video.

        byte[][] newList = new byte[N][];
        int counterSize = (int) Math.pow(256.0, d);
        int[] counter = new int[counterSize];


        for (int shift = 0, s = k - 1; shift < k; shift++, s--) {

            for (int i = 0; i < counterSize; i++) {
                counter[i] = 0;
            }

            for (int j = 0; j < N; j++) {
                //+128 ugly hack to handle negative integer interpretation of byte
                counter[(list[j][s]) + 128]++;
            }

            for (int p = 1; p < 256; p++) {
                counter[p] += counter[p - 1];
            }

            for (int b = N - 1; b >= 0; b--) {
                //+128 ugly hack to handle negative integer interpretation of byte
                int index = (list[b][s]) + 128;
                newList[--counter[index]] = list[b];
            }

            byte[][] tmp = list;
            list = newList;
            newList = tmp;
        }
    }

    static boolean needsSwapped(byte[] previousList, byte[] currentList) {
        int i = -1;
        // return immediately if out of order, otherwise increment through array
        do {
            i++;
            if (previousList[i] > currentList[i]) return true;
        } while (i < previousList.length && previousList[i] == currentList[i]);

        return false;
    }

    public static void runTimeTests(long maximumTime, int min_N, int max_N) {

        long averageTime6 = 0;
        long averageTime12 = 0;
        long averageTime24 = 0;
        long averageTime48 = 0;
        long totalTime = 0;
        long startTime = 0;
        long endTime = 0;
        String actualRatio6 = "na";
        String actualRatio12 = "na";
        String actualRatio24 = "na";
        String actualRatio48 = "na";
        String expectedRatio = "na";
        String timeOutput6 = "na";
        String timeOutput12 = "na";
        String timeOutput24 = "na";
        String timeOutput48 = "na";
        double previousTime6 = 0;
        double previousTime12 = 0;
        double previousTime24 = 0;
        double previousTime48 = 0;
        int MININT = Integer.MIN_VALUE;
        int MAXINT = Integer.MAX_VALUE;

        // INSERTION SORT
        // Print Header
        System.out.printf("\n\nINSERTION SORT:");
        System.out.printf("\n%10s%-16s     %-16s     %-16s     %-16s", "", "k = 6", "k = 12", "k = 24", "k = 48");
        System.out.printf("\n%-10s%-10s%-6s     %-10s%-6s     %-10s%-6s     %-10s%-6s   %-12s\n\n",
                "N", "Time", "Ratio", "Time", "Ratio", "Time", "Ratio", "Time", "Ratio", "Expected Ratio");

        for (int n = min_N; n < max_N; n = n * 2) {

            if (averageTime48 == -1 && averageTime24 == -1 && averageTime12 == -1 && averageTime6 == -1 ){

                averageTime6 = 0;
                averageTime12 = 0;
                averageTime24 = 0;
                averageTime48 = 0;
                previousTime6 = 0;
                previousTime12 = 0;
                previousTime24 = 0;
                previousTime48 = 0;
                break;

            }

            // Run tests
            if (previousTime6 < maximumTime) {
                for (int run6 = 0; run6 < 100; run6++) {
                    byte[][] randomList6 = GenerateTestList(n, 6, 1, 255);
                    startTime = getCpuTime();
                    InsertionSort(randomList6, 6);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime6 = averageTime6;
                averageTime6 = totalTime / 100;
                totalTime = 0;

            } else averageTime6 = -1;

            if (previousTime12 < maximumTime) {
                for (int run12 = 0; run12 < 100; run12++) {
                    byte[][] randomList12 = GenerateTestList(n, 12, 1, 255);
                    startTime = getCpuTime();
                    InsertionSort(randomList12, 12);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime12 = averageTime12;
                averageTime12 = totalTime / 100;
                totalTime = 0;

            } else averageTime12 = -1;

            if (previousTime24 < maximumTime) {
                for (int run24 = 0; run24 < 100; run24++) {
                    byte[][] randomList24 = GenerateTestList(n, 24, 1, 255);
                    startTime = getCpuTime();
                    InsertionSort(randomList24, 24);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime24 = averageTime24;
                averageTime24 = totalTime / 100;
                totalTime = 0;

            } else averageTime24 = -1;

            if (previousTime48 < maximumTime) {
                for (int run48 = 0; run48 < 100; run48++) {
                    byte[][] randomList48 = GenerateTestList(n, 48, 1, 255);
                    startTime = getCpuTime();
                    InsertionSort(randomList48, 48);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime48 = averageTime48;
                averageTime48 = totalTime / 100;
                totalTime = 0;

            } else averageTime48 = -1;

            // Calculate Ratios
            if (previousTime6 != 0) {
                actualRatio6 = String.format("%2.2f", ((averageTime6 / previousTime6)));
            }
            if (previousTime12 != 0) {
                actualRatio12 = String.format("%2.2f", ((averageTime12 / previousTime12)));
            }
            if (previousTime24 != 0) {
                actualRatio24 = String.format("%2.2f", ((averageTime24 / previousTime24)));
            }
            if (previousTime48 != 0) {
                actualRatio48 = String.format("%2.2f", ((averageTime48 / previousTime48)));
            }
/*
            if (n > 1){
                seqExpectedRatio = "~2";
                if (n>2){
                    binExpectedRatio = String.format("~%2.2f", (double)(Math.log(n)/Math.log(2))/(Math.log(n/2)/Math.log(2)));
                } else {
                    binExpectedRatio = "na";
                }

            } else {
                binExpectedRatio = "na";
                seqExpectedRatio = "na";
            }
*/

            if (averageTime6 != -1){
                timeOutput6 = Long.toString(averageTime6);
            } else {
                timeOutput6 = "na";
                actualRatio6 = "na";
            }
            if (averageTime12 != -1){
                timeOutput12 = Long.toString(averageTime12);
            } else {
                timeOutput12 = "na";
                actualRatio12 = "na";
            }
            if (averageTime24 != -1){
                timeOutput24 = Long.toString(averageTime24);
            } else {
                timeOutput24 = "na";
                actualRatio24 = "na";
            }
            if (averageTime48 != -1){
                timeOutput48 = Long.toString(averageTime48);
            } else {
                timeOutput48 = "na";
                actualRatio48 = "na";
            }

            //print test results
            System.out.printf("%-10s%-10s%-6s     %-10s%-6s     %-10s%-6s     %-10s%-6s   %-12s\n",
                    n, timeOutput6, actualRatio6, timeOutput12, actualRatio12,
                    timeOutput24, actualRatio24, timeOutput48, actualRatio48,
                    expectedRatio);


        }


        // MERGE SORT
        // Print Header
        System.out.printf("\n\nMERGE SORT:");
        System.out.printf("\n%10s%-16s     %-16s     %-16s     %-16s", "", "k = 6", "k = 12", "k = 24", "k = 48");
        System.out.printf("\n%-10s%-10s%-6s     %-10s%-6s     %-10s%-6s     %-10s%-6s   %-12s\n\n",
                "N", "Time", "Ratio", "Time", "Ratio", "Time", "Ratio", "Time", "Ratio", "Expected Ratio");

        for (int n = min_N; n < max_N; n = n * 2) {

            if (averageTime48 == -1 && averageTime24 == -1 && averageTime12 == -1 && averageTime6 == -1 ){

                averageTime6 = 0;
                averageTime12 = 0;
                averageTime24 = 0;
                averageTime48 = 0;
                previousTime6 = 0;
                previousTime12 = 0;
                previousTime24 = 0;
                previousTime48 = 0;
                break;

            }

            // Run tests
            if (previousTime6 < maximumTime) {
                for (int run6 = 0; run6 < 100; run6++) {
                    byte[][] randomList6 = GenerateTestList(n, 6, 1, 255);
                    startTime = getCpuTime();
                    MergeSort(randomList6, 6);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime6 = averageTime6;
                averageTime6 = totalTime / 100;
                totalTime = 0;

            } else averageTime6 = -1;

            if (previousTime12 < maximumTime) {
                for (int run12 = 0; run12 < 100; run12++) {
                    byte[][] randomList12 = GenerateTestList(n, 12, 1, 255);
                    startTime = getCpuTime();
                    MergeSort(randomList12, 12);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime12 = averageTime12;
                averageTime12 = totalTime / 100;
                totalTime = 0;

            } else averageTime12 = -1;

            if (previousTime24 < maximumTime) {
                for (int run24 = 0; run24 < 100; run24++) {
                    byte[][] randomList24 = GenerateTestList(n, 24, 1, 255);
                    startTime = getCpuTime();
                    MergeSort(randomList24, 24);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime24 = averageTime24;
                averageTime24 = totalTime / 100;
                totalTime = 0;

            } else averageTime24 = -1;

            if (previousTime48 < maximumTime) {
                for (int run48 = 0; run48 < 100; run48++) {
                    byte[][] randomList48 = GenerateTestList(n, 48, 1, 255);
                    startTime = getCpuTime();
                    MergeSort(randomList48, 48);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime48 = averageTime48;
                averageTime48 = totalTime / 100;
                totalTime = 0;

            } else averageTime48 = -1;

            // Calculate Ratios
            if (previousTime6 != 0) {
                actualRatio6 = String.format("%2.2f", ((averageTime6 / previousTime6)));
            }
            if (previousTime12 != 0) {
                actualRatio12 = String.format("%2.2f", ((averageTime12 / previousTime12)));
            }
            if (previousTime24 != 0) {
                actualRatio24 = String.format("%2.2f", ((averageTime24 / previousTime24)));
            }
            if (previousTime48 != 0) {
                actualRatio48 = String.format("%2.2f", ((averageTime48 / previousTime48)));
            }
/*
            if (n > 1){
                seqExpectedRatio = "~2";
                if (n>2){
                    binExpectedRatio = String.format("~%2.2f", (double)(Math.log(n)/Math.log(2))/(Math.log(n/2)/Math.log(2)));
                } else {
                    binExpectedRatio = "na";
                }

            } else {
                binExpectedRatio = "na";
                seqExpectedRatio = "na";
            }
*/

            if (averageTime6 != -1){
                timeOutput6 = Long.toString(averageTime6);
            } else {
                timeOutput6 = "na";
                actualRatio6 = "na";
            }
            if (averageTime12 != -1){
                timeOutput12 = Long.toString(averageTime12);
            } else {
                timeOutput12 = "na";
                actualRatio12 = "na";
            }
            if (averageTime24 != -1){
                timeOutput24 = Long.toString(averageTime24);
            } else {
                timeOutput24 = "na";
                actualRatio24 = "na";
            }
            if (averageTime48 != -1){
                timeOutput48 = Long.toString(averageTime48);
            } else {
                timeOutput48 = "na";
                actualRatio48 = "na";
            }

            //print test results
            System.out.printf("%-10s%-10s%-6s     %-10s%-6s     %-10s%-6s     %-10s%-6s   %-12s\n",
                    n, timeOutput6, actualRatio6, timeOutput12, actualRatio12,
                    timeOutput24, actualRatio24, timeOutput48, actualRatio48,
                    expectedRatio);


        }


        // QUICK SORT
        // Print Header
        System.out.printf("\n\nQUICK SORT:");
        System.out.printf("\n%10s%-16s     %-16s     %-16s     %-16s", "", "k = 6", "k = 12", "k = 24", "k = 48");
        System.out.printf("\n%-10s%-10s%-6s     %-10s%-6s     %-10s%-6s     %-10s%-6s   %-12s\n\n",
                "N", "Time", "Ratio", "Time", "Ratio", "Time", "Ratio", "Time", "Ratio", "Expected Ratio");

        for (int n = min_N; n < max_N; n = n * 2) {

            if (averageTime48 == -1 && averageTime24 == -1 && averageTime12 == -1 && averageTime6 == -1 ){

                averageTime6 = 0;
                averageTime12 = 0;
                averageTime24 = 0;
                averageTime48 = 0;
                previousTime6 = 0;
                previousTime12 = 0;
                previousTime24 = 0;
                previousTime48 = 0;
                break;

            }

            // Run tests
            if (previousTime6 < maximumTime) {
                for (int run6 = 0; run6 < 100; run6++) {
                    byte[][] randomList6 = GenerateTestList(n, 6, 1, 255);
                    startTime = getCpuTime();
                    QuickSort(randomList6, 0, n-1);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime6 = averageTime6;
                averageTime6 = totalTime / 100;
                totalTime = 0;

            } else averageTime6 = -1;

            if (previousTime12 < maximumTime) {
                for (int run12 = 0; run12 < 100; run12++) {
                    byte[][] randomList12 = GenerateTestList(n, 12, 1, 255);
                    startTime = getCpuTime();
                    QuickSort(randomList12, 0, n-1);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime12 = averageTime12;
                averageTime12 = totalTime / 100;
                totalTime = 0;

            } else averageTime12 = -1;

            if (previousTime24 < maximumTime) {
                for (int run24 = 0; run24 < 100; run24++) {
                    byte[][] randomList24 = GenerateTestList(n, 24, 1, 255);
                    startTime = getCpuTime();
                    QuickSort(randomList24, 0, n-1);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime24 = averageTime24;
                averageTime24 = totalTime / 100;
                totalTime = 0;

            } else averageTime24 = -1;

            if (previousTime48 < maximumTime) {
                for (int run48 = 0; run48 < 100; run48++) {
                    byte[][] randomList48 = GenerateTestList(n, 48, 1, 255);
                    startTime = getCpuTime();
                    QuickSort(randomList48, 0, n-1);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime48 = averageTime48;
                averageTime48 = totalTime / 100;
                totalTime = 0;

            } else averageTime48 = -1;

            // Calculate Ratios
            if (previousTime6 != 0) {
                actualRatio6 = String.format("%2.2f", ((averageTime6 / previousTime6)));
            }
            if (previousTime12 != 0) {
                actualRatio12 = String.format("%2.2f", ((averageTime12 / previousTime12)));
            }
            if (previousTime24 != 0) {
                actualRatio24 = String.format("%2.2f", ((averageTime24 / previousTime24)));
            }
            if (previousTime48 != 0) {
                actualRatio48 = String.format("%2.2f", ((averageTime48 / previousTime48)));
            }
/*
            if (n > 1){
                seqExpectedRatio = "~2";
                if (n>2){
                    binExpectedRatio = String.format("~%2.2f", (double)(Math.log(n)/Math.log(2))/(Math.log(n/2)/Math.log(2)));
                } else {
                    binExpectedRatio = "na";
                }

            } else {
                binExpectedRatio = "na";
                seqExpectedRatio = "na";
            }
*/

            if (averageTime6 != -1){
                timeOutput6 = Long.toString(averageTime6);
            } else {
                timeOutput6 = "na";
                actualRatio6 = "na";
            }
            if (averageTime12 != -1){
                timeOutput12 = Long.toString(averageTime12);
            } else {
                timeOutput12 = "na";
                actualRatio12 = "na";
            }
            if (averageTime24 != -1){
                timeOutput24 = Long.toString(averageTime24);
            } else {
                timeOutput24 = "na";
                actualRatio24 = "na";
            }
            if (averageTime48 != -1){
                timeOutput48 = Long.toString(averageTime48);
            } else {
                timeOutput48 = "na";
                actualRatio48 = "na";
            }

            //print test results
            System.out.printf("%-10s%-10s%-6s     %-10s%-6s     %-10s%-6s     %-10s%-6s   %-12s\n",
                    n, timeOutput6, actualRatio6, timeOutput12, actualRatio12,
                    timeOutput24, actualRatio24, timeOutput48, actualRatio48,
                    expectedRatio);


        }


        // RADIX SORT
        // Print Header
        System.out.printf("\n\nRADIX SORT:");
        System.out.printf("\n%10s%-16s     %-16s     %-16s     %-16s", "", "k = 6", "k = 12", "k = 24", "k = 48");
        System.out.printf("\n%-10s%-10s%-6s     %-10s%-6s     %-10s%-6s     %-10s%-6s   %-12s\n\n",
                "N", "Time", "Ratio", "Time", "Ratio", "Time", "Ratio", "Time", "Ratio", "Expected Ratio");

        for (int n = min_N; n < max_N; n = n * 2) {

            if (averageTime48 == -1 && averageTime24 == -1 && averageTime12 == -1 && averageTime6 == -1 ){
                break;

            }

            // Run tests
            if (previousTime6 < maximumTime) {
                for (int run6 = 0; run6 < 100; run6++) {
                    byte[][] randomList6 = GenerateTestList(n, 6, 1, 255);
                    startTime = getCpuTime();
                    RadixSort(randomList6, n, 1, 6);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime6 = averageTime6;
                averageTime6 = totalTime / 100;
                totalTime = 0;

            } else averageTime6 = -1;

            if (previousTime12 < maximumTime) {
                for (int run12 = 0; run12 < 100; run12++) {
                    byte[][] randomList12 = GenerateTestList(n, 12, 1, 255);
                    startTime = getCpuTime();
                    RadixSort(randomList12, n, 1, 12);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime12 = averageTime12;
                averageTime12 = totalTime / 100;
                totalTime = 0;

            } else averageTime12 = -1;

            if (previousTime24 < maximumTime) {
                for (int run24 = 0; run24 < 100; run24++) {
                    byte[][] randomList24 = GenerateTestList(n, 24, 1, 255);
                    startTime = getCpuTime();
                    RadixSort(randomList24, n, 1, 24);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime24 = averageTime24;
                averageTime24 = totalTime / 100;
                totalTime = 0;

            } else averageTime24 = -1;

            if (previousTime48 < maximumTime) {
                for (int run48 = 0; run48 < 100; run48++) {
                    byte[][] randomList48 = GenerateTestList(n, 48, 1, 255);
                    startTime = getCpuTime();
                    RadixSort(randomList48, n, 1, 48);
                    endTime = getCpuTime();
                    totalTime = totalTime + (endTime - startTime);
                }
                previousTime48 = averageTime48;
                averageTime48 = totalTime / 100;
                totalTime = 0;

            } else averageTime48 = -1;

            // Calculate Ratios
            if (previousTime6 != 0) {
                actualRatio6 = String.format("%2.2f", ((averageTime6 / previousTime6)));
            }
            if (previousTime12 != 0) {
                actualRatio12 = String.format("%2.2f", ((averageTime12 / previousTime12)));
            }
            if (previousTime24 != 0) {
                actualRatio24 = String.format("%2.2f", ((averageTime24 / previousTime24)));
            }
            if (previousTime48 != 0) {
                actualRatio48 = String.format("%2.2f", ((averageTime48 / previousTime48)));
            }
/*
            if (n > 1){
                seqExpectedRatio = "~2";
                if (n>2){
                    binExpectedRatio = String.format("~%2.2f", (double)(Math.log(n)/Math.log(2))/(Math.log(n/2)/Math.log(2)));
                } else {
                    binExpectedRatio = "na";
                }

            } else {
                binExpectedRatio = "na";
                seqExpectedRatio = "na";
            }
*/

            if (averageTime6 != -1){
                timeOutput6 = Long.toString(averageTime6);
            } else {
                timeOutput6 = "na";
                actualRatio6 = "na";
            }
            if (averageTime12 != -1){
                timeOutput12 = Long.toString(averageTime12);
            } else {
                timeOutput12 = "na";
                actualRatio12 = "na";
            }
            if (averageTime24 != -1){
                timeOutput24 = Long.toString(averageTime24);
            } else {
                timeOutput24 = "na";
                actualRatio24 = "na";
            }
            if (averageTime48 != -1){
                timeOutput48 = Long.toString(averageTime48);
            } else {
                timeOutput48 = "na";
                actualRatio48 = "na";
            }

            //print test results
            System.out.printf("%-10s%-10s%-6s     %-10s%-6s     %-10s%-6s     %-10s%-6s   %-12s\n",
                    n, timeOutput6, actualRatio6, timeOutput12, actualRatio12,
                    timeOutput24, actualRatio24, timeOutput48, actualRatio48,
                    expectedRatio);


        }

    }
}
