package com.dmoracco;

import java.util.Random;

public class Main {

    public static void main(String[] args) {

        int N = 15;
        int k = 10;
        int minV = 65;
        int maxV = 70;

        try{
            ValidateSortList();
        } catch(Exception e){
            System.out.println("Error validating sorting algorithms: " + e.getMessage());
        }

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

        byte[][] randomList = new byte[N][];
        byte[][] insertionList = new byte[N][];
        randomList = GenerateTestList(N, k, minV, maxV);
        insertionList = randomList;

        System.out.print("Unsorted: ");
        PrintList(randomList, N, k);
        InsertionSort(insertionList, k);
        System.out.print("Insertn : ");
        PrintList(insertionList, N, k);


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
