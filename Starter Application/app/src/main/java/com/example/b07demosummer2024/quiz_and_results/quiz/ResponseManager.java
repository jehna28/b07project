package com.example.b07demosummer2024.quiz_and_results.quiz;

public class ResponseManager {
    private String[] responses;

    public ResponseManager(int numQuestions) {
        // initializes a String array in which the user's responses will be saved
        // index of each response will match the index of the corresponding question in the questions array list
        responses = new String[numQuestions];
    }
    public String getResponse(int index) {
        // returns response at certain index
        return responses[index];
    }

    public void deleteResponse(int index) {
        // clears response at certain index
        responses[index] = null;
    }

    public String[] getResults(){
        // returns responses with all null responses replaced with an empty string
        replaceNull();
        return responses;
    }

    private void replaceNull(){
        // replace all null responses with an empty string
        for (int i = 0; i < responses.length; i++){
            if (responses[i] == null) {
                responses[i] = "";
            }
        }
    }

    public void saveResponse(String response, int index) {
        // save response at certain index of the responses array
        responses[index] = response;
    }
}
