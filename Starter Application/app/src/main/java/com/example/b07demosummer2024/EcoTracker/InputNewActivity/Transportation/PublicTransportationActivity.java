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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.b07demosummer2024.EcoTracker.Calculations.TPTCalculation;
import com.example.b07demosummer2024.EcoTracker.Calendar.CalendarEcoTracker;
import com.example.b07demosummer2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class PublicTransportationActivity extends AppCompatActivity {

    Button saveButton;
    Spinner transportationType;
    EditText numHours;
    String selectedTransportationType;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_public_transportation);

        String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
        int cntActs = getIntent().getIntExtra("ACTIVITY_COUNT", 0); // Default value is 0

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // setting up dropdown menu to select type of public transportation
        Spinner spinner1 = (Spinner) findViewById(R.id.tType);
        String[] items1 = new String[]{"Bus", "Train", "Subway"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        spinner1.setAdapter(adapter1);

        // setting up dropdown menu to
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // Retrieve the selected item as a String
                selectedTransportationType = (String) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (optional)
            }
        });

        numHours = (EditText)findViewById(R.id.timeInHours);
        saveButton = (Button)findViewById(R.id.saveButton2);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = numHours.getText().toString();
                if (!str.isEmpty()) {
                    try {
                        double numHoursDouble = Double.parseDouble(str);
                        Log.d("Hours Value", "Parsed value: " + numHoursDouble);

                        TPTCalculation calculate = new TPTCalculation();
                        double C02eEmission = calculate.getCO2eEmission(numHoursDouble);
                        String C02eEmissionString = String.valueOf(C02eEmission);
                        Log.v("PT C02eEmission", C02eEmissionString);

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(PublicTransportationActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
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

                        // Retrieve the latest cntActs from Firebase
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int activityCount = (int) snapshot.getChildrenCount(); // Get count of children
                                int newCntActs = activityCount + 1;

                                Map<String, Object> activityData = new HashMap<>();
                                activityData.put("Activity", "Traveled " +  numHours.getText().toString() + " hrs via " + selectedTransportationType.toLowerCase());
                                activityData.put("CO2e Emission", C02eEmissionString);

                                databaseReference.child("Activity " + newCntActs)
                                        .setValue(activityData)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(PublicTransportationActivity.this, "New activity saved!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(PublicTransportationActivity.this, CalendarEcoTracker.class);
                                            intent.putExtra("SELECTED_DATE", stringDateSelected);
                                            intent.putExtra("ACTIVITY_COUNT", newCntActs);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(PublicTransportationActivity.this, "Failed to save activity", Toast.LENGTH_SHORT).show();
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(PublicTransportationActivity.this, "Error fetching count", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (NumberFormatException e) {
                        numHours.setError("Invalid number format");
                    }
                } else {
                    numHours.setError("Must enter a value");
                }
            }
        });


    }
}