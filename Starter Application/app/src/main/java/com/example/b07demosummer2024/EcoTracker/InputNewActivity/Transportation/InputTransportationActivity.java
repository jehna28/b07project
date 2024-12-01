package com.example.b07demosummer2024.EcoTracker.InputNewActivity.Transportation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.b07demosummer2024.R;


public class InputTransportationActivity extends AppCompatActivity {

    Button pvButton, ptButton, cwButton, flButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_input_transportation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pvButton = (Button)findViewById(R.id.pv);
        ptButton = (Button)findViewById(R.id.pt);
        cwButton = (Button)findViewById(R.id.cw);
        flButton = (Button)findViewById(R.id.flight);

        pvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputTransportationActivity.this, PersonalVehicleActivity.class);
                String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
                intent.putExtra("SELECTED_DATE", stringDateSelected);  // Pass the date to the next activity
                startActivity(intent);
            }
        });

        ptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputTransportationActivity.this, PublicTransportationActivity.class);
                String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
                intent.putExtra("SELECTED_DATE", stringDateSelected);
                startActivity(intent);
            }
        });

        cwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputTransportationActivity.this, CyclingWalkingActivity.class);
                String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
                intent.putExtra("SELECTED_DATE", stringDateSelected);
                startActivity(intent);
            }
        });

        flButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputTransportationActivity.this, FlightActivity.class);
                String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
                intent.putExtra("SELECTED_DATE", stringDateSelected);
                startActivity(intent);
            }
        });


    }
}