package com.example.b07demosummer2024.quiz_and_results.results.question_strategies.food_strategies;

import android.util.Log;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;

import java.util.ArrayList;

public class LeftoversStrategy implements QuestionStrategy {
    //Never=0; Rarely=23.4 Kg, Occasionally=70.2 Kg, Frequently=140.4 Kg
    final double RARELY = 23.4;
    final double OCCASIONALLY = 70.2;
    final double FREQUENTLY = 140.4;

    @Override
    public double getEmissions(ArrayList<QuestionData> data, String response) {
        double total = 0;
        if (response.equals("Rarely")) total = RARELY;
        else if (response.equals("Occasionally")) total = OCCASIONALLY;
        else if (response.equals("Frequently")) total = FREQUENTLY;
        Log.d("leftoversStrategy", "returning " + String.valueOf(total));
        return total;
    }
}
