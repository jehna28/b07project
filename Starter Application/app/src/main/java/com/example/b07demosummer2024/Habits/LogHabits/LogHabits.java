package com.example.b07demosummer2024.Habits.LogHabits;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07demosummer2024.EcoTracker.Calendar.CalendarEcoTracker;
import com.example.b07demosummer2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LogHabits extends AppCompatActivity {

    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_habits);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize U.I elements
        rv = findViewById(R.id.recyclerViewTrackHabitsWow);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        grabUserHabits();

        goBack();

    }

    private void goBack() {
        // Use OnBackPressedDispatcher for handling back button presses
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), CalendarEcoTracker.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void grabUserHabits() {

        // We grab the users habits

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();

        ArrayList<String> habits = new ArrayList<>();

        if (user != null) {

            DatabaseReference ref = mDataBase.getReference("Users").child(user.getUid()).child("habits");

            // Iterate through all the habits found in the users habit node
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot habitSnapShot : snapshot.getChildren()) {
                        Log.d("Log Habits", habitSnapShot.getKey());
                        habits.add(habitSnapShot.getKey());
                    }

                    // update recycler
                    HabitAdapter adapter = new HabitAdapter(habits, LogHabits.this);
                    rv.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Manage Habits", "Failed to retrieve User Habits: " + error.getMessage());
                }
            });
        }
    }
}