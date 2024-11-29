package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText editTxtFirstName, editTxtLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize varibales
        editTxtFirstName = findViewById(R.id.firstNameTxtFieldProfile);
        editTxtLastName = findViewById(R.id.lastNameTxtFieldProfile);
        Button saveChangesBtn = findViewById(R.id.confirmBtnProfile);

        // On click listener for save changes button to automatically update db and app interface with changed first name and/or last name
        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Gets strings to capture text in text fields
                String firstName, lastName;

                // Initialize Strings
                firstName = String.valueOf(editTxtFirstName.getText());
                lastName = String.valueOf((editTxtLastName.getText()));

                saveChanges(firstName, lastName);
            }
        });

        goBack();
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

    private void saveChanges(String firstName, String lastName) {

        // Check if both fields are empty
        if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)) {
            Toast.makeText(ProfileActivity.this, "Enter First Name and/or Last Name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get a working instance of FireBase Authentication to grab current users userid
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Open instance of real-time db
        FirebaseDatabase mDataBase = FirebaseDatabase.getInstance();

        // Gets newly registered user as current user
        FirebaseUser user = mAuth.getCurrentUser();

        if (user!=null) {
            if (!(TextUtils.isEmpty(firstName))) {

                //Update firstName so all characters are lowercase except the initial character, have to make them final so we can add it to the db later
                final String formattedFirstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();

                // Open up branch of current user in db
                DatabaseReference users = mDataBase.getReference("Users").child(user.getUid());

                // Add first name to current user node in
                users.child("name").child("firstName").setValue(formattedFirstName);
            }

            // Similarly for lastName
            if (!(TextUtils.isEmpty(lastName))) {

                final String formattedLastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();

                DatabaseReference users = mDataBase.getReference("Users").child(user.getUid());

                users.child("name").child("lastName").setValue(formattedLastName);
            }

            Toast.makeText(ProfileActivity.this, "Changes Successfully Saved", Toast.LENGTH_SHORT).show();
        }
    }
}