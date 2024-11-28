package com.example.b07demosummer2024.quiz_and_results.quiz;

import java.io.InputStream;
import java.util.ArrayList;

public class QuestionManager {
    private ArrayList<Question> questions;  // list of all questions
    public QuestionManager(InputStream inputStream){
        // gets all the questions from the file
        questions = QuestionReader.loadQuestions(inputStream);
    }
    public Question getQuestion(int index){
        // returns the question at a specific index
        return questions.get(index);
    }

    public int getNumQuestions(){
        // returns the total number of questions
        return questions.size();
    }

    public String[] getQuestionArr(){
        // gets an array of all questions
        String[] questionArr = new String[getNumQuestions()];
        for (int i = 0; i < getNumQuestions(); i++) {
            questionArr[i] = getQuestion(i).getQuestion();
        }
        return questionArr;
    }

    public String[] getCategoryArr(){
        // gets an array of each question's category
        String[] categoryArr = new String[getNumQuestions()];
        for (int i = 0; i < getNumQuestions(); i++) {
            categoryArr[i] = getQuestion(i).getCategory();
        }
        return categoryArr;
    }

    public boolean isSkippable(Question question){
        // returns true if the question can be skipped, false otherwise
        return question.getSkippable();
    }

    public int numToSkip(Question question, String response){
        // returns num questions > 0 that will be skipped if on a branching question, 0 if not branching
        int toSkip = 0;
        if (question.getBranching() != 0 && !response.equals(question.getBranchingOption())) {
            toSkip = question.getBranching();
        }
        return toSkip;
    }
}
