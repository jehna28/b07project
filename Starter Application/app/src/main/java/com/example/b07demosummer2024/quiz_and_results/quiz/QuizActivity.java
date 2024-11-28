package com.example.b07demosummer2024.quiz_and_results.quiz;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.b07demosummer2024.HomeScreenActivity;
import com.example.b07demosummer2024.R;
import com.example.b07demosummer2024.quiz_and_results.results.ResultsActivity;

import java.io.IOException;
import java.io.InputStream;

public class QuizActivity extends AppCompatActivity {

    private QuizManager quizManager;
    private TextView textQuestion;
    private Button buttonNext;
    private RadioGroup radioOptions;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        // initialize views
        progressBar = findViewById(R.id.progress_bar);
        buttonNext = findViewById(R.id.buttonNext);
        textQuestion = findViewById(R.id.textQuestion);
        radioOptions = findViewById(R.id.radioOptions);
        // initialize manager (to handle logic)
        quizManager = getQuizManager();
        // display first question
        loadQuestion();

        // set listener for when button is pressed
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if no radio button is selected, send an incomplete toast
                int selectedId = radioOptions.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    incompleteToast();
                }
                else {
                    // otherwise,
                    // save the response
                    RadioButton selectedRadio = findViewById(selectedId);
                    String selectedResponse = selectedRadio.getText().toString();
                    quizManager.saveResponse(selectedResponse);
                    // if there are questions left, load the next question
                    if (quizManager.questionsLeft()) {
                        quizManager.nextQuestion(selectedResponse);
                        loadQuestion();
                    }
                    // if there are no more questions left, go to results
                    else {
                        goToResults();
                    }
                }
            }
        });

    }
    private void loadQuestion() {

        // get current question to display
        Question currentQ = quizManager.getCurrentQuestion();

        // update progress bar
        progressBar.setProgress(quizManager.getCurrentIndex());
        progressBar.setMax(quizManager.getNumQuestions());

        // set text to display question and remove previous radio buttons
        textQuestion.setText(currentQ.getQuestion());
        radioOptions.removeAllViews();
        radioOptions.clearCheck();

        // get array of all options for the current question
        String[] currentOptions = currentQ.getOptions();

        // add radiobuttons for each option using a for-loop
        for (int i = 0; i < currentOptions.length; i++) {
            // initialize and add radio button to group, setting text to display option
            RadioButton newRadioOption = new RadioButton(this);
            newRadioOption.setText(currentOptions[i]);
            styleRadioButton(newRadioOption);
            radioOptions.addView(newRadioOption);
            // if a response is already saved, we set that option as already checked
            String currentResponse = quizManager.getCurrentResponse();
            if (currentResponse != null && currentResponse.equals(currentOptions[i])){
                newRadioOption.setChecked(true);
            }
        }
    }

    private void styleRadioButton(RadioButton button){
        // set the style of the radio button
        button.setTextColor(getResources().getColor(R.color.white));
        button.setBackgroundResource(R.drawable.radio_button_bg);
        Typeface typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD);
        button.setTypeface(typeface);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.setMargins(5,5,5,5);
        button.setLayoutParams(buttonParams);
        }

    private QuizManager getQuizManager(){
        // initialize the QuizManager by sending the question file as an argument
        AssetManager assetManager = getAssets();
        try {
            // make sure the file name matches the quiz questions .txt file!
            InputStream inputStream = assetManager.open("emission_quiz_assets/emission_quiz_questions/quiz_questions.txt");
            return new QuizManager(inputStream);
        }
        catch (Exception e) {
            Toast.makeText(this, "Currently unable to load quiz, moving to home screen...", Toast.LENGTH_LONG).show();
            goBackHome();
            return null;
        }
    }

    private void goBackHome(){
        // force user to the home screen if exceptions occur (fail-safe)
        Intent intent = new Intent(QuizActivity.this, HomeScreenActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // if not on the first question, do the following
        if (quizManager.getCurrentIndex() > 0) {
            quizManager.prevQuestion();
            // load the last answered question
            loadQuestion();
        }
        // if on the first question, call super.onBackPressed() to go back to previous screen
        else {
            super.onBackPressed();
        }
    }

    private void goToResults(){
        // save and send the saved responses to ResultsActivity
        Intent intent = new Intent(QuizActivity.this, ResultsActivity.class);
        saveData(intent);
        startActivity(intent);
    }

    private void saveData(Intent intent){
        String[] questions = quizManager.getQuestionArr();
        String[] responses = quizManager.getResults();
        String[] categories = quizManager.getCategories();
        intent.putExtra("RESPONSES", responses);
        intent.putExtra("CATEGORIES", categories);
        intent.putExtra("QUESTIONS", questions);
    }

    private void incompleteToast(){
        // toast for when the user does not select an option
        Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
    }

}

