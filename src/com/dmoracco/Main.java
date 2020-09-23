package com.dmoracco;

import java.util.Random;

public class Main {

    public static void main(String[] args) {

        try{

            byte[][] randomList = new byte[3][];
            randomList = GenerateTestList(3, 5, 65, 90);

        } catch(Exception e){
            System.out.println("Error generating random arrays: " + e.getMessage());
        }


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
}
