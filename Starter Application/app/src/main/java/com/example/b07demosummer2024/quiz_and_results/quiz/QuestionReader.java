package com.example.b07demosummer2024.quiz_and_results.quiz;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class QuestionReader {
    public static ArrayList<Question> loadQuestions(InputStream inputStream){
        ArrayList<Question> questions = new ArrayList<Question>();
        try {
            //Log.d("questionReader", "getting file");
            Scanner input = new Scanner(inputStream);
            //Log.d("questionReader", "got file");
            // skip headers
            if(input.hasNext()) input.nextLine();
            while (input.hasNext()) {
                String line = input.nextLine();
                String[] tokens = line.split("\\t");
                String question = tokens[0].replaceAll("\"", "");
                String category = tokens[1].replaceAll("\"", "");
                int branching = Integer.valueOf(tokens[2]);
                String branchingOptions = tokens[3].replaceAll("\"","");
                boolean skippable = Boolean.valueOf(tokens[4]);
                String[] options = new String[tokens.length-5];
                for (int i = 0; i < options.length; i++){
                    options[i] = tokens[i+5].replaceAll("\"","");
                }
                for (int i = 0; i < tokens.length; i++) {
                    //Log.d("questionReader", tokens[i]);
                }
                questions.add(new Question(question, category, branching, branchingOptions, skippable, options));
            }
        }
        catch (Exception e) {
            //Log.d("questionReader", "error");
            throw new RuntimeException(e);
        }
        return questions;
    }
}
