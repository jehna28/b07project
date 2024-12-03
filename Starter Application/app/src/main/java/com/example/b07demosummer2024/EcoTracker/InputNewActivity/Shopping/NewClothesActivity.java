package com.example.b07demosummer2024.EcoTracker.InputNewActivity.Shopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.b07demosummer2024.EcoTracker.Calculations.SCCalculation;
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

public class NewClothesActivity extends AppCompatActivity {

    Button saveButton;

    EditText numItems;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_clothes);

        String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
        int cntActs = getIntent().getIntExtra("ACTIVITY_COUNT", 0); // Default value is 0
        Log.v("stringDateSelected", stringDateSelected);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        numItems = (EditText) findViewById(R.id.numItems);
        saveButton = (Button) findViewById(R.id.saveButtonClothes);



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = numItems.getText().toString();
                if (!str.isEmpty()) {
                    try {
                        double numItemsVal = Double.parseDouble(str);
                        Log.d("NumItems Value", "Parsed value: " + numItemsVal);

                        // calculating C02eEmission
                        SCCalculation calculate = new SCCalculation();
                        double C02eEmission = calculate.getCO2eEmission(numItemsVal);
                        String C02eEmissionString = String.valueOf(C02eEmission);
                        Log.v("Clothes C02eEmission", C02eEmissionString);

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(NewClothesActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(user.getUid())
                                .child("EcoTrackerCalendar")
                                .child(stringDateSelected)
                                .child("Shopping");

                        // Retrieve the latest cntActs from Firebase
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int activityCount = (int) snapshot.getChildrenCount(); // Get count of children
                                int newCntActs = activityCount + 1;

                                Map<String, Object> activityData = new HashMap<>();
                                activityData.put("Activity", "Purchased " + numItemsVal + " articles of clothing");
                                activityData.put("CO2e Emission", C02eEmissionString);

                                databaseReference.child("Activity " + newCntActs)
                                        .setValue(activityData)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(NewClothesActivity.this, "New activity saved!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(NewClothesActivity.this, CalendarEcoTracker.class);
                                            intent.putExtra("SELECTED_DATE", stringDateSelected);
                                            intent.putExtra("ACTIVITY_COUNT", newCntActs);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(NewClothesActivity.this, "Failed to save activity", Toast.LENGTH_SHORT).show();
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(NewClothesActivity.this, "Error fetching count", Toast.LENGTH_SHORT).show();
                            }
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