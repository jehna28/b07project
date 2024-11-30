package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;


/**
 * The following class produces a SplashScreen of the company "Planetze" with its
 * mission statement alongside.
 * The SplashScreen will appear for three seconds and will navigate to the Welcome Page of the
 * Application.
 * @author Alena Carvalho
 * @since 2024-11-05
 */
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 3000; // 3 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Use a Handler to post a delayed action for transitioning to the Welcome activity
        new Handler().postDelayed(() -> {
            // Start the Welcome Activity after the splash screen duration
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
