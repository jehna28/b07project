package com.example.b07demosummer2024.quiz_and_results.quiz;

import java.io.InputStream;

public class QuizManager {
    private QuestionManager questionManager;
    private ResponseManager responseManager;
    private int currentIndex;

    public QuizManager(InputStream inputStream) {
        questionManager = new QuestionManager(inputStream);
        responseManager = new ResponseManager(questionManager.getNumQuestions());
        currentIndex = 0;
    }

    public Question getCurrentQuestion(){ // returns Question at currentIndex
        return questionManager.getQuestion(currentIndex);
    }

    public String[] getQuestionArr(){
        return questionManager.getQuestionArr();
    }

    public String[] getResults(){
        return responseManager.getResults();
    }

    public String[] getCategories(){
        return questionManager.getCategoryArr();
    }

    public int[] getIndices(){
        return questionManager.getCategoryIndices();
    }

    public String getCurrentResponse(){ // returns saved response at currentIndex, null if no response
        return responseManager.getResponse(currentIndex);
    }

    public int getNumQuestions(){
        return questionManager.getNumQuestions();
    }
    public void saveResponse(String response){
        responseManager.saveResponse(response, currentIndex);
    }

    public boolean questionsLeft(){
        return currentIndex < questionManager.getNumQuestions() - 1;
    }

    public void nextQuestion(String response){
        currentIndex += questionManager.numToSkip(getCurrentQuestion(), response) + 1;
    }

    public void prevQuestion(){
        // if the question can be skipped, reset the saved response
        if (questionManager.isSkippable(questionManager.getQuestion(currentIndex))) {
            responseManager.deleteResponse(currentIndex);
        }
        // go back one question
        currentIndex--;
        // keep going back to until the last answered question
        while (currentIndex > 0 && responseManager.getResponse(currentIndex) == null) {
            currentIndex--;
        }
    }

    public int getCurrentIndex(){
        return currentIndex;
    }

}
