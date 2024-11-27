package com.example.b07demosummer2024.quiz_and_results.results;

import android.content.Context;

import com.example.b07demosummer2024.quiz_and_results.results.calculators.ConsumptionCalculator;
import com.example.b07demosummer2024.quiz_and_results.results.calculators.FoodCalculator;
import com.example.b07demosummer2024.quiz_and_results.results.calculators.HousingCalculator;
import com.example.b07demosummer2024.quiz_and_results.results.calculators.TransportationCalculator;
import com.example.b07demosummer2024.quiz_and_results.results.object_classes.DataPackage;
import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class ResultsManager {
    ArrayList<QuestionData> data;
    TransportationCalculator transportationCalculator;
    FoodCalculator foodCalculator;
    ConsumptionCalculator consumptionCalculator;
    HousingCalculator housingCalculator;
    public ResultsManager(DataPackage data, Context context) {
        this.data = data.getQuestionData();
        //Log.d("resultsManager", "initialized array of data");
        transportationCalculator = new TransportationCalculator(this.data, context);
        //Log.d("resultsManager", "initialized transportationCalculator");
        foodCalculator = new FoodCalculator(this.data, context);
        //Log.d("resultsManager", "initialized foodCalculator");
        consumptionCalculator = new ConsumptionCalculator(this.data, context);
        //Log.d("resultsManager", "initialized consumptionCalculator");
        housingCalculator = new HousingCalculator(this.data, context);
        //Log.d("resultsManager", "initialized housingCalculator");
    }

    public Dictionary<String, Double> getFootprints(){
        Dictionary<String, Double> footprints = new Hashtable<>();
        double transportation = convertKgtoTons(transportationCalculator.getFootprint());
        double food = convertKgtoTons(foodCalculator.getFootprint());
        double consumption = convertKgtoTons(consumptionCalculator.getFootprint());
        double housing = convertKgtoTons(housingCalculator.getFootprint());
        double total = roundTo6(transportation + food + consumption + housing);
        footprints.put("TOTAL", total);
        footprints.put("TRANSPORTATION", transportation);
        footprints.put("FOOD", food);
        footprints.put("CONSUMPTION", consumption);
        footprints.put("HOUSING", housing);
        return footprints;
    }

    public void saveToDB(Dictionary<String, Double> footprints){
        // complete later
    }

    private double convertKgtoTons(double kg) {
        // rounds to 6 decimal places
        return roundTo6(kg*0.001);
    }

    private double roundTo6(double x) {
        return Math.round(x * 1e6) / 1e6;
    }

}
