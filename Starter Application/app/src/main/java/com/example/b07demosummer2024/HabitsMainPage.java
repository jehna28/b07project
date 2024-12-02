package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;

public class HabitsMainPage extends AppCompatActivity {

    private boolean isActivitiesPresent;
    private Button habitsByCategoryBtn, habitsBySuggestedBtn, manageHabitsBtn;
    private SearchView habitSearchBar;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataBase;

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView, recyclerSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_habits_main_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize U.I Elements and Firebase
        habitsByCategoryBtn = findViewById(R.id.showByCategoryBtnHabitsMainPage);
        habitsBySuggestedBtn = findViewById(R.id.showBySuggestedBtnHabitsMainPage);
        habitSearchBar = findViewById(R.id.searchHabitsMainPage);
        manageHabitsBtn = findViewById(R.id.manageHabitsBtnHabitsMainPage);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDataBase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        recyclerView = findViewById(R.id.habitRecyclerViewHabitsMainPage);
        recyclerSearchView = findViewById(R.id.searchResultsRecyclerViewHabitsMainPage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerSearchView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Show habits by categories of transportation, energy, food, consumption
        habitsByCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJsonDataByCategory();
            }
        });

        // Show habits by user unique driven habits via firebase data collection
        habitsBySuggestedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJSONDataBySuggested();
            }
        });

        // Navigate to screen to show user's habits if any
        manageHabitsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goManageHabits();
            }
        });

        // Make it so that user can touch anywhere on the search bar to open it, instead of just
        // the magnifying glass icon
        habitSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habitSearchBar.setIconified(false);
            }
        });

        // When user types something and/or enters typed text, we want to match in real time
        // the habits the correspond to the search text and then update the recycler with the matched
        // habits
        habitSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                parseJSONDataBySearch(query);

                // Bring the associated recycler view for the search bar visible and on top
                // of the other views
                recyclerSearchView.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                parseJSONDataBySearch(newText);
                recyclerSearchView.setVisibility(View.VISIBLE);
                return true;
            }
        });

        // When search bar is closed, we also want to make sure that the associated recycler view is
        // gone as well
        habitSearchBar.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    recyclerSearchView.setVisibility(View.GONE);
                }
            }
        });

        goBack();

        // By Default we're going to show habits by categories
        parseJsonDataByCategory();
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

    private void goManageHabits() {
        if (user != null) {
            // See if user has any habits, if they do then redirect them to the next page to manage habits if there are
            DatabaseReference ref = mDataBase.getReference("Users").child(user.getUid()).child("habits");

            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Intent intent = new Intent(getApplicationContext(), ManageHabitsActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(HabitsMainPage.this, "You have no habits to manage, please add one first!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Log error
                    Log.e("FirebaseError", "Error fetching data", task.getException());
                }
            });
        }
    }

    private void parseJsonDataByCategory() {

        // Initialize arrays
        ArrayList<String> habitNames = new ArrayList<>();
        ArrayList<String> habitImpact = new ArrayList<>();

        try {
            // Get JSON object from json file
            JSONObject obj = new JSONObject(loadJSONfromAssets());

            // Grab keys
            JSONArray keys = obj.names();

            // Parse though each category
            for (int i = 0; i < keys.length(); i++) {
                String category = keys.getString(i);

                // Grab the corresponding array of each category
                JSONArray curr_array = obj.getJSONArray(category);

                // Add the category to the array as we're going to distinguish between habits in the
                // recycler view
                habitNames.add(category);
                habitImpact.add("category");

                // Iterate through array of each category and add them to our arrays
                for (int j = 0; j < curr_array.length(); j++) {
                    JSONObject habit = curr_array.getJSONObject(j);
                    habitNames.add(habit.getString("name"));
                    habitImpact.add(habit.getString("impact"));
                }
            }


        } catch (JSONException e){
            Log.e("JSON parsing Error", "Error parsing JSON data", e);
        }

        // Update recycler with our arrays using our custom adapter
        CustomAdapter customAdapter = new CustomAdapter(habitNames, habitImpact, HabitsMainPage.this);
        recyclerView.setAdapter(customAdapter);
    }

    private void parseJSONDataBySearch(String searchText) {

        try {
            // Initialize arrays
            ArrayList<String> habitNames = new ArrayList<>();
            ArrayList<String> habitImpact = new ArrayList<>();

            // This array is only going to be used in the case of when user has inputtd text, so that
            // the recycler doesn't add two habits that are the same when parsing and updating arrays
            // in real time
            HashSet<String> addedHabits = new HashSet<>();

            // Get JSON object from json file
            JSONObject obj = new JSONObject(loadJSONfromAssets());

            // Grab keys
            JSONArray keys = obj.names();

            // Check if user typed anything, if they typed nothing we want to show them all the
            // habits (order doesn't matter here, so go just from how the json data is formatted
            if (searchText == null || searchText.isEmpty()) {
                for (int i = 0; i < keys.length(); i++) {
                    String category = keys.getString(i);

                    // Grab the corresponding array of each category
                    JSONArray curr_array = obj.getJSONArray(category);
                    for (int j = 0; j < curr_array.length(); j++) {
                        JSONObject habit = curr_array.getJSONObject(j);
                        habitNames.add(habit.getString("name"));
                        habitImpact.add(habit.getString("impact"));
                    }
                }
            } else {

                // In the case user has either started typing or entered word/words, we want to sort
                // our arrays based on the category that matches the searchText (and populate the
                // corresponding habits), and then populate other habits that match the search text
                // but not the category (if any).

                // Every time we find a habit to add, we add it to our hash set so that, later in
                // the loops if we encounter them again we don't double add it (thus we always check
                // if a habit is in the hash set or not already, too see if it's already in the array
                String loweredSearchText = searchText.toLowerCase();

                // See if search text matches up with the category names, if so put them first in recycler view
                for (int i = 0; i < keys.length(); i++) {

                    String category = keys.getString(i);

                    if (category.toLowerCase().contains(loweredSearchText)) {
                        JSONArray curr_array = obj.getJSONArray(category);

                        for(int j = 0; j < curr_array.length(); j++) {
                            JSONObject habit = curr_array.getJSONObject(j);
                            String name = habit.getString("name");
                            String impact = habit.getString("impact");

                            if (!addedHabits.contains(name)) {
                                habitNames.add(name);
                                habitImpact.add(impact);
                                addedHabits.add(name);
                            }
                        }
                    }
                }

                // Then put any matches of inputted text that match the search text but not the
                // category
                for (int i = 0; i < keys.length(); i++) {
                    JSONArray curr_array = obj.getJSONArray(keys.getString(i));
                    for (int j = 0; j < curr_array.length(); j++) {
                        JSONObject habit = curr_array.getJSONObject(j);
                        String name = habit.getString("name");
                        String impact = habit.getString("impact");

                        if (name.toLowerCase().contains(loweredSearchText) && !addedHabits.contains(name)) {
                            habitNames.add(name);
                            habitImpact.add(impact);
                            addedHabits.add(name);
                        }
                    }
                }

            }

            // Update recycler with our arrays using our custom search adapter
            CustomSearchAdapter searchAdapter = new CustomSearchAdapter(habitNames, habitImpact, HabitsMainPage.this);
            recyclerSearchView.setAdapter(searchAdapter);

        } catch (JSONException e) {
            Log.e("JSON parsing Error", "Error parsing JSON data", e);
        }

    }

    // Code here to parse based on user suggested; provides User suggestions
    private void parseJSONDataBySuggested() {
        // Initialize arrays
        ArrayList<String> habitNames = new ArrayList<>();
        ArrayList<String> habitImpact = new ArrayList<>();
        boolean WordAppears[] = {false, false, false, false};
        isActivitiesPresent = true;

        try {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (!snapshot.hasChild("EcoTrackerCalendar")) {
                        isActivitiesPresent = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HabitsMainPage.this, "Failed to display suggested habits", Toast.LENGTH_SHORT).show();
                }
            });

            if (!isActivitiesPresent) {
                Toast.makeText(HabitsMainPage.this, "Please Input Activities in the Calendar", Toast.LENGTH_SHORT).show();
                Toast.makeText(HabitsMainPage.this, "Displaying All Possible Habits", Toast.LENGTH_SHORT).show();
                parseJsonDataByCategory();
                return;
            }
            // Get JSON object from json file
            JSONObject obj = new JSONObject(loadJSONfromAssets());

            databaseReference = databaseReference.child("EcoTrackerCalendar");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Loop through all dates
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String date = dateSnapshot.getKey(); // obtain date

                        // loop through categories (Transportation, Shopping, Food, Energy)
                        for (DataSnapshot categorySnapshot : dateSnapshot.getChildren()) {
                            String category = categorySnapshot.getKey(); // Get category name
                            Log.d("Category", "Category: " + category);

                            // Loop through activities
                            for (DataSnapshot activitySnapshot : categorySnapshot.getChildren()) {
                                String activityKey = activitySnapshot.getKey(); // e.g., "Activity 1"
                                String activity = activitySnapshot.child("Activity").getValue(String.class); // Get activity description
                                String co2Emission = activitySnapshot.child("CO2e Emission").getValue(String.class); // Get CO2e Emission value

                                Log.d("Activity", "Key: " + activityKey + ", Activity: " + activity + ", CO2e Emission: " + co2Emission);

                                // Enlists Suggestions based on the user's activities
                                if (!WordAppears[0] && category.equals("Transportation")) {
                                    WordAppears[0] = true;
                                    try {
                                        JSONArray curr_array = obj.getJSONArray(category);

                                        habitNames.add(category);
                                        habitImpact.add("category");

                                        JSONObject habit = curr_array.getJSONObject(0);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                        habit = curr_array.getJSONObject(1);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                        habit = curr_array.getJSONObject(3);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                    } catch (JSONException e) {
                                        Log.e("JSON parsing Error", "Error parsing JSON data", e);
                                    }

                                }else if (!WordAppears[2] && category.equals("Food")) {
                                    WordAppears[2] = true;

                                    try {
                                        JSONArray curr_array = obj.getJSONArray(category);

                                        habitNames.add(category);
                                        habitImpact.add("category");

                                        JSONObject habit = curr_array.getJSONObject(0);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                        habit = curr_array.getJSONObject(1);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                        habit = curr_array.getJSONObject(2);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                        habit = curr_array.getJSONObject(3);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                        habit = curr_array.getJSONObject(4);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                    } catch (JSONException e) {
                                        Log.e("JSON parsing Error", "Error parsing JSON data", e);
                                    }

                                }else if (!WordAppears[1] && category.equals("Energy") && activity.toLowerCase().contains("electricity")) {
                                    WordAppears[1] = true;

                                    try {
                                        JSONArray curr_array = obj.getJSONArray(category);

                                        habitNames.add(category);
                                        habitImpact.add("category");

                                        JSONObject habit = curr_array.getJSONObject(0);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                        habit = curr_array.getJSONObject(1);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                        habit = curr_array.getJSONObject(2);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                        habit = curr_array.getJSONObject(3);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                    } catch (JSONException e) {
                                        Log.e("JSON parsing Error", "Error parsing JSON data", e);
                                    }
                                } else if (!WordAppears[3] && category.equals("Shopping")) {
                                    WordAppears[3] = true;
                                    try {
                                        JSONArray curr_array = obj.getJSONArray(category);

                                        habitNames.add(category);
                                        habitImpact.add("category");

                                        JSONObject habit = curr_array.getJSONObject(0);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                        habit = curr_array.getJSONObject(1);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                        habit = curr_array.getJSONObject(2);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                        habit = curr_array.getJSONObject(4);
                                        habitNames.add(habit.getString("name"));
                                        habitImpact.add(habit.getString("impact"));

                                    } catch (JSONException e) {
                                        Log.e("JSON parsing Error", "Error parsing JSON data", e);
                                    }
                                }
                            }
                        }
                    }

                    CustomAdapter customAdapter = new CustomAdapter(habitNames, habitImpact, HabitsMainPage.this);
                    recyclerView.setAdapter(customAdapter);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HabitsMainPage.this, "Failed to fetch suggested habits", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e){
            Log.e("JSON parsing Error", "Error parsing JSON data", e);
        }
    }

    private String loadJSONfromAssets() {
        String json = null;

        try {

            // Open the json file from assets
            InputStream is = getAssets().open("habits.json");

            // Figures out the size of the json file in bytes
            int size = is.available();

            // We create an array that's the same size as the file and read the contents into it as
            // bytes (always have to close InputStream after usage)
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // We encode the bytes back to string (JSON Object Format)
            json = new String(buffer, StandardCharsets.UTF_8);

        } catch (IOException e) {
            Log.e("JSON parsing Error", "Error fetching data", e);
            return null;
        }

        return json;
    }
}