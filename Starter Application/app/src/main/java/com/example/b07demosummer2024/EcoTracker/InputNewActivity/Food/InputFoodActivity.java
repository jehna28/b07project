package com.example.b07demosummer2024.EcoTracker.InputNewActivity.Food;

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

import com.example.b07demosummer2024.EcoTracker.Calculations.FCalculation;
import com.example.b07demosummer2024.EcoTracker.Calendar.CalendarEcoTracker;
import com.example.b07demosummer2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class InputFoodActivity extends AppCompatActivity {

    Button saveButton;
    String selectedFoodType;
    EditText numServings;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_input_food);

        String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
        int cntActs = getIntent().getIntExtra("ACTIVITY_COUNT", 0); // Default value is 0
        Log.v("stringDateSelected", stringDateSelected);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinner1 = (Spinner) findViewById(R.id.mType);
        String[] items1 = new String[]{"Beef", "Pork", "Chicken", "Fish", "Plant-based"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // Retrieve the selected item as a String
                selectedFoodType = (String) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (optional)
            }
        });

        numServings = (EditText)findViewById(R.id.numServings);
        saveButton = (Button)findViewById(R.id.saveButtonFood);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = numServings.getText().toString();
                if (!str.isEmpty()) {
                    try {
                        double numServingsVal = Double.parseDouble(str);
                        Log.d("Servings Value", "Parsed value: " + numServingsVal);

                        // calculating C02eEmission
                        FCalculation calculate = new FCalculation();
                        double C02eEmission = calculate.getCO2eEmission(selectedFoodType, numServingsVal);
                        String C02eEmissionString = String.valueOf(C02eEmission);
                        Log.v("Food C02eEmission", C02eEmissionString);

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(InputFoodActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(user.getUid())
                                .child("EcoTrackerCalendar")
                                .child(stringDateSelected)
                                .child("Food");

                        Map<String, Object> activityData = new HashMap<>();
                        activityData.put("Activity Type", "Food");
                        activityData.put("Activity", "Ate " + (int)numServingsVal + " servings of " + selectedFoodType + " food type");
                        activityData.put("CO2e Emission", C02eEmissionString);

                        String newActivityKey = databaseReference.push().getKey();
                        if (newActivityKey != null) {
                            databaseReference.child(newActivityKey)
                                    .setValue(activityData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(InputFoodActivity.this, "New activity saved!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(InputFoodActivity.this, CalendarEcoTracker.class);
                                        intent.putExtra("SELECTED_DATE", stringDateSelected);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(InputFoodActivity.this, "Failed to save activity", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(InputFoodActivity.this, "Failed to generate unique key for activity", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NumberFormatException e) {
                        numServings.setError("Invalid number format");
                    }
                } else {
                    numServings.setError("Must enter a value");
                }
            }
        });

    }
}