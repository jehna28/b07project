package com.example.b07demosummer2024.quiz_and_results.results.question_strategies.consumption_strategies;

import android.util.Log;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;

import java.util.ArrayList;

public class DeviceStrategy implements QuestionStrategy {
    final double ONE = 300;
    final double TWO = 600;
    final double THREE = 900;
    final double FOURPLUS = 1200;

    @Override
    public double getEmissions(ArrayList<QuestionData> data, String response) {
        double total = 0;
        if (response.equals("1")) total = ONE;
        else if (response.equals("2")) total = TWO;
        else if (response.equals("3")) total = THREE;
        else if (response.equals("4 or more")) total = FOURPLUS;
        //Log.d("deviceStrategy", "returning "+String.valueOf(total));
        return total;
    }
}
