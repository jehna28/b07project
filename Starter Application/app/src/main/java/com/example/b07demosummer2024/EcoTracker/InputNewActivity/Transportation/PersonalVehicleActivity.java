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

import com.example.b07demosummer2024.EcoTracker.Calculations.TPVCalculation;
import com.example.b07demosummer2024.EcoTracker.Calendar.CalendarEcoTracker;
import com.example.b07demosummer2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class PersonalVehicleActivity extends AppCompatActivity {

    Button saveButton;
    EditText distanceDrivenVal;
    String selectedVehicleType, selectedDistanceUnit;
    private FirebaseUser user;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PersonalVehicleActivity", "onCreate: Activity created");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_vehicle);

        String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
        int cntActs = getIntent().getIntExtra("ACTIVITY_COUNT", 0); // Default value is 0
        Log.v("stringDateSelected", stringDateSelected);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // setting dropdown menu to select type of vehicle
        Spinner spinner1 = (Spinner) findViewById(R.id.vType);
        String[] items1 = new String[]{"Gasoline", "Diesel", "Hybrid", "Electric", "I don't know"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        spinner1.setAdapter(adapter1);

        // setting dropdown menu to select distance unit
        Spinner spinner2 = (Spinner) findViewById(R.id.mikm);
        String[] items2 = new String[]{"km", "mi"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        spinner2.setAdapter(adapter2);

        // getting selected input from first dropdown menu
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // Retrieve the selected item as a String
                selectedVehicleType = (String) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (optional)
            }
        });

        // getting selected input from second dropdown menu
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // Retrieve the selected item as a String
                selectedDistanceUnit = (String) parent.getItemAtPosition(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (optional)
            }
        });

        distanceDrivenVal = (EditText)findViewById(R.id.disDriven);

        saveButton = (Button) findViewById(R.id.saveButton1);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = distanceDrivenVal.getText().toString();
                if (!str.isEmpty()) {
                    try {
                        double distanceVal = Double.parseDouble(str);
                        TPVCalculation calculate = new TPVCalculation();
                        double CO2eEmission = calculate.getCO2eEmission(selectedVehicleType, selectedDistanceUnit, distanceVal);
                        String CO2eEmissionString = String.valueOf(CO2eEmission);

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(PersonalVehicleActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get reference to the user's transportation data for the selected date
                        databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(user.getUid())
                                .child("EcoTrackerCalendar")
                                .child(stringDateSelected)
                                .child("Transportation");

                        // Create the activity data map
                        Map<String, Object> activityData = new HashMap<>();
                        activityData.put("Activity Type", "Personal Vehicle");
                        activityData.put("Activity", "Traveled " + distanceVal + " " + selectedDistanceUnit + " via " + selectedVehicleType.toLowerCase() + " vehicle type");
                        activityData.put("CO2e Emission", CO2eEmissionString);

                        // Use push() to generate a unique key for the new activity
                        String newActivityKey = databaseReference.push().getKey();
                        if (newActivityKey != null) {
                            databaseReference.child(newActivityKey)
                                    .setValue(activityData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(PersonalVehicleActivity.this, "New activity saved!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PersonalVehicleActivity.this, CalendarEcoTracker.class);
                                        intent.putExtra("SELECTED_DATE", stringDateSelected);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(PersonalVehicleActivity.this, "Failed to save activity", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(PersonalVehicleActivity.this, "Failed to generate unique key for activity", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        distanceDrivenVal.setError("Invalid number format");
                    }
                } else {
                    distanceDrivenVal.setError("Must enter a value");
                }
            }
        });
    }
}