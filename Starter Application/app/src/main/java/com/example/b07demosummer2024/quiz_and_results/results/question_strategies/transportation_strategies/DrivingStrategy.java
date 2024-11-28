package com.example.b07demosummer2024.quiz_and_results.results.question_strategies.transportation_strategies;

import android.util.Log;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;

import java.util.ArrayList;

public class DrivingStrategy implements QuestionStrategy {

    final double GASOLINE = 0.24;
    final double DIESEL = 0.27;
    final double HYBRID = 0.16;
    final double ELECTRIC = 0.05;
    @Override
    public double getEmissions(ArrayList<QuestionData> data, String response) {
        String carType = "";
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getQuestion().equals("What type of car do you drive?")){
                carType = data.get(i).getResponse();
                break;
            }
        }
        double carFactor = getCarFactor(carType);
        double distance = getDistance(response);
//        //Log.d("drivingStrategy", "carFactor: " + String.valueOf(carFactor));
//        //Log.d("drivingStrategy", "distance: " + String.valueOf(distance));
        //Log.d("drivingStrategy", "returning " + String.valueOf(carFactor*distance));
        return carFactor*distance;
    }
    private double getCarFactor(String carType) {
        //Log.d("drivingStrategy", "car type: " + carType);
        if (carType.equals("Diesel")) return DIESEL;
        else if (carType.equals("Hybrid")) return HYBRID;
        else if (carType.equals("Electric")) return ELECTRIC;
        else return GASOLINE;
    }
    private double getDistance(String response){
        //Log.d("drivingStrategy", "distance: " + response);
        if (response.equals("Up to 5,000 km (3,000 miles)")) return 5000;
        else if (response.equals("5,000-10,000 km (3,000-6,000 miles)")) return 10000;
        else if (response.equals("10,000-15,000 km (6,000-9,000 miles)")) return 15000;
        else if (response.equals("15,000-20,000 km (9,000-12,000 miles)")) return 20000;
        else if (response.equals("20,000-25,000 km (12,000-15,000 miles)")) return 25000;
        else if (response.equals("More than 25,000 km (15,000 miles)")) return 35000;
        else return 0;
    }
}
