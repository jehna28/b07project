package com.example.b07demosummer2024.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;

import androidx.appcompat.app.AppCompatActivity;

import com.example.b07demosummer2024.HomePage.HomeScreenActivity;
import com.example.b07demosummer2024.R;
import com.example.b07demosummer2024.Welcome.WelcomeActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivityView extends AppCompatActivity {
    private TextInputEditText emailTxtField, passwordTxtField;

    private LoginActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        emailTxtField = findViewById(R.id.emailTxtFieldLogin);
        passwordTxtField = findViewById(R.id.passwordTxtFieldLogin);

        Button loginBtn = findViewById(R.id.loginBtn);
        Button forgotPasswordBtn = findViewById(R.id.forgotPasswordBtn);

        // Initialize presenter with view and model
        presenter = new LoginActivityPresenter(this, new LoginActivityModel());

        // On click listeners when buttons are pressed

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Grab typed out credentials
                String email = String.valueOf(emailTxtField.getText());
                String password = String.valueOf((passwordTxtField.getText()));

                // Pass values to presenter to check if user can be logged in
                presenter.loginBtnClicked(email, password);
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Grab typed out email
                String email = String.valueOf(emailTxtField.getText());

                // Pass email to presenter to check if email can be sent a password reset
                presenter.forgotPasswordBtnClicked(email);
            }
        });

        goBack();
    }

    public void toastMsg(String message) {

        // Used for quick small messages for user (like errors or success messages)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void goHome() {

        // Create an Intent to switch to Home Screen
        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
        startActivity(intent);
        finish();

    }

    private void goBack() {
        // Use OnBackPressedDispatcher for handling back button presses
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

}
