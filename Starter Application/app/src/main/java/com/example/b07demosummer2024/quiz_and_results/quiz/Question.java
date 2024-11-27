package com.example.b07demosummer2024.quiz_and_results.quiz;

public class Question {
    String question;
    String category;
    int branching;
    String branchingOption;
    boolean skippable;
    String[] options;

    public Question(String question, String category, int branching, String branchingOption, boolean skippable, String[] options) {
        this.question = question;
        this.category = category;
        this.branching = branching;
        this.branchingOption = branchingOption;
        this.skippable = skippable;
        this.options = options;
    }
    public String getQuestion(){
        return this.question;
    }
    public String getCategory(){
        return this.category;
    }
    public int getBranching(){
        return this.branching;
    }
    public boolean getSkippable(){
        return this.skippable;
    }
    public String[] getOptions(){
        return this.options;
    }
}
