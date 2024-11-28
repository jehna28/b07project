package com.example.b07demosummer2024.quiz_and_results.results.question_strategies.food_strategies;

import android.util.Log;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;

import java.util.ArrayList;

public class BeefStrategy implements QuestionStrategy {
    // Daily= 2500 kg; Frequently= 1900 kg; Occasionally=1300 kg; Never=0
    final double DAILY = 2500;
    final double FREQUENTLY = 1900;
    final double OCCASIONALLY = 1300;

    @Override
    public double getEmissions(ArrayList<QuestionData> data, String response) {
        double total = 0;
        if (response.equals("Daily")) total = DAILY;
        else if (response.equals("Frequently (3-5 times/week)")) total = FREQUENTLY;
        else if (response.equals("Occasionally (1-2 times/week)")) total = OCCASIONALLY;
        //Log.d("beefStrategy", "returning " + String.valueOf(total));
        return total;
    }
}
