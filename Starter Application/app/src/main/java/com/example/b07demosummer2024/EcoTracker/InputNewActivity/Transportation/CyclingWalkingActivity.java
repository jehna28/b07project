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

public class CyclingWalkingActivity extends AppCompatActivity {

    Button saveButton;
    String selectedDistanceUnit;
    EditText distance;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cycling_walking);

        String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
        int cntActs = getIntent().getIntExtra("ACTIVITY_COUNT", 0);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinner1 = (Spinner) findViewById(R.id.mikm);
        String[] items1 = new String[]{"km", "mi"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        spinner1.setAdapter(adapter2);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        distance = (EditText)findViewById(R.id.disCycled);
        saveButton = (Button)findViewById(R.id.saveButtonCycling);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = distance.getText().toString();
                if (!str.isEmpty()) {
                    try {
                        double distanceVal = Double.parseDouble(str);
                        Log.d("Distance Value", "Parsed value: " + distanceVal);

                        // calculating C02eEmission
                        double C02eEmission = 0;
                        String C02eEmissionString = String.valueOf(C02eEmission);


                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(CyclingWalkingActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                            return;
                        }

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
                                activityData.put("Activity", "Walked or cycled for " + distanceVal + " " + selectedDistanceUnit);
                                activityData.put("CO2e Emission", C02eEmissionString);

                                databaseReference.child("Activity " + newCntActs)
                                        .setValue(activityData)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(CyclingWalkingActivity.this, "New activity saved!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(CyclingWalkingActivity.this, CalendarEcoTracker.class);
                                            intent.putExtra("SELECTED_DATE", stringDateSelected);
                                            intent.putExtra("ACTIVITY_COUNT", newCntActs);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(CyclingWalkingActivity.this, "Failed to save activity", Toast.LENGTH_SHORT).show();
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(CyclingWalkingActivity.this, "Error fetching count", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (NumberFormatException e) {
                        distance.setError("Invalid number format");
                    }
                } else {
                    distance.setError("Must enter a value");
                }
            }
        });

    }
}