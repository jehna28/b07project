package com.example.b07demosummer2024.EcoTracker.InputNewActivity.Shopping;

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


public class InputShoppingActivity extends AppCompatActivity {

    Button cButton, eButton, opButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_input_shopping);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cButton = (Button)findViewById(R.id.clothes);
        eButton = (Button)findViewById(R.id.electronics);
        opButton = (Button)findViewById(R.id.otherPurchases);

        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputShoppingActivity.this, NewClothesActivity.class);
                String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
                intent.putExtra("SELECTED_DATE", stringDateSelected);
                startActivity(intent);
            }
        });

        eButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputShoppingActivity.this, NewElectronicsActivity.class);
                String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
                intent.putExtra("SELECTED_DATE", stringDateSelected);
                startActivity(intent);
            }
        });

        opButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputShoppingActivity.this, OtherPurchasesActivity.class);
                String stringDateSelected = getIntent().getStringExtra("SELECTED_DATE");
                intent.putExtra("SELECTED_DATE", stringDateSelected);
                startActivity(intent);
            }
        });
    }
}