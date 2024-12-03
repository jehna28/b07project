package com.example.b07demosummer2024.EcoTracker.InputNewActivity.Transportation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.b07demosummer2024.EcoTracker.Calculations.TFCalculation;
import com.example.b07demosummer2024.EcoTracker.Calendar.CalendarEcoTracker;
import com.example.b07demosummer2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class FlightActivity extends AppCompatActivity {

    Button saveButton;
    Spinner slHaul;
    EditText numFlights;
    String selectedFlightType;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flight);

        String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
        int cntActs = getIntent().getIntExtra("ACTIVITY_COUNT", 0); // Default value is 0

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinner1 = (Spinner) findViewById(R.id.slHaul);
        String[] items1 = new String[]{"Short-Haul (<1,500km)", "Long-Haul (>1,500km)"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        spinner1.setAdapter(adapter2);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // Retrieve the selected item as a String
                selectedFlightType = (String) parent.getItemAtPosition(pos);
                // You can store it in a variable or perform any operation
                slHaul = spinner1; // if you want to keep the spinner reference itself
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (optional)
            }
        });

        numFlights = (EditText)findViewById(R.id.numFlight);
        saveButton = (Button)findViewById(R.id.saveButtonFlight);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = numFlights.getText().toString();
                if (!str.isEmpty()) {
                    try {
                        double numFlightsVal = Double.parseDouble(str);
                        Log.d("Distance Value", "Parsed value: " + numFlightsVal);

                        // calculating C02eEmission
                        TFCalculation calculate = new TFCalculation();
                        double C02eEmission = calculate.getCO2eEmission(selectedFlightType, numFlightsVal);
                        String C02eEmissionString = String.valueOf(C02eEmission);
                        Log.v("Flight C02eEmission", C02eEmissionString);

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(FlightActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // this line is where the error is occurring, why is stringDateSelected null?
                        Log.d("Selected Date 2nd Check", stringDateSelected);  // Log for debugging

                        databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(user.getUid())
                                .child("EcoTrackerCalendar")
                                .child(stringDateSelected)
                                .child("Transportation");

                        Map<String, Object> activityData = new HashMap<>();
                        activityData.put("Activity Type", "Flight");
                        activityData.put("Activity", "Took " +  (int)numFlightsVal + selectedFlightType +" flights");
                        activityData.put("CO2e Emission", C02eEmissionString);

                        String newActivityKey = databaseReference.push().getKey();
                        if (newActivityKey != null) {
                            databaseReference.child(newActivityKey)
                                    .setValue(activityData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(FlightActivity.this, "New activity saved!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(FlightActivity.this, CalendarEcoTracker.class);
                                        intent.putExtra("SELECTED_DATE", stringDateSelected);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(FlightActivity.this, "Failed to save activity", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(FlightActivity.this, "Failed to generate unique key for activity", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NumberFormatException e) {
                        numFlights.setError("Invalid number format");
                    }
                } else {
                    numFlights.setError("Must enter a value");
                }
            }
        });

    }
}