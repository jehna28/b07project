package com.example.b07demosummer2024.quiz_and_results.quiz;

import java.io.InputStream;

public class QuizManager {
    private QuestionManager questionManager;
    private ResponseManager responseManager;
    private int currentIndex;

    public QuizManager(InputStream inputStream) {
        // create a QuestionManager and a ResponseManager, begin at index 0
        questionManager = new QuestionManager(inputStream);
        responseManager = new ResponseManager(questionManager.getNumQuestions());
        currentIndex = 0;
    }

    public Question getCurrentQuestion(){
        // returns Question at currentIndex
        return questionManager.getQuestion(currentIndex);
    }

    public String[] getQuestionArr(){
        // returns an array containing all the questions as Strings
        return questionManager.getQuestionArr();
    }

    public String[] getResults(){
        // returns an array containing all the responses as Strings
        return responseManager.getResults();
    }

    public String[] getCategories(){
        // returns an array containing all the questions' categories as Strings
        return questionManager.getCategoryArr();
    }

    public String getCurrentResponse(){
        // returns saved response at currentIndex, null if no response
        return responseManager.getResponse(currentIndex);
    }

    public int getNumQuestions(){
        // returns the total number of questions
        return questionManager.getNumQuestions();
    }
    public void saveResponse(String response){
        // saves the user's response for the current question
        responseManager.saveResponse(response, currentIndex);
    }

    public boolean questionsLeft(){
        // returns true if there are questions remaining after the current question, false otherwise
        return currentIndex < questionManager.getNumQuestions() - 1;
    }

    public void nextQuestion(String response){
        // jumps to the next question to be asked
        currentIndex += questionManager.numToSkip(getCurrentQuestion(), response) + 1;
    }

    public void prevQuestion(){
        // if the question can be skipped, reset the saved response
        if (questionManager.isSkippable(questionManager.getQuestion(currentIndex))) {
            responseManager.deleteResponse(currentIndex);
        }
        // go back one question
        currentIndex--;
        // keep going back until currentIndex is at the last answered question
        while (currentIndex > 0 && responseManager.getResponse(currentIndex) == null) {
            currentIndex--;
        }
    }

    public int getCurrentIndex(){
        return currentIndex;
    }

}
