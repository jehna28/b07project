package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.b07demosummer2024.quiz_and_results.quiz.QuizActivity;

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
                Intent intent = new Intent(CountrySelectionActivity.this, QuizActivity.class);
                startActivity(intent);
                //finish(); // Optional: close CountrySelectionActivity so the user cannot go back to it
            }
        });
    }

    private ArrayList<String> loadCountriesFromCSV() {
        ArrayList<String> countryList = new ArrayList<>();
        try {
            Log.d("debug", "getting file");
            InputStream is = getAssets().open("Global_Averages.csv"); // Ensure this matches your file name
            Log.d("debug", "got file");
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
}