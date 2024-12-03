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

import com.example.b07demosummer2024.EcoTracker.Calculations.SOPCalculation;
import com.example.b07demosummer2024.EcoTracker.Calendar.CalendarEcoTracker;
import com.example.b07demosummer2024.EcoTracker.InputNewActivity.Shopping.OtherPurchasesActivity;
import com.example.b07demosummer2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UpdateOtherPurchases extends AppCompatActivity {

    Button updateButton;
    String selectedPurchaseType;
    EditText numItems;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    String activityKey; // Unique key of the activity being updated
    String stringDateSelected; // Selected date for this activity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_other_purchases);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinner1 = (Spinner) findViewById(R.id.updateOPType);
        String[] items1 = new String[]{"Appliance", "Furniture"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // Retrieve the selected item as a String
                selectedPurchaseType = (String) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (optional)
            }
        });

        numItems = (EditText)findViewById(R.id.updateNumPurchases);
        updateButton = (Button)findViewById(R.id.updateButtonPurchases);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            stringDateSelected = bundle.getString("SelectedDate");
            activityKey = bundle.getString("Key");
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = numItems.getText().toString();
                if (!str.isEmpty()) {
                    try {
                        double numItemsVal = Double.parseDouble(str);
                        Log.d("Distance Value", "Parsed value: " + numItemsVal);

                        // calculating C02eEmission
                        SOPCalculation calculate = new SOPCalculation();
                        double C02eEmission = calculate.getCO2eEmission(selectedPurchaseType, numItemsVal);
                        String C02eEmissionString = String.valueOf(C02eEmission);
                        Log.v("Purchases C02eEmission", C02eEmissionString);

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(UpdateOtherPurchases.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(user.getUid())
                                .child("EcoTrackerCalendar")
                                .child(stringDateSelected)
                                .child("Shopping");

                        Map<String, Object> updatedData = new HashMap<>();
                        updatedData.put("Activity Type", "Other Purchases");
                        updatedData.put("Activity", "Purchased" + numItemsVal + " " + selectedPurchaseType + "(s)");
                        updatedData.put("CO2e Emission", C02eEmissionString);

                        databaseReference.child(activityKey)
                                .updateChildren(updatedData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(UpdateOtherPurchases.this, "Activity updated successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(UpdateOtherPurchases.this, CalendarEcoTracker.class);
                                    intent.putExtra("SELECTED_DATE", stringDateSelected);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(UpdateOtherPurchases.this, "Failed to update activity", Toast.LENGTH_SHORT).show();
                                });

                    } catch (NumberFormatException e) {
                        numItems.setError("Invalid number format");
                    }
                } else {
                    numItems.setError("Must enter a value");
                }
            }
        });
    }
}