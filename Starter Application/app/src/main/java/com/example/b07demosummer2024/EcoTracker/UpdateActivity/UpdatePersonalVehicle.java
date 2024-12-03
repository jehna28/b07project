package com.example.b07demosummer2024.EcoTracker.UpdateActivity;

import android.app.Activity;
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
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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


public class UpdatePersonalVehicle extends AppCompatActivity {

    Button updateButton;
    EditText distanceDrivenVal;
    String selectedVehicleType, selectedDistanceUnit;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    String activityKey; // Unique key of the activity being updated
    String stringDateSelected; // Selected date for this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("UpdatePersonalVehicle", "onCreate: Activity created");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_personal_vehicle);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinner1 = findViewById(R.id.updateVType);
        String[] items1 = new String[]{"Gasoline", "Diesel", "Hybrid", "Electric", "I don't know"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        spinner1.setAdapter(adapter1);

        Spinner spinner2 = findViewById(R.id.updateMiKm);
        String[] items2 = new String[]{"km", "mi"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        spinner2.setAdapter(adapter2);

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

        distanceDrivenVal = findViewById(R.id.updateDisDriven);
        updateButton = findViewById(R.id.updateButtonPV);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            stringDateSelected = bundle.getString("SelectedDate");
            activityKey = bundle.getString("Key");
        }

        updateButton.setOnClickListener(v -> {
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
                            .child(stringDateSelected)
                            .child("Transportation");

                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("Activity Type", "Personal Vehicle");
                    updatedData.put("Activity", "Drove a " + selectedVehicleType.toLowerCase() + " vehicle type for " + distanceVal + " " + selectedDistanceUnit);
                    updatedData.put("CO2e Emission", CO2eEmissionString);

                    databaseReference.child(activityKey)
                            .updateChildren(updatedData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(UpdatePersonalVehicle.this, "Activity updated successfully!", Toast.LENGTH_SHORT).show();
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
