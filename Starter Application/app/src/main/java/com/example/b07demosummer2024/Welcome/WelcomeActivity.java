package com.example.b07demosummer2024.Welcome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.b07demosummer2024.HomePage.HomeScreenActivity;
import com.example.b07demosummer2024.Login.LoginActivityView;
import com.example.b07demosummer2024.R;
import com.example.b07demosummer2024.Registration.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onStart() {

        //Check if user is signed in already, if so redirect them to the home page
        super.onStart();

        if (userSignedIn()) {
            // Create Intent to switch to home screen
            Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Register Button, when clicked you are directed to register screen
        Button registerBtn = findViewById(R.id.createAccBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to switch to Register Screen
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Login button, when clicked you are directed to login screen
        Button loginBtn = findViewById(R.id.loginBtnWelcome);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an Intent to switch to Login Screen
                Intent intent = new Intent(getApplicationContext(), LoginActivityView.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean userSignedIn() {
        // Initialize firebase to see if user is already signed in, if they are, send them to the home screen
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        return user != null;
    }
}