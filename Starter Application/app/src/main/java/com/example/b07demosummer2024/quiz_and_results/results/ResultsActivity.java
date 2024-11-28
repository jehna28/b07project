package com.example.b07demosummer2024.quiz_and_results.results;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.b07demosummer2024.HomeScreenActivity;
import com.example.b07demosummer2024.R;
import com.example.b07demosummer2024.quiz_and_results.results.object_classes.DataPackage;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {

    ResultsManager resultsManager;
    Dictionary<String, Double> footprints;
    TextView totalText;
    TextView comparisonText;
    PieChart chart;
    Button nextButton;

    final int[] CHART_COLOURS = {
            Color.rgb(65, 164, 165),
            Color.rgb(109, 178, 180),
            Color.rgb(161, 197, 201),
            Color.rgb(0, 152, 153),
            Color.rgb(127, 204, 204)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Log.d("debug", "successfully entered results");
        nextButton = findViewById(R.id.nextButton);
        DataPackage data = new DataPackage(
                getIntent().getStringArrayExtra("QUESTIONS"),
                getIntent().getStringArrayExtra("RESPONSES"),
                getIntent().getStringArrayExtra("CATEGORIES"));
        ////Log.d("resultsActivity", "initialized datapackage");
        //data.displayQuestionData();
        resultsManager = new ResultsManager(data, this);
        footprints = resultsManager.getFootprints();
        setFootprintTexts(footprints);
        setPieChart(footprints);
        resultsManager.saveToDB(footprints);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultsActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setFootprintTexts(Dictionary<String, Double> footprints) {
        totalText = findViewById(R.id.totalText);
        comparisonText = findViewById(R.id.comparisonText);

        double totalFootprint = footprints.get("TOTAL");
        totalText.setText(String.valueOf(totalFootprint));

        String country = resultsManager.getCountry();
        double countryAverage = resultsManager.getCountryAverage();

        double difference = Math.abs(((totalFootprint - countryAverage) / countryAverage) * 100);
        String differencePercent = String.format(Locale.CANADA, "%.2f", difference);
        String belowAbove = "below";
        if (totalFootprint >= countryAverage) belowAbove = "above";

        String comparison = "Your carbon footprint is " +
                differencePercent + "% " +
                belowAbove + " the national average for " + country;

        comparisonText.setText(comparison);
    }

    private void setPieChart(Dictionary<String, Double> footprints) {

        float food = (float) (double) footprints.get("FOOD");
        float transportation = (float) (double) footprints.get("TRANSPORTATION");
        float housing = (float) (double) footprints.get("HOUSING");
        float consumption = (float) (double) footprints.get("CONSUMPTION");

        chart = (PieChart) findViewById(R.id.chart);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(food, "Food"));
        entries.add(new PieEntry(transportation, "Transportation"));
        entries.add(new PieEntry((float) 4.73, "Housing")); // change to housing later
        entries.add(new PieEntry(consumption, "Consumption"));
        PieDataSet dataSet = new PieDataSet(entries, "Breakdown of Annual Footprint");
        dataSet.setColors(CHART_COLOURS);
        PieData data = new PieData(dataSet);

        Typeface typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD);
        chart.setEntryLabelColor(0xFFFFFFFF);
        dataSet.setValueTextColor(0xFFFFFFFF);
        chart.setEntryLabelTextSize(14f);
        dataSet.setValueTextSize(14f);
        chart.setEntryLabelTypeface(typeface);
        dataSet.setValueTypeface(typeface);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setData(data);
        chart.animateY(1000);
        chart.invalidate();
    }
}