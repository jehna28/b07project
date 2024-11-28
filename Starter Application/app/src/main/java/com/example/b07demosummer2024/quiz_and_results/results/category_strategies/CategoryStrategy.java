package com.example.b07demosummer2024.quiz_and_results.results.category_strategies;

public class CategoryStrategy {
    private String question;
    private String strategy;

    public CategoryStrategy(){
        this.question = "";
        this.strategy = "";
    }

    public CategoryStrategy(String question, String strategy) {
        this.question = question;
        this.strategy = strategy;
    }

    public String getQuestion() {
        return question;
    }

    public String getStrategy() {
        return strategy;
    }
}
