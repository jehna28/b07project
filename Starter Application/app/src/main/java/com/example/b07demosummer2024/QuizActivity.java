package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {

    private int CurrentProgress = 0;
    private ProgressBar progressBar;
    private Button startProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        progressBar = findViewById(R.id.progress_bar);
        startProgress = findViewById(R.id.buttonNext);

        startProgress.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CurrentProgress += 10;
                progressBar.setProgress(CurrentProgress);
                progressBar.setMax(210);



            }
        });

    }

}

