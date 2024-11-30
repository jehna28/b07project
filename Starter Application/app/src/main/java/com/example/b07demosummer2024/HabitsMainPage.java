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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;

public class HabitsMainPage extends AppCompatActivity {

    private Button habitsByCategoryBtn, habitsBySuggestedBtn, manageHabitsBtn;
    private SearchView habitSearchBar;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDataBase;
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

        recyclerView = findViewById(R.id.habitRecyclerViewHabitsMainPage);
        recyclerSearchView = findViewById(R.id.searchResultsRecyclerViewHabitsMainPage);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerSearchView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Initialize search bar recycler view with empty arrays
        ArrayList<String> empty = new ArrayList<>();

        // Show habits by categories of transportation, energy, food, consumption
        habitsByCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJsonDataByCategory();
            }
        });

        // Navigate to screen to show user's habits if any
        manageHabitsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goManageHabits();
            }
        });

        // Search Bar
        habitSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habitSearchBar.setIconified(false);
            }
        });

        habitSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                parseJSONDataBySearch(query);
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

        habitSearchBar.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    recyclerSearchView.setVisibility(View.GONE);
                }
            }
        });

        goBack();
        parseJsonDataByCategory();
    }

    private void goBack() {
        // Use OnBackPressedDispatcher for handling back button presses
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void goManageHabits() {
        if (user != null) {
            // See if user has any habits, if they do then redirect them to the next page to manage habits
            DatabaseReference ref = mDataBase.getReference("Users").child(user.getUid()).child("habits");

            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        // Redirect them to manage page, still gotta code this
                        return;

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
                habitNames.add(category);
                habitImpact.add("category");

                for (int j = 0; j < curr_array.length(); j++) {
                    JSONObject habit = curr_array.getJSONObject(j);
                    habitNames.add(habit.getString("name"));
                    habitImpact.add(habit.getString("impact"));
                }
            }


        } catch (JSONException e){
            Log.e("JSON parsing Error", "Error parsing JSON data", e);
        }

        // Update recycler
        CustomAdapter customAdapter = new CustomAdapter(habitNames, habitImpact, HabitsMainPage.this);
        recyclerView.setAdapter(customAdapter);
    }

    private void parseJSONDataBySearch(String searchText) {

        try {
            // Initialize arrays
            ArrayList<String> habitNames = new ArrayList<>();
            ArrayList<String> habitImpact = new ArrayList<>();
            HashSet<String> addedHabits = new HashSet<>();

            // Get JSON object from json file
            JSONObject obj = new JSONObject(loadJSONfromAssets());

            // Grab keys
            JSONArray keys = obj.names();

            // Check if user typed anything
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

                // Then put any matches of inputted text
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

            CustomSearchAdapter searchAdapter = new CustomSearchAdapter(habitNames, habitImpact, HabitsMainPage.this);
            recyclerSearchView.setAdapter(searchAdapter);

        } catch (JSONException e) {
            Log.e("JSON parsing Error", "Error parsing JSON data", e);
        }

    }

    private  void parseJSONDataBySuggested() {
        // Code here to parse based on user suggested
    }

    private String loadJSONfromAssets() {
        String json = null;

        try {
            InputStream is = getAssets().open("habits.json");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, StandardCharsets.UTF_8);

        } catch (IOException e) {
            Log.e("JSON parsing Error", "Error fetching data", e);
            return null;
        }

        return json;
    }
}