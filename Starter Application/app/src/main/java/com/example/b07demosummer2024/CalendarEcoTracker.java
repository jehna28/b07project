package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
public class CalendarEcoTracker extends AppCompatActivity{

    //Fields that represent the components of the Calendar of the EcoTracker
    public CalendarView calendarView;
    private EditText editEventTitle, editEventUpdate;

    private Button buttonSaveAct;
    private Button buttonUpdateEvent;
    private Button buttonDeleteEvent;

    private TextView textEvents;

    private int cntActs;

    private String stringDateSelected;

    private String selectedEventText;
    private String selectedEventId;

    private FirebaseUser user;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.calendar_ecotracker);

        //Initializations of the fields
        calendarView = findViewById(R.id.calendarView);
        editEventTitle = findViewById(R.id.editEventText);
        editEventUpdate = findViewById(R.id.editEventUpdate);
        buttonUpdateEvent = findViewById(R.id.buttonUpdateEvent);
        buttonDeleteEvent = findViewById(R.id.buttonDeleteEvent);
        cntActs = 0;

        buttonSaveAct = findViewById(R.id.buttonSaveEvent);

        textEvents = findViewById(R.id.textEvents);

        // When User clicks on a specific date on the calendar, the date is saved to the field 'stringDateSelected'
        // and the function calendarClicked() is called.
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            stringDateSelected = year + (Integer.toString(month+1)) + dayOfMonth;
            calendarClicked();
        });

        // Initializations of the fields that will store, upload, and delete activities in the database.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("EcoTrackerCalendar");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Makes the buttons for editing, updating and deleting events invisible (initially)
        editEventUpdate.setVisibility(View.GONE);
        buttonUpdateEvent.setVisibility(View.GONE);
        buttonDeleteEvent.setVisibility(View.GONE);

        // These methods will delete/update the corresponding events when clicked upon.
        buttonUpdateEvent.setOnClickListener(this::buttonUpdateEvent);
        buttonDeleteEvent.setOnClickListener(this::buttonDeleteEvent);
    }

    // The following method is called when the user clicks on a specific date of the calendar.
    // In addition, the following method will display the current/new activities of the specific day
    private void calendarClicked() {
        databaseReference.child(stringDateSelected).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> events = new ArrayList<>();
                List<String> eventIds = new ArrayList<>();

                // Iterates through the activities and displays the titles of the Activities
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    String actID = eventSnapshot.getKey();
                    String act = eventSnapshot.getValue(String.class);
                    if (act != null) {
                        events.add(act);
                        eventIds.add(actID);
                        cntActs++;
                    }
                }
                displayActs(events, eventIds);
                editEventTitle.setText("Enter Activity");
            }

            //Use this to delete events from firebase

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


    // The following helper function is used to display the current activities of the day.
    private void displayActs(List<String> events, List<String> eventIds) {
        if (events.isEmpty()) {
            textEvents.setText("No Activities for This Date");
        } else {
            StringBuilder eventsText = new StringBuilder();
            for (String event : events) {
                eventsText.append("- ").append(event).append("\n");
            }
            textEvents.setText(eventsText.toString().trim());

            // Handles event click for updating
            textEvents.setOnClickListener(v -> {

                if (!events.isEmpty()) {
                    showEventSelectionDialog(events, eventIds);
                }
            });
        }
    }


    // The following function shows a dialog to select an activity to edit or delete
    private void showEventSelectionDialog(List<String> events, List<String> eventIds) {
        String[] eventArray = events.toArray(new String[0]);

        // Users should not be able to edit/delete an activity if there are no activities for desired date.
        if(textEvents.getText() == "No Activities for This Date") return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select an Activity to Edit")
                .setItems(eventArray, (dialog, which) -> {
                    // Get the selected event and its ID
                    selectedEventText = events.get(which);
                    selectedEventId = eventIds.get(which);

                    // Populate the EditText for updating
                    editEventUpdate.setText(selectedEventText);
                    editEventUpdate.setVisibility(View.VISIBLE);
                    buttonUpdateEvent.setVisibility(View.VISIBLE);
                    buttonDeleteEvent.setVisibility(View.VISIBLE);
                })
                .setNegativeButton("Cancel", null);
        builder.show();
    }

    // The following function updates the selected activity
    public void buttonUpdateEvent(View view) {
        String updatedEvent = editEventUpdate.getText().toString().trim();

        if (!updatedEvent.isEmpty() && selectedEventId != null) {
            databaseReference.child(stringDateSelected).child(selectedEventId).setValue(updatedEvent)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CalendarEcoTracker.this, "Activity updated!", Toast.LENGTH_SHORT).show();
                            editEventUpdate.setVisibility(View.GONE);
                            buttonUpdateEvent.setVisibility(View.GONE);
                            buttonDeleteEvent.setVisibility(View.GONE);
                            calendarClicked();
                        } else {
                            Toast.makeText(CalendarEcoTracker.this, "Error updating activity", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter a new activity title", Toast.LENGTH_SHORT).show();
        }
    }

    // The following function deletes the selected activity
    public void buttonDeleteEvent(View view) {
        if (selectedEventId != null) {
            databaseReference.child(stringDateSelected).child(selectedEventId).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CalendarEcoTracker.this, "Activity deleted!", Toast.LENGTH_SHORT).show();
                            editEventUpdate.setVisibility(View.GONE);
                            buttonUpdateEvent.setVisibility(View.GONE);
                            buttonDeleteEvent.setVisibility(View.GONE);
                            calendarClicked(); // Refresh events display
                        } else {
                            Toast.makeText(CalendarEcoTracker.this, "Error deleting Activity", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No activity selected", Toast.LENGTH_SHORT).show();
        }
    }


}
