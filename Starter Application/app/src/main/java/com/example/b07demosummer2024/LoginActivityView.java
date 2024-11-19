package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivityView extends AppCompatActivity {

    // UI elements used
    private TextInputEditText emailTxtField, passwordTxtField;
    private Button loginBtn, forgotPasswordBtn, goBackBtn;

    private LoginActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        emailTxtField = findViewById(R.id.emailTxtFieldLogin);
        passwordTxtField = findViewById(R.id.passwordTxtFieldLogin);

        loginBtn = findViewById(R.id.loginBtn);
        forgotPasswordBtn = findViewById(R.id.forgotPasswordBtn);
        goBackBtn = findViewById(R.id.goBackBtnLogin);

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

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an Intent to switch to Welcome Screen
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    public void toastMsg(String message) {

        // Used for quick small messages for user (like errors or success messages)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void goHome(String user) {

        // Create an Intent to switch to Home Screen (right now set to main activity for to see if this works)
        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
        startActivity(intent);
        finish();

    }

}
