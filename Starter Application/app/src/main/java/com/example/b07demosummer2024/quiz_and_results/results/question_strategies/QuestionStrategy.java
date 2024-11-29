package com.example.b07demosummer2024.quiz_and_results.results.question_strategies;

import android.content.Context;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;

import java.util.ArrayList;

public interface QuestionStrategy {
    public double getEmissions(ArrayList<QuestionData> data, String response);
}
