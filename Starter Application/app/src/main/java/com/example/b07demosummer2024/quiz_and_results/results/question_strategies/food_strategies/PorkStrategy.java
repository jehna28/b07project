package com.example.b07demosummer2024.quiz_and_results.results.question_strategies.food_strategies;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;

import java.util.ArrayList;

public class PorkStrategy implements QuestionStrategy {
    //Daily= 1450 kg; Frequently= 860 kg; Occasionally=450 kg; Never=0
    final double DAILY = 1450;
    final double FREQUENTLY = 860;
    final double OCCASIONALLY = 450;

    @Override
    public double getEmissions(ArrayList<QuestionData> data, String response) {
        double total = 0;
        if (response.equals("Daily")) total = DAILY;
        else if (response.equals("Frequently (3-5 times/week)")) total = FREQUENTLY;
        else if (response.equals("Occasionally (1-2 times/week)")) total = OCCASIONALLY;
        //Log.d("porkStrategy", "returning " + String.valueOf(total));
        return total;
    }
}
