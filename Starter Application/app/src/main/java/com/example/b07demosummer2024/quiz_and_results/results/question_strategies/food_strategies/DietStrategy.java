package com.example.b07demosummer2024.quiz_and_results.results.question_strategies.food_strategies;

import android.util.Log;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;

import java.util.ArrayList;

public class DietStrategy implements QuestionStrategy {
    final double VEGETARIAN = 1000;
    final double VEGAN = 500;
    final double PESCATARIAN = 1500;
    @Override
    public double getEmissions(ArrayList<QuestionData> data, String response) {
        double total = 0;
        if (response.equals("Vegetarian")) total = VEGETARIAN;
        else if (response.equals("Vegan")) total = VEGAN;
        else if (response.equals("Pescatarian (fish/seafood)")) total = PESCATARIAN;
        //Log.d("dietStrategy", "returning " + String.valueOf(total));
        return total;
    }
}
