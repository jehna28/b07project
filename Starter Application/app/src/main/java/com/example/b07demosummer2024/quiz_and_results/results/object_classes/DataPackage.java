package com.example.b07demosummer2024.quiz_and_results.results.object_classes;

import android.util.Log;

import java.util.ArrayList;

public class DataPackage {

    String[] questions;
    String[] responses;
    String[] categories;

    public DataPackage(String[] questions, String[] responses, String[] categories){
        this.questions = questions;
        this.responses = responses;
        this.categories = categories;
    }
    public ArrayList<QuestionData> getQuestionData(){
        ArrayList<QuestionData> data = new ArrayList<>();
        for (int i = 0; i < questions.length; i++) {
            data.add(new QuestionData(questions[i], responses[i], categories[i]));
        }
        return data;
    }
    public void displayQuestionData(){
        for (int i = 0; i < questions.length; i++) {
            Log.d("dataPackage", "Question: " + questions[i] + "\nCategory: " + categories[i] + "\nResponse: " + responses[i] + "\n");
        }
    }

}
