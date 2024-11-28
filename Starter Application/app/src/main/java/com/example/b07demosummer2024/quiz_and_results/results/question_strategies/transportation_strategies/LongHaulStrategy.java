package com.example.b07demosummer2024.quiz_and_results.results.question_strategies.transportation_strategies;

import android.util.Log;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;

import java.util.ArrayList;

public class LongHaulStrategy implements QuestionStrategy {
    @Override
    public double getEmissions(ArrayList<QuestionData> data, String response) {
        double total = 0;
        if (response.equals("1-2 flights")) total = 825;
        else if (response.equals("3-5 flights")) total = 2200;
        else if (response.equals("6-10 flights")) total = 4400;
        else if (response.equals("More than 10 flights")) total = 6600;
        //Log.d("shortHaulStrategy", "returning "+String.valueOf(total));
        return total;
    }
}
