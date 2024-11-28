package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.b07demosummer2024.quiz_and_results.quiz.QuizActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CountrySelectionActivity extends AppCompatActivity {

    private Spinner countrySpinner;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_choice);

        countrySpinner = findViewById(R.id.country_spinner);
        continueButton = findViewById(R.id.continue_button);

        ArrayList<String> countries = loadCountriesFromCSV();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country = countrySpinner.getSelectedItem().toString();
                if (country.equals("Find country")) {
                    incompleteToast();
                }
                else {
                    double average = getCountryAverage(countries, country);
                    saveCountryToDB(country, average);
                    Intent intent = new Intent(CountrySelectionActivity.this, QuizActivity.class);
                    //finish(); // Optional: close CountrySelectionActivity so the user cannot go back to it
                    startActivity(intent);
                }
            }
        });
    }

    private double getCountryAverage(ArrayList<String> countries, String country){
        ArrayList<Double> averageList = loadAveragesFromCSV();
        int index = countries.indexOf(country);
        if (index != -1) {
            return ( (double) averageList.get(index));
        }
        return 0;
    }
    private void saveCountryToDB(String country, double average){
        Log.d("save", "saving "+country+ ", " + String.valueOf(average));

        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                String userID = user.getUid();
                DatabaseReference ref = db.getReference("Users").child(userID);
                ref.child("country").setValue(country);
                ref.child("countryAverage").setValue(average);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void incompleteToast(){
        Toast.makeText(this, "Please select a country", Toast.LENGTH_SHORT).show();
    }
    private ArrayList<String> loadCountriesFromCSV() {
        ArrayList<String> countryList = new ArrayList<>();
        try {
            InputStream is = getAssets().open("Global_Averages.csv"); // Ensure this matches your file name
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                if (!row[0].equals("Country")) { // Skip the header row
                    countryList.add(row[0].trim()); // Add the country name
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("debug", "error");
        }
        return countryList;
    }

    private ArrayList<Double> loadAveragesFromCSV(){
        ArrayList<Double> averageList = new ArrayList<>();
        try {
            InputStream is = getAssets().open("Global_Averages.csv"); // Ensure this matches your file name
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                if (!row[1].equals("Emissions (per capita)")) { // Skip the header row
                    averageList.add(Double.valueOf(row[1])); // Add the country's average
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return averageList;
    }
}