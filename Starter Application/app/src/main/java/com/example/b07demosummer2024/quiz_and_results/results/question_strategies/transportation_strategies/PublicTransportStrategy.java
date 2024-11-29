package com.example.b07demosummer2024.quiz_and_results.results.question_strategies.transportation_strategies;

import android.content.Context;
import android.util.Log;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;

import java.util.ArrayList;

public class PublicTransportStrategy implements QuestionStrategy {

    final String[] frequencies = new String[]{ //
            "Never",
            "Occasionally (1-2 times/week)",
            "Frequently (3-4 times/week)",
            "Always (5+ times/week)"};
    final String[] durations = new String[]{ //
            "Under 1 hour",
            "1-3 hours",
            "3-5 hours",
            "5-10 hours",
            "More than 10 hours"};
    final double[][] transportEmissions = { //[frequency][duration]
            //Under 1 hour, 1-3 hours, 3-5 hours, 5-10 hours, More than 10 hours
            {0, 0, 0, 0, 0},
            {246, 819, 1638, 3071, 4095}, // occasionally
            {573, 1911, 3822, 7166, 9555}, // frequently
            {573, 1911, 3822, 7166, 9555}}; // always
    @Override
    public double getEmissions(ArrayList<QuestionData> data, String response) {

        String frequency = getFrequency(data);
        //Log.d("publicTransportStrategy", "frequency: " + frequency + ", response: " + response);
        int frequencyIndex = 0;
        int durationIndex = 0;
        for (int f = 0; f < frequencies.length; f++){
            if (frequency.equals(frequencies[f])) {
                frequencyIndex = f;
            }
        }
        for (int d = 0; d < durations.length; d++) {
            if (response.equals(durations[d])) {
                durationIndex = d;
            }
        }
        //Log.d("publicTransportStrategy", "returning " + transportEmissions[frequencyIndex][durationIndex]);
        return transportEmissions[frequencyIndex][durationIndex];
    }

    private String getFrequency(ArrayList<QuestionData> data){
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getQuestion().equals("How often do you use public transportation (bus, train, subway)?")){
                return data.get(i).getResponse();
            }
        }
        return "";
    }
}
