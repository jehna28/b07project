package com.example.b07demosummer2024.quiz_and_results.results.calculators;

import android.content.Context;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;

import java.util.ArrayList;

public class ConsumptionCalculator extends EmissionCalculator{
    public ConsumptionCalculator(ArrayList<QuestionData> data, Context context){
        super(data, context, "Consumption", "emission_quiz_assets/emission_strategies/consumption_strategy.txt");
    }
}
