package com.example.b07demosummer2024.quiz_and_results.results.object_classes;

public class QuestionData {
    String question;
    String response;
    String category;
    public QuestionData(String question, String response, String category) {
        this.question = question;
        this.response = response;
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public String getResponse() {
        return response;
    }

    public String getCategory() {
        return category;
    }
}
