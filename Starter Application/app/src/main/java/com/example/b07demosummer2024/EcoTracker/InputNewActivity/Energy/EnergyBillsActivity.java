package com.example.b07demosummer2024.EcoTracker.InputNewActivity.Energy;

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

import com.example.b07demosummer2024.EcoTracker.Calculations.ECalculation;
import com.example.b07demosummer2024.EcoTracker.Calendar.CalendarEcoTracker;
import com.example.b07demosummer2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EnergyBillsActivity extends AppCompatActivity {

    Button saveButton;
    String selectedEnergyType;
    EditText billValue;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_energy_bills);

        String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
        Log.v("stringDateSelected", stringDateSelected);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinner1 = (Spinner) findViewById(R.id.energyType);
        String[] items1 = new String[]{"Electricity", "Gas", "Water"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // Retrieve the selected item as a String
                selectedEnergyType = (String) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (optional)
            }
        });

        billValue = (EditText)findViewById(R.id.billValue);
        saveButton = (Button)findViewById(R.id.saveButtonEnergy);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = billValue.getText().toString();
                if (!str.isEmpty()) {
                    try {
                        double billValueDouble = Double.parseDouble(str);
                        Log.d("Distance Value", "Parsed value: " + billValueDouble);

                        // calculating C02eEmission
                        ECalculation calculate = new ECalculation();
                        double C02eEmission = calculate.getCO2eEmission(selectedEnergyType, billValueDouble);
                        String C02eEmissionString = String.valueOf(C02eEmission);
                        Log.v("PV C02eEmission", C02eEmissionString);

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(EnergyBillsActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(user.getUid())
                                .child("EcoTrackerCalendar")
                                .child(stringDateSelected)
                                .child("Energy");

                        Map<String, Object> activityData = new HashMap<>();
                        activityData.put("Activity Type", "Energy");
                        activityData.put("Activity", "Paid " + selectedEnergyType.toLowerCase() + " bill for " + billValueDouble + " dollars");
                        activityData.put("CO2e Emission", C02eEmissionString);

                        String newActivityKey = databaseReference.push().getKey();
                        if (newActivityKey != null) {
                            databaseReference.child(newActivityKey)
                                    .setValue(activityData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(EnergyBillsActivity.this, "New activity saved!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(EnergyBillsActivity.this, CalendarEcoTracker.class);
                                        intent.putExtra("SELECTED_DATE", stringDateSelected);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EnergyBillsActivity.this, "Failed to save activity", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(EnergyBillsActivity.this, "Failed to generate unique key for activity", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NumberFormatException e) {
                        billValue.setError("Invalid number format");
                    }
                } else {
                    billValue.setError("Must enter a value");
                }
            }
        });

    }
}