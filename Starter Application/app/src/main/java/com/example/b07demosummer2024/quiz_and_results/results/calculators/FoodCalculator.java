package com.example.b07demosummer2024.quiz_and_results.results.calculators;

import android.content.Context;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;

import java.util.ArrayList;

public class FoodCalculator extends EmissionCalculator {
    public FoodCalculator(ArrayList<QuestionData> data, Context context){
        // initialize calculator with the food strategy .txt file
        // make sure the file name matches the strategy .txt file!
        super(data, context, "Food", "emission_quiz_assets/emission_strategies/food_strategy.txt");
    }
}
