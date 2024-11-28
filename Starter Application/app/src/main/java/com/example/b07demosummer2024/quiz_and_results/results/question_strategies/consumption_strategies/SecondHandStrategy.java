package com.example.b07demosummer2024.quiz_and_results.results.question_strategies.consumption_strategies;

import android.util.Log;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;

import java.util.ArrayList;

public class SecondHandStrategy implements QuestionStrategy {
    //Monthly: 360 kg CO2e/year; Quarterly: 120 kg; Annually: 100 kg ; Rarely: 5 kg
    final double MONTHLY = 360;
    final double QUARTERLY = 120;
    final double ANNUALLY = 100;
    final double RARELY = 5;
    final double REGULARLY = 0.5;
    final double OCCASIONALLY = 0.7;

    @Override
    public double getEmissions(ArrayList<QuestionData> data, String response) {
        double total = 0;
        String newClothes = "";
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getQuestion().equals("How often do you buy new clothes?")){
                newClothes = data.get(i).getResponse();
                break;
            }
        }
        double clothesFactor = getClothesFactor(newClothes);
        double secondHandScale = getScale(response);
        total = clothesFactor*secondHandScale;
        //Log.d("secondHandStrategy", "returning " + String.valueOf(total));
        return total;
    }
    private double getClothesFactor(String newClothes){
        if (newClothes.equals("Monthly")) return MONTHLY;
        else if (newClothes.equals("Quarterly")) return QUARTERLY;
        else if (newClothes.equals("Annually")) return ANNUALLY;
        else if (newClothes.equals("Rarely")) return RARELY;
        else return 0;
    }
    private double getScale(String response){
        if (response.equals("Yes, regularly")) return REGULARLY;
        else if (response.equals("Yes, occasionally")) return OCCASIONALLY;
        else return 1;
    }
}
