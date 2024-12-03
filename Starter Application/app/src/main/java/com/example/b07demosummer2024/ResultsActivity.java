package com.example.b07demosummer2024;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    final int[] CHART_COLOURS = {
            Color.rgb(65, 164, 165),
            Color.rgb(109, 178, 180),
            Color.rgb(161, 197, 201),
            Color.rgb(0, 152, 153),
            Color.rgb(127, 204, 204)
    };

    PieChart chart;
    Dictionary<String, Double> footprints;

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
        footprints = new Hashtable<>();
        footprints.put("TOTAL", 12.28);
        footprints.put("TRANSPORTATION", 4.13); // these are just random placeholder values
        footprints.put("FOOD", 2.13);           // ignore this code
        footprints.put("CONSUMPTION", 1.09);
        footprints.put("HOUSING", 4.93);
        setPieChart(footprints);
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
        entries.add(new PieEntry(housing, "Housing"));
        entries.add(new PieEntry(consumption, "Consumption"));
        PieDataSet dataSet = new PieDataSet(entries, "Breakdown of Annual Footprint");

        // NO NEED TO TOUCH ANY OF THE CODE ABOVE THIS
        // you will need to look at specific MPAndroidCharts documentation to change colours, size, etc.
        // try https://github.com/PhilJay/MPAndroidChart/blob/master/README.md#documentation

        // ANY CODE RELATING TO THE FRONTEND APPEARANCE CAN BE ADDED BELOW

        dataSet.setColors(CHART_COLOURS);
        PieData data = new PieData(dataSet);


        chart.setData(data);// updates piechart with information ,ignore
        chart.animateY(1000);
        chart.invalidate(); // redraws piechart, ignore
    }
}