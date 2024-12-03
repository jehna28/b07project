package com.example.b07demosummer2024.EcoTracker.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07demosummer2024.EcoTracker.InputNewActivity.SelectCategoryActivity;
import com.example.b07demosummer2024.HabitsMainPage;
import com.example.b07demosummer2024.HomeScreenActivity;
import com.example.b07demosummer2024.LogHabits;
import com.example.b07demosummer2024.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The following class represents the Calendar & Activity Management Options implementation of the EcoTracker.
 * Users are able to log activities for past, present and future dates; in addition, they are able to edit and
 * delete activities.
 * @author Alena Carvalho
 * @since 2024-11-21
 */
public class CalendarEcoTracker extends AppCompatActivity {

    FloatingActionButton addActivity;
    Button buttonHabits, buttonLogHabits;

    //Fields that represent the components of the Calendar of the EcoTracker
    public CalendarView calendarView;
    private EditText editEventTitle, editEventUpdate;

    private Button buttonSaveAct;
    private Button buttonUpdateEvent;
    private Button buttonDeleteEvent;

    private TextView textEvents;

    public int cntActs;

    public String stringDateSelected;

    private String selectedEventText;
    private String selectedEventId;

    private FirebaseUser user;

    private DatabaseReference databaseReference;

    // Setting up initializations for recycler view
    RecyclerView recyclerView;
    List<DataClass> dataList;
    ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.calendar_ecotracker);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        buttonHabits = findViewById(R.id.buttonMainHabits);
        buttonHabits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HabitsMainPage.class);
                startActivity(intent);
            }
        });

        buttonLogHabits = findViewById(R.id.buttonLogHabits);
        buttonLogHabits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(CalendarEcoTracker.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(user.getUid())
                        .child("habits");
                ref.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            Intent intent = new Intent(getApplicationContext(), LogHabits.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "You need to add habits first!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CalendarEcoTracker.this, CalendarEcoTracker.class);
                            startActivity(intent);
                        }
                    } else {
                        // Handle errors fetching data
                        Log.e("FirebaseError", "Error fetching data", task.getException());
                        Toast.makeText(getApplicationContext(),
                                "Error checking habits. Please try again later.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //Initializations of the fields
        calendarView = findViewById(R.id.calendarView);

        // Setting up floating action button (fab)
        addActivity = findViewById(R.id.addActivityFab);

        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CalendarEcoTracker.this, "Please select a date first", Toast.LENGTH_SHORT).show();
            }
        });

        // When User clicks on a specific date on the calendar, the date is saved to the field 'stringDateSelected'
        // and the function calendarClicked() is called.
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            stringDateSelected = year + (Integer.toString(month + 1)) + dayOfMonth;
            calendarClicked();

            // Setting up the fab button and passing stringDateSelected to SelectCategoryActivity
            addActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (stringDateSelected != null) {
                        Log.d("Selected Date", "Selected date from outside: " + stringDateSelected);
                    } else {
                        Log.d("Selected Date", "No date selected yet.");
                    }
                    Intent intent = new Intent(CalendarEcoTracker.this, SelectCategoryActivity.class);
                    intent.putExtra("SELECTED_DATE", stringDateSelected);
                    startActivity(intent);
                }
            });
        });

        // Initializations of the fields that will store, upload, and delete activities in the database.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("EcoTrackerCalendar");

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the data when the activity is resumed
        if (stringDateSelected != null) {
            // If a date is already selected, refresh the activities for that date
            calendarClicked();
        }
    }

    // The following method is called when the user clicks on a specific date of the calendar.
    // In addition, the following method will display the current/new activities of the specific day
    // The following method is called when the user clicks on a specific date of the calendar.
// It retrieves and displays the list of activities for the selected date.
    private void calendarClicked() {
        recyclerView = findViewById(R.id.recyclerViewActivities);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(CalendarEcoTracker.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        dataList = new ArrayList<>();
        MyAdapter adapter = new MyAdapter(CalendarEcoTracker.this, dataList);
        recyclerView.setAdapter(adapter);

        databaseReference.child(stringDateSelected).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                if (!snapshot.exists()) {
                    Toast.makeText(CalendarEcoTracker.this, "No activities found for this date.", Toast.LENGTH_SHORT).show();
                    dataList.clear();
                    adapter.notifyDataSetChanged();
                    return;
                }
                // Loop through all categories (e.g., "Transportation")
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String title = categorySnapshot.getKey(); // E.g., "Transportation"

                    // Loop through activities under the category
                    for (DataSnapshot activitySnapshot : categorySnapshot.getChildren()) {
                        String description = activitySnapshot.child("Activity").getValue(String.class); // E.g., "Drove a gasoline vehicle..."
                        String co2eEmission = activitySnapshot.child("CO2e Emission").getValue(String.class); // E.g., "2.4"
                        String activityType = activitySnapshot.child("Activity Type").getValue(String.class); // E.g., "Personal Vehiclew"
                        String activityKey = activitySnapshot.getKey(); // Get the unique key for the activity

                        if (description != null && co2eEmission != null) {
                            // Trimming emission value
                            double emissionValue = Double.parseDouble(co2eEmission);
                            String formattedEmission = String.format("%.2f", emissionValue);

                            // Add to the list
                            DataClass dataClass = new DataClass(title, description, formattedEmission + " kg CO2e", stringDateSelected, activityType);
                            Log.d("detailActivity IntentData", "SelectedDate: " + stringDateSelected + ", Category: " + title);
                            dataClass.setKey(activityKey); // Set the key for deletion
                            dataList.add(dataClass);
                        }
                    }
                }

                adapter.notifyDataSetChanged(); // Notify adapter to update RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CalendarEcoTracker.this, "Failed to load activities.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // The following function is called when the User logs their new activity and will store the new activity
    // in the database, under the section "EcoTrackerCalendar", of the User.
    public void buttonSaveEvent(View view) {
        cntActs++;
        databaseReference.child(stringDateSelected).child("ActivityName" + cntActs).setValue(editEventTitle.getText().toString());
    }

}
