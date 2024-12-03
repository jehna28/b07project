package com.example.b07demosummer2024.EcoTracker.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.b07demosummer2024.EcoTracker.UpdateActivity.UpdateClothes;
import com.example.b07demosummer2024.EcoTracker.UpdateActivity.UpdateCyclingWalking;
import com.example.b07demosummer2024.EcoTracker.UpdateActivity.UpdateElectronics;
import com.example.b07demosummer2024.EcoTracker.UpdateActivity.UpdateEnergyBills;
import com.example.b07demosummer2024.EcoTracker.UpdateActivity.UpdateFlight;
import com.example.b07demosummer2024.EcoTracker.UpdateActivity.UpdateFood;
import com.example.b07demosummer2024.EcoTracker.UpdateActivity.UpdateOtherPurchases;
import com.example.b07demosummer2024.EcoTracker.UpdateActivity.UpdatePersonalVehicle;
import com.example.b07demosummer2024.EcoTracker.UpdateActivity.UpdatePublicTransportation;
import com.example.b07demosummer2024.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, detailLang;
    FloatingActionButton deleteButton, editButton;
    String key = "";
    String selectedDate, category, activityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        detailDesc = findViewById(R.id.detailDesc);
        detailTitle = findViewById(R.id.detailTitle);
        detailLang = findViewById(R.id.detailLang);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            selectedDate = bundle.getString("SelectedDate");
            category = bundle.getString("Title");
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailLang.setText(bundle.getString("C02eEmission"));
            key = bundle.getString("Key");
            activityType = bundle.getString("Activity Type");

            Log.d("IntentData", "SelectedDate: " + selectedDate);
            Log.d("IntentData", "Category: " + category);
            Log.d("IntentData", "Key: " + key);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key != null && !key.isEmpty()) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("EcoTrackerCalendar")
                            .child(selectedDate)
                            .child(category);

                    // Use the unique key to remove the activity
                    reference.child(key).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(DetailActivity.this, "Activity deleted successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity
                        } else {
                            Toast.makeText(DetailActivity.this, "Failed to delete activity.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(DetailActivity.this, "Invalid activity key.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                Class<?> updateActivity;

                // Check for activity type and assign the corresponding class
                if (activityType.equals("Personal Vehicle")) {
                    updateActivity = UpdatePersonalVehicle.class;
                } else if (activityType.equals("Public Transportation")) {
                    updateActivity = UpdatePublicTransportation.class;
                } else if (activityType.equals("Cycling or Walking")) {
                    updateActivity = UpdateCyclingWalking.class;
                } else if (activityType.equals("Flight")) {
                    updateActivity = UpdateFlight.class;
                } else if (activityType.equals("Clothes")) {
                    updateActivity = UpdateClothes.class;
                } else if (activityType.equals("Electronics")) {
                    updateActivity = UpdateElectronics.class;
                } else if (activityType.equals("Other Purchases")) {
                    updateActivity = UpdateOtherPurchases.class;
                } else if (activityType.equals("Food")) {
                    updateActivity = UpdateFood.class;
                } else if (activityType.equals("Energy")) {
                    updateActivity = UpdateEnergyBills.class;
                } else {
                    // Handle the case where activityType does not match any expected value
                    throw new IllegalArgumentException("Invalid activity type: " + activityType);
                }
                intent = new Intent(DetailActivity.this, updateActivity)
                        .putExtra("Title", detailTitle.getText().toString())
                        .putExtra("Description", detailDesc.getText().toString())
                        .putExtra("C02eEmission", detailLang.getText().toString())
                        .putExtra("SelectedDate", selectedDate)
                        .putExtra("Key", key)
                        .putExtra("Activity Type", activityType);
                startActivity(intent);

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}