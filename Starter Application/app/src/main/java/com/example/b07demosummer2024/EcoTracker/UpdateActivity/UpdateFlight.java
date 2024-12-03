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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.b07demosummer2024.EcoTracker.Calculations.TFCalculation;
import com.example.b07demosummer2024.EcoTracker.Calendar.CalendarEcoTracker;
import com.example.b07demosummer2024.EcoTracker.InputNewActivity.Transportation.FlightActivity;
import com.example.b07demosummer2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UpdateFlight extends AppCompatActivity {

    Button saveButton;
    Spinner slHaul;
    EditText numFlights;
    String selectedFlightType;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    String activityKey; // Unique key of the activity being updated
    String stringDateSelected; // Selected date for this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_flight);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Spinner spinner1 = (Spinner) findViewById(R.id.updateSLHaul);
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

        numFlights = (EditText)findViewById(R.id.updateNumFlight);
        saveButton = (Button)findViewById(R.id.updateButtonFlight);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            stringDateSelected = bundle.getString("SelectedDate");
            activityKey = bundle.getString("Key");
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = numFlights.getText().toString();
                if (!str.isEmpty()) {
                    try {
                        double numFlightsVal = Double.parseDouble(str);
                        // calculating C02eEmission
                        TFCalculation calculate = new TFCalculation();
                        double C02eEmission = calculate.getCO2eEmission(selectedFlightType, numFlightsVal);
                        String C02eEmissionString = String.valueOf(C02eEmission);

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(UpdateFlight.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(user.getUid())
                                .child("EcoTrackerCalendar")
                                .child(stringDateSelected)
                                .child("Transportation");

                        Map<String, Object> updatedData = new HashMap<>();
                        updatedData.put("Activity Type", "Flight");
                        updatedData.put("Activity", "Traveled " +  numFlights.getText().toString() + selectedFlightType +" flights");
                        updatedData.put("CO2e Emission", C02eEmissionString);

                        databaseReference.child(activityKey)
                                .updateChildren(updatedData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(UpdateFlight.this, "Activity updated successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(UpdateFlight.this, CalendarEcoTracker.class);
                                    intent.putExtra("SELECTED_DATE", stringDateSelected);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(UpdateFlight.this, "Failed to update activity", Toast.LENGTH_SHORT).show();
                                });

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