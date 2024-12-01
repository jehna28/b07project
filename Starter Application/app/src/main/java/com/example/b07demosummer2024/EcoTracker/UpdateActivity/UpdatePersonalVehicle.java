package com.example.b07demosummer2024.EcoTracker.UpdateActivity;

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

import com.example.b07demosummer2024.EcoTracker.Calculations.TPVCalculation;
import com.example.b07demosummer2024.EcoTracker.Calendar.CalendarEcoTracker;
import com.example.b07demosummer2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class UpdatePersonalVehicle extends AppCompatActivity{

    Button saveButton;
    EditText distanceDrivenVal;
    String selectedVehicleType, selectedDistanceUnit;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("UpdatePersonalVehicle", "onCreate: Activity created");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_personal_vehicle);

        String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
        String selectedEventId = getIntent().getStringExtra("SELECTED_EVENT_ID");
        String vehicleType = getIntent().getStringExtra("VEHICLE_TYPE");
        String distanceUnit = getIntent().getStringExtra("DISTANCE_UNIT");
        String distanceValue = getIntent().getStringExtra("DISTANCE_VALUE");

        distanceDrivenVal = findViewById(R.id.disDriven);
        saveButton = findViewById(R.id.saveButtonUPV);

        Spinner spinner1 = findViewById(R.id.vType);
        String[] items1 = new String[]{"Gasoline", "Diesel", "Hybrid", "Electric"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        spinner1.setAdapter(adapter1);

        Spinner spinner2 = findViewById(R.id.mikm);
        String[] items2 = new String[]{"km", "mi"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        spinner2.setAdapter(adapter2);

        // Pre-fill fields with data if present
        if (vehicleType != null) {
            int vehicleIndex = adapter1.getPosition(vehicleType);
            spinner1.setSelection(vehicleIndex);
        }
        if (distanceUnit != null) {
            int unitIndex = adapter2.getPosition(distanceUnit);
            spinner2.setSelection(unitIndex);
        }
        if (distanceValue != null) {
            distanceDrivenVal.setText(distanceValue);
        }

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedVehicleType = (String) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedDistanceUnit = (String) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Save updated data
        saveButton.setOnClickListener(v -> {
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
                        Toast.makeText(UpdatePersonalVehicle.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(user.getUid())
                            .child("EcoTrackerCalendar")
                            .child(stringDateSelected);

                    Map<String, Object> activityData = new HashMap<>();
                    activityData.put("Activity", "Drove a " + selectedVehicleType +  "vehicle type for " + distanceVal + " " + selectedDistanceUnit);
                    activityData.put("CO2e Emission", CO2eEmissionString);

                    // Update existing event
                    databaseReference.child(selectedEventId)
                            .setValue(activityData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(UpdatePersonalVehicle.this, "Activity updated!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdatePersonalVehicle.this, CalendarEcoTracker.class);
                                intent.putExtra("SELECTED_DATE", stringDateSelected);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(UpdatePersonalVehicle.this, "Failed to update activity", Toast.LENGTH_SHORT).show();
                            });
                } catch (NumberFormatException e) {
                    distanceDrivenVal.setError("Invalid number format");
                }
            } else {
                distanceDrivenVal.setError("Must enter a value");
            }
        });
    }

}
