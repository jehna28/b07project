package com.example.b07demosummer2024.quiz_and_results.quiz;

import java.io.InputStream;
import java.util.ArrayList;

public class QuestionManager {
    private ArrayList<Question> questions;
    private final int NUM_CAR_Q = 2;
    private final int NUM_DIET_Q = 4;
    private final int[] CATEGORY_START_INDICES = new int[]{0, 7, 13, 20};
    //[first transportation q, first food q, first housing q, first consumption q]

    public QuestionManager(InputStream inputStream){
        questions = QuestionReader.loadQuestions(inputStream);
    }
    public Question getQuestion(int index){
        return questions.get(index);
    }

    public int getNumQuestions(){
        return questions.size();
    }

    public int[] getCategoryIndices(){
        return CATEGORY_START_INDICES;
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
        return question.skippable;
    }

    public int numToSkip(Question question, String response){
    // returns num questions > 0 that will be skipped if on a branching question, 0 if not branching
        int toSkip = 0;
        if (question.branching != 0 && !response.equals(question.branchingOption)) {
            toSkip = question.branching;
        }
        return toSkip;
    }
}
