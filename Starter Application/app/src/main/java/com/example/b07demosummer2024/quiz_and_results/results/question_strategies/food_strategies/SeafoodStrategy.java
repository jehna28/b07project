package com.example.b07demosummer2024.quiz_and_results.results.question_strategies.food_strategies;

import android.content.Context;
import android.util.Log;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;

import java.util.ArrayList;

public class SeafoodStrategy implements QuestionStrategy {
    // Daily= 800 kg; Frequently= 500 kg; Occasionally=150 kg; Never=0
    final double DAILY = 800;
    final double FREQUENTLY = 500;
    final double OCCASIONALLY = 150;

    @Override
    public double getEmissions(ArrayList<QuestionData> data, String response) {
        double total = 0;
        if (response.equals("Daily")) total = DAILY;
        else if (response.equals("Frequently (3-5 times/week)")) total = FREQUENTLY;
        else if (response.equals("Occasionally (1-2 times/week)")) total = OCCASIONALLY;
        ////Log.d("seafoodStrategy", "returning " + String.valueOf(total));
        return total;
    }
}
