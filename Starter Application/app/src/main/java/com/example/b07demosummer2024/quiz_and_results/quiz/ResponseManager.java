package com.example.b07demosummer2024.quiz_and_results.quiz;

public class ResponseManager {
    private String[] responses;

    public ResponseManager(int numQuestions) {
        responses = new String[numQuestions];
    }
    public String getResponse(int index) {
        return responses[index];
    }

    public void deleteResponse(int index) {
        responses[index] = null;
    }

    public String[] getResults(){ // returns responses with all null responses replace with empty string
        replaceNull();
        return responses;
    }

    private void replaceNull(){
        for (int i = 0; i < responses.length; i++){
            if (responses[i] == null) {
                responses[i] = "";
            }
        }
    }

    public void saveResponse(String response, int index) {
        responses[index] = response;
    }
}
