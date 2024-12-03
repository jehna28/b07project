package com.example.b07demosummer2024.Habits.ManageHabits;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07demosummer2024.Habits.HabitsMain.HabitsMainPage;
import com.example.b07demosummer2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ManageHabitsActivity extends AppCompatActivity {

    private List<Pair<String, Integer>> habitPlusLoggedDays;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_habits);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Array and U.I elements
        habitPlusLoggedDays = new ArrayList<>();
        rv = findViewById(R.id.recyclerViewManageHabitsWow);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        grabUserHabits();

        // We can't call our sort method after grabbing user habits in here as Firebase calls are
        // async, so java will think the code is done executing when it's not. Thus we have to call
        // the sort method inside the listener of the Firebase call so that our arrays are properly
        // loaded at run time and non-empty

        goBack();
    }

    private void goBack() {
        // Use OnBackPressedDispatcher for handling back button presses
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), HabitsMainPage.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void grabUserHabits() {

        // We grab the users habits and display them alongside the number of days they logged the
        // activity

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();

        if (user != null) {

            DatabaseReference ref = mDataBase.getReference("Users").child(user.getUid()).child("habits");

            // Iterate through all the habits found in the users habit node
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    habitPlusLoggedDays.clear();
                    for (DataSnapshot habitSnapShot : snapshot.getChildren()) {
                        habitPlusLoggedDays.add(new Pair<>(habitSnapShot.getKey(), Integer.parseInt(habitSnapShot.child("loggedDays").getValue(String.class))));
                    }

                    // After async data retrieval is done, we can sort the arrays
                    sortUserHabits();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Manage Habits", "Failed to retrieve User Habits: " + error.getMessage());
                }
            });
        }
    }

    private void sortUserHabits() {
        // Sort with built in sorting algorithm with custom compare method to make items in the row
        // get added in descending order
        habitPlusLoggedDays.sort(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                return o2.second - o1.second;
            }
        });

        // Update recycler with our arrays using our custom manager adapter
        ManageAdapter manageAdapter =  new ManageAdapter(habitPlusLoggedDays, ManageHabitsActivity.this);
        rv.setAdapter(manageAdapter);
    }
}