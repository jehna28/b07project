package com.example.b07demosummer2024.EcoTracker.InputNewActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.b07demosummer2024.EcoTracker.InputNewActivity.Energy.EnergyBillsActivity;
import com.example.b07demosummer2024.EcoTracker.InputNewActivity.Food.InputFoodActivity;
import com.example.b07demosummer2024.EcoTracker.InputNewActivity.Shopping.InputShoppingActivity;
import com.example.b07demosummer2024.EcoTracker.InputNewActivity.Transportation.InputTransportationActivity;
import com.example.b07demosummer2024.R;

public class SelectCategoryActivity extends AppCompatActivity {

    Button tButton, fButton, sButton, eButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tButton = (Button)findViewById(R.id.transportation);
        fButton = (Button)findViewById(R.id.food);
        sButton = (Button)findViewById(R.id.shopping);
        eButton = (Button)findViewById(R.id.energy);

        tButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategoryActivity.this, InputTransportationActivity.class);
                String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
                intent.putExtra("SELECTED_DATE", stringDateSelected);  // Pass the date to the next activity
                int cntActs = getIntent().getIntExtra("ACTIVITY_COUNT", 0); // Default value is 0
                intent.putExtra("ACTIVITY_COUNT", cntActs);
                startActivity(intent);
            }
        });

        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategoryActivity.this, InputFoodActivity.class);
                String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
                intent.putExtra("SELECTED_DATE", stringDateSelected);  // Pass the date to the next activity
                int cntActs = getIntent().getIntExtra("ACTIVITY_COUNT", 0); // Default value is 0
                intent.putExtra("ACTIVITY_COUNT", cntActs);
                startActivity(intent);
            }
        });

        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategoryActivity.this, InputShoppingActivity.class);
                String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
                intent.putExtra("SELECTED_DATE", stringDateSelected);  // Pass the date to the next activity
                int cntActs = getIntent().getIntExtra("ACTIVITY_COUNT", 0); // Default value is 0
                intent.putExtra("ACTIVITY_COUNT", cntActs);
                startActivity(intent);
            }
        });

        eButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCategoryActivity.this, EnergyBillsActivity.class);
                String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
                intent.putExtra("SELECTED_DATE", stringDateSelected);  // Pass the date to the next activity
                int cntActs = getIntent().getIntExtra("ACTIVITY_COUNT", 0); // Default value is 0
                intent.putExtra("ACTIVITY_COUNT", cntActs);
                startActivity(intent);
            }
        });

    }
}