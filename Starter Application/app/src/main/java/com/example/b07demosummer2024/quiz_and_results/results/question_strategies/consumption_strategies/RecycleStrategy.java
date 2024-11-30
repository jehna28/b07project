package com.example.b07demosummer2024.quiz_and_results.results.question_strategies.consumption_strategies;

import android.content.Context;
import android.util.Log;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;

import java.util.ArrayList;

public class RecycleStrategy implements QuestionStrategy {

    final String[] recycleFreqs = new String[]{ //
            "Never",
            "Occasionally",
            "Frequently",
            "Always"};
    final String[] clothesFreqs = new String[]{ //
            "Monthly",
            "Quarterly",
            "Annually",
            "Rarely"};
    final String[] deviceNums = new String[]{ //
            "None",
            "1",
            "2",
            "3",
            "4 or more"};
    final double[][] clothesReduction = { //[recycle][clothes]
            // Monthly, Quarterly, Annual, Rarely
            {0, 0, 0, 0},    // Never
            {54, 18, 15, 0.75}, // Occasional
            {108, 36, 30, 1.5}, // Frequent
            {180, 60, 50, 2.5}}; // Always
    final double[][] deviceReduction = { //[recycle][device]
            //None, 1, 2, 3, 4 or more
            {0, 0, 0, 0, 0},     // Never
            {0, 45, 60, 90, 120},    // Occasional
            {0, 60, 120, 180, 240}, // Frequent
            {0, 90, 180, 270, 360}}; // Always

    @Override
    public double getEmissions(ArrayList<QuestionData> data, String response) {
        double total = 0;
        String newClothes = "";
        String numDevices = "";
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getQuestion().equals("How often do you buy new clothes?")){
                newClothes = data.get(i).getResponse();
            }
            else if (data.get(i).getQuestion().equals("How many electronic devices (phones, laptops, etc.) have you purchased in the past year?")){
                numDevices = data.get(i).getResponse();
            }
        }
        int clothesIndex = getClothesIndex(newClothes);
        int deviceIndex = getDeviceIndex(numDevices);
        int recycleIndex = getRecycleIndex(response);
        //Log.d("recycleStrategy", "clothes reducing by " + String.valueOf(clothesReduction[recycleIndex][clothesIndex]));
        //Log.d("recycleStrategy", "device reducing by " + String.valueOf(deviceReduction[recycleIndex][deviceIndex]));
        total -= deviceReduction[recycleIndex][deviceIndex];
        total -= clothesReduction[recycleIndex][clothesIndex];
        //Log.d("recycleStrategy", "returning " + String.valueOf(total));
        return total;
    }

    private int getDeviceIndex(String numDevices){
        for (int d = 0; d < deviceNums.length; d++){
            if (numDevices.equals(deviceNums[d])) {
                return d;
            }
        }
        return 0;
    }
    private int getClothesIndex(String newClothes){
        for (int c = 0; c < clothesFreqs.length; c++){
            if (newClothes.equals(clothesFreqs[c])) {
                return c;
            }
        }
        return 0;
    }
    private int getRecycleIndex(String recycle){
        for (int r = 0; r < recycleFreqs.length; r++){
            if (recycle.equals(recycleFreqs[r])) {
                return r;
            }
        }
        return 0;
    }
}




