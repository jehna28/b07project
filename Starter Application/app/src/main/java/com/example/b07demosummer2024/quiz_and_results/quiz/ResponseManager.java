package com.example.b07demosummer2024.quiz_and_results.quiz;

public class ResponseManager {
    private String[] responses;

    public ResponseManager(int numQuestions) {
        responses = new String[numQuestions];
    }
    public String getResponse(int index) {
        //Log.d("responseManager", "returning"+responses[index]);
        return responses[index];
    }

    public void deleteResponse(int index) {
        ////Log.d("responseManager", "before deleting: "+responses[index]);
        responses[index] = null;
        ////Log.d("responseManager", "after deleting: "+responses[index]);
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
        //Log.d("responseManager", "saved response: " + responses[index]);
    }
}
