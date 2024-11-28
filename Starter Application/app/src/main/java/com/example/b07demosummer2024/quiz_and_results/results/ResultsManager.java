package com.example.b07demosummer2024.quiz_and_results.results;

import android.content.Context;
import android.util.Log;

import com.example.b07demosummer2024.R;
import com.example.b07demosummer2024.quiz_and_results.results.calculators.ConsumptionCalculator;
import com.example.b07demosummer2024.quiz_and_results.results.calculators.FoodCalculator;
import com.example.b07demosummer2024.quiz_and_results.results.calculators.HousingCalculator;
import com.example.b07demosummer2024.quiz_and_results.results.calculators.TransportationCalculator;
import com.example.b07demosummer2024.quiz_and_results.results.object_classes.DataPackage;
import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        transportationCalculator = new TransportationCalculator(this.data, context);
        foodCalculator = new FoodCalculator(this.data, context);
        consumptionCalculator = new ConsumptionCalculator(this.data, context);
        housingCalculator = new HousingCalculator(this.data, context);
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

    public String getCountry() {
        String[] country = new String[]{"Canada"};
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                String userID = user.getUid();
                DatabaseReference ref = db.getReference("Users").child(userID).child("country");
                ref.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        country[0] = task.getResult().getValue(String.class);
                    } else {
                        Log.e("Firebase", "Error getting country", task.getException());
                    }
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return country[0];
    }

    public double getCountryAverage() {
        double[] average = new double[]{14.249212};
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                String userID = user.getUid();
                DatabaseReference ref = db.getReference("Users").child(userID).child("countryAverage");
                ref.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        average[0] = task.getResult().getValue(Double.class);
                    } else {
                        Log.e("Firebase", "Error getting country average", task.getException());
                    }
                });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return average[0];
    }
    public void saveToDB(Dictionary<String, Double> footprints){
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                float total = (float) (double) footprints.get("TOTAL");
                float food = (float) (double) footprints.get("FOOD");
                float transportation = (float) (double) footprints.get("TRANSPORTATION");
                float housing = (float) (double) footprints.get("HOUSING");
                float consumption = (float) (double) footprints.get("CONSUMPTION");

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                String userID = user.getUid();
                DatabaseReference ref = db.getReference("Users").child(userID).child("annualFootprint");
                ref.child("total").setValue(total);
                ref.child("food").setValue(food);
                ref.child("transportation").setValue(transportation);
                ref.child("housing").setValue(housing);
                ref.child("consumption").setValue(consumption);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double convertKgtoTons(double kg) {
        return roundTo6(kg*0.001);
    }

    private double roundTo6(double x) {
        return Math.round(x * 1e6) / 1e6;
    }

}
