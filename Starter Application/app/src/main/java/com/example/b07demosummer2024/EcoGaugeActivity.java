package com.example.b07demosummer2024;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//Imports for pie chart

import com.example.b07demosummer2024.HomePage.HomeScreenActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.components.Description;

//Imports for line chart
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EcoGaugeActivity extends AppCompatActivity {

    // private PieChart pieChart;
    private DatabaseReference databaseReference;

    private DatabaseReference databaseCountryInfo;

    private String country;
    private Float countryAverage;

    // ArrayLists to store CO2 emissions data
    private List<Float> transportationEmissions;
    private List<Float> foodEmissions;
    private List<Float> energyEmissions;
    private List<Float> shoppingEmissions;

    private List<Float> dailyEmissions;
    private List<String> dates;

    private TextView countrySentence;

    private TextView kgSentence;

    final int[] CHART_COLOURS = {
            // set the colours for the pie chart to match the app's theme
            Color.rgb(65, 164, 165),
            Color.rgb(109, 178, 180),
            Color.rgb(161, 197, 201),
            Color.rgb(0, 152, 153),
            Color.rgb(127, 204, 204)
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_gauge);

        //Initialize needed user database stuff
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("EcoTrackerCalendar");
        databaseCountryInfo = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("primaryData");

        // Initialize lists
        transportationEmissions = new ArrayList<>();
        foodEmissions = new ArrayList<>();
        energyEmissions = new ArrayList<>();
        shoppingEmissions = new ArrayList<>();
        dailyEmissions = new ArrayList<>();
        dates= new ArrayList<>();

        // Fetch data and update charts dynamically
        fetchDataFromFirebase();

        //Fetch country data and display country average comparison
        fetchCountryDataFromFirebase();

        //Determine current date
        String currentDate = getCurrentDate();
        //Log.d(TAG, " Date: " + currentDate);


        // Initialize dropdown menu
        Spinner dropdownMenu = findViewById(R.id.dropdown_menu);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.time_options,
                R.layout.ecogauge_spinner
        );
        adapter.setDropDownViewResource(R.layout.ecogauge_spinner);
        dropdownMenu.setAdapter(adapter);

        // Set item selected listener for the dropdown menu
        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                switch (selectedOption) {
                    case "Today":
                        double today = today_emissions(currentDate);
                        double roundedValue1 = Math.round(today * 100.0) / 100.0;
                        kgSentence = findViewById(R.id.total_kg_sentence);
                        kgSentence.setText("You have emitted "+ roundedValue1+" kg of CO2 today!");
                        Toast.makeText(EcoGaugeActivity.this, "Today selected", Toast.LENGTH_SHORT).show();
                        break;
                    case "This Month":
                        double month = month_emissions(currentDate);
                        double roundedValue2 = Math.round(month * 100.0) / 100.0;
                        kgSentence = findViewById(R.id.total_kg_sentence);
                        kgSentence.setText("You have emitted "+ roundedValue2+" kg of CO2 this month!");
                        Toast.makeText(EcoGaugeActivity.this, "This Month selected", Toast.LENGTH_SHORT).show();
                        break;
                    case "This Year":
                        double year = year_emissions(currentDate);
                        double roundedValue3 = Math.round(year * 100.0) / 100.0;
                        kgSentence = findViewById(R.id.total_kg_sentence);
                        kgSentence.setText("You have emitted "+ roundedValue3+" kg of CO2 this year!");
                        Toast.makeText(EcoGaugeActivity.this, "This Year selected", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Use OnBackPressedDispatcher for handling back button presses
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);


    }

    private double today_emissions(String currentDate){
        for(int i=0; i< dates.size(); i++){
            if(currentDate.equals(dates.get(i))){
                return (double) dailyEmissions.get(i);
            }
        }
        return 0.0;
    }

    private double month_emissions(String currentDate){
        String month = currentDate.substring(4,6);
        String year = currentDate.substring(0,4);
        double total = 0.0;
        for(int i=0; i< dates.size(); i++){
            String tempMonth = dates.get(i).substring(4,6);
            String tempYear = dates.get(i).substring(0,4);
            if(tempMonth.equals(month) && tempYear.equals(year)){
                total+= (double) dailyEmissions.get(i);
            }
        }
        return total;
    }

    private double year_emissions(String currentDate){
        String year = currentDate.substring(0,4);
        double total = 0.0;
        for(int i=0; i< dates.size();i++){
            String tempYear = dates.get(i).substring(0,4);
            if(tempYear.equals(year)){
                total+= (double) dailyEmissions.get(i);
            }
        }
        return total;
    }

    //This tells user how ahead or behind they are from their country
    private void fetchCountryDataFromFirebase(){
        databaseCountryInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    // Retrieve the country
                    country = snapshot.child("country").getValue(String.class);

                    // Retrieve the country average
                    countryAverage = snapshot.child("countryAverage").getValue(Float.class);

                    //Convert from tons to kgs and converts from yearly to day
                    countryAverage = (float) (countryAverage*(907.185)/365);

                    float userDayAverage = calculateTotal(dailyEmissions)/dailyEmissions.size();

                    float difference = (float)(Math.abs(((countryAverage-userDayAverage)/countryAverage)/100.0));

                    userDayAverage = Math.round(userDayAverage * 100.0f) / 100.0f;
                    difference = Math.round(difference * 100.0f) / 100.0f;
                    String outputs = "";

                    if(countryAverage > userDayAverage){
                        outputs = "You are " + difference +"% better than the average person in " + country+"!";
                    }else if(countryAverage==userDayAverage){
                        outputs = "You are an average person in "+ country;
                    }else{
                        outputs = "You are " + difference + "% worse than the average person in " + country+"!";
                    }

                    countrySentence = findViewById(R.id.countryCompare);
                    countrySentence.setText(outputs);

                }else{
                    //This lets user know if there is a data issue, for debugging purposes
                    Toast.makeText(EcoGaugeActivity.this, "Error Fetching Country Data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //This lets user know if there is a data issue, for debugging purposes
                Toast.makeText(EcoGaugeActivity.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Make sure any extra data is not there
                    transportationEmissions.clear();
                    foodEmissions.clear();
                    energyEmissions.clear();
                    shoppingEmissions.clear();

                    // Process data, go through each date
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String dateKey = dateSnapshot.getKey();
                        dates.add(dateKey);
                        //Log.d(TAG, "Processing date: " + dates.get((dates.size()-1)));

                        //Update emission lists for each day
                        processEmissionsForDate(dateSnapshot);
                    }



                    // Update charts after fetching data
                    updateCharts();
                } else {
                    //This lets user know if there is a data issue, for debugging purposes
                    Toast.makeText(EcoGaugeActivity.this, "No data available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //This lets user know if there is a data issue, for debugging purposes
                Toast.makeText(EcoGaugeActivity.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //This method updates the pie chart and line graph
    private void updateCharts() {
        // Calculate totals
        float transTotal = calculateTotal(transportationEmissions);
        float foodTotal = calculateTotal(foodEmissions);
        float energyTotal = calculateTotal(energyEmissions);
        float shoppingTotal = calculateTotal(shoppingEmissions);

        // Update PieChart
        createPieChart(transTotal, energyTotal, foodTotal, shoppingTotal);

        // Update LineChart
        createLineChart();
    }

    //This is a helper function used to total up emissions
    private float calculateTotal(List<Float> emissions) {
        float total = 0;
        for (Float value : emissions) {
            total += value;
        }
        return total;
    }

    //This sums all the activites in a selected day, in their category lists
    private void processEmissionsForDate(DataSnapshot dateSnapshot) {
        float transportationTotal = 0;
        float foodTotal = 0;
        float energyTotal = 0;
        float shoppingTotal = 0;

        if (dateSnapshot.hasChild("Transportation")) {
            transportationTotal = sumCategoryEmissions(dateSnapshot.child("Transportation"));
        }
        if (dateSnapshot.hasChild("Food")) {
            foodTotal = sumCategoryEmissions(dateSnapshot.child("Food"));
        }
        if (dateSnapshot.hasChild("Energy")) {
            energyTotal = sumCategoryEmissions(dateSnapshot.child("Energy"));
        }
        if (dateSnapshot.hasChild("Shopping")) {
            shoppingTotal = sumCategoryEmissions(dateSnapshot.child("Shopping"));
        }

        transportationEmissions.add(transportationTotal);
        foodEmissions.add(foodTotal);
        energyEmissions.add(energyTotal);
        shoppingEmissions.add(shoppingTotal);
    }

    //This sums all the activity emissions for a given category on a given day
    private float sumCategoryEmissions(DataSnapshot categorySnapshot) {
        float totalEmissions = 0f;

        //Goes through each activity
        for (DataSnapshot activitySnapshot : categorySnapshot.getChildren()) {
            //Log.d(TAG, "values: " + activitySnapshot.child("CO2e Emission").getValue());
            Float emissionValue;

            //Sanity checking database values
            if(activitySnapshot.child("CO2e Emission").getValue() instanceof String){
                String tempEmissionValue = (String) activitySnapshot.child("CO2e Emission").getValue();
                emissionValue = Float.parseFloat(tempEmissionValue);
            }else{
                Long tempEmissionValue = (Long) activitySnapshot.child("CO2e Emission").getValue();
                emissionValue = (float) tempEmissionValue;
            }

            //String emissionValue = (String) activitySnapshot.child("CO2e Emission").getValue();
            //Log.d(TAG, "values: " + emissionValue)
            if (emissionValue != null) {
                //Log.d(TAG, "values: " + emissionValue);
                totalEmissions += emissionValue;
            }
        }
        return totalEmissions;
    }

    //Creates Pie chart with four sections
    private void createPieChart(float transportation, float energy, float food, float shopping) {
        PieChart pieChart = findViewById(R.id.pie_chart);

        // Check if all values are zero
        if (transportation == 0 && energy == 0 && food == 0 && shopping == 0) {
            Toast.makeText(this, "No data to display in the Pie Chart.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(transportation, "Transportation"));
        entries.add(new PieEntry(energy, "Energy"));
        entries.add(new PieEntry(food, "Food"));
        entries.add(new PieEntry(shopping, "Shopping"));

        PieDataSet dataSet = new PieDataSet(entries, "Emissions Breakdown");
        dataSet.setColors(CHART_COLOURS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);


        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.animateY(1000);


        Description description = new Description();
        description.setText("Emissions by Category");
        pieChart.setDescription(description);

        pieChart.setUsePercentValues(true);
        Typeface typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD);
        pieChart.setEntryLabelTypeface(typeface);
        dataSet.setValueTypeface(typeface);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.DKGRAY);

        pieChart.invalidate(); // Refresh chart
    }

    //Creates line chart to show emission progress over time, day by day
    //Each data point is total emissions for that day
    private void createLineChart() {
        LineChart lineChart = findViewById(R.id.line_chart);

        //Get a total emissions list for each day
        for(int i=0; i<transportationEmissions.size();i++){
            float temp_total = transportationEmissions.get(i) + foodEmissions.get(i)+energyEmissions.get(i)+shoppingEmissions.get(i);
            dailyEmissions.add(temp_total);
        }

        ArrayList<Entry> entries = new ArrayList<>();
        //entries.add(new Entry(0, 1));
        //entries.add(new Entry(1, 2));
        //entries.add(new Entry(2, 4));
        //entries.add(new Entry(3, 8));
        //entries.add(new Entry(4, 16));

        for(int i=0; i<dailyEmissions.size();i++){
            entries.add(new Entry(i, dailyEmissions.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Your CO2 Trends");
        dataSet.setColor(Color.DKGRAY);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        Description description = new Description();
        description.setText("Emissions Trend");
        lineChart.setDescription(description);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter());

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        lineChart.invalidate(); // Refresh chart
    }

    //Fetches date and formats it to follow firebase date keys
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String tempdate = dateFormat.format(calendar.getTime());
        String newDate = "";
        for(int i=0; i<tempdate.length();i++){
            if(tempdate.charAt(i)=='-' || i==8 && tempdate.charAt(i)=='0'){
                //skip this char
            }else {
                newDate+=tempdate.charAt(i);
            }
        }
        return newDate;
    }


}