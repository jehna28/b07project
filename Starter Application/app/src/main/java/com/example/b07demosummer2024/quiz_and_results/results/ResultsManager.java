package com.example.b07demosummer2024.quiz_and_results.results;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Locale;

public class ResultsManager {
    private ArrayList<QuestionData> data;
    private TransportationCalculator transportationCalculator;
    private FoodCalculator foodCalculator;
    private ConsumptionCalculator consumptionCalculator;
    private HousingCalculator housingCalculator;
    private Context context;
    public ResultsManager(DataPackage data, Context context) {
        // initialize fields
        this.data = data.getQuestionData();
        this.context = context;
        transportationCalculator = new TransportationCalculator(this.data, context);
        foodCalculator = new FoodCalculator(this.data, context);
        consumptionCalculator = new ConsumptionCalculator(this.data, context);
        housingCalculator = new HousingCalculator(this.data, context);
    }

    public Dictionary<String, Double> getFootprints(){
        // return a dictionary with each category and the corresponding footprints
        Dictionary<String, Double> footprints = new Hashtable<>();
        // get the partial footprints, convert them to tons, then save the values in the dictionary
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

    public void compareWithAverage(TextView totalText, TextView comparisonText, Dictionary<String, Double> footprints) throws Exception{
        // note: this method can throw an exception which is handled in ResultsActivity
        // display the total footprint
        double totalFootprint = footprints.get("TOTAL");
        totalText.setText(String.valueOf(totalFootprint));
        // try to get the user's saved country and average from database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            String userID = user.getUid();
            DatabaseReference ref = db.getReference("Users").child(userID).child("primaryData");
            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // obtain the user's country and countryAverage
                    DataSnapshot snapshot = task.getResult();
                    String country = snapshot.child("country").getValue(String.class);
                    Double countryAverage = snapshot.child("countryAverage").getValue(Double.class);
                    // if unable to obtain either value, compare with the default instead (Canada)
                    if (country == null || countryAverage == null){
                        compareWithDefault(comparisonText, totalFootprint);
                        return;
                    }
                    // compare with the user's country and countryAverage
                    double difference = Math.abs(((totalFootprint - countryAverage) / countryAverage) * 100);
                    String differencePercent = String.format(Locale.CANADA, "%.2f", difference);
                    String belowAbove = "below";
                    if (totalFootprint >= countryAverage) belowAbove = "above";
                    // update text view to display comparison
                    String comparison = "Your carbon footprint is " +
                            differencePercent + "% " +
                            belowAbove + " the national average for " + country;
                    comparisonText.setText(comparison);
                } else {
                    Log.e("Firebase", "Error getting country information", task.getException());
                }
            });
        }
        else {
            // if, for whatever reason, we cannot obtain the user, we compare with the default (Canada)
            compareWithDefault(comparisonText, totalFootprint);
        }
    }
    private void compareWithDefault(TextView comparisonText, double totalFootprint){
        Toast.makeText(context, "Unable to compare with saved country, comparing to default (Canada) instead...", Toast.LENGTH_LONG).show();
        String country = "Canada";
        double countryAverage = 14.249212;
        double difference = Math.abs(((totalFootprint - countryAverage) / countryAverage) * 100);
        String differencePercent = String.format(Locale.CANADA, "%.2f", difference);
        String belowAbove = "below";
        if (totalFootprint >= countryAverage) belowAbove = "above";
        String comparison = "Your carbon footprint is " +
                differencePercent + "% " +
                belowAbove + " the national average for " + country;
        comparisonText.setText(comparison);
    }
    public void saveToDB(Dictionary<String, Double> footprints){
        // save the user's partial and total footprints in the database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            float total = (float) (double) footprints.get("TOTAL");
            float food = (float) (double) footprints.get("FOOD");
            float transportation = (float) (double) footprints.get("TRANSPORTATION");
            float housing = (float) (double) footprints.get("HOUSING");
            float consumption = (float) (double) footprints.get("CONSUMPTION");

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            String userID = user.getUid();
            // save the partial footprints under parent annualFootprint
            DatabaseReference ref = db.getReference("Users").child(userID).child("primaryData").child("annualFootprint");
            ref.child("total").setValue(total);
            ref.child("food").setValue(food);
            ref.child("transportation").setValue(transportation);
            ref.child("housing").setValue(housing);
            ref.child("consumption").setValue(consumption);
        }
    }
    private double convertKgtoTons(double kg) {
        return roundTo6(kg*0.001);
    }
    private double roundTo6(double x) {
        return Math.round(x * 1e6) / 1e6;
    }

}
