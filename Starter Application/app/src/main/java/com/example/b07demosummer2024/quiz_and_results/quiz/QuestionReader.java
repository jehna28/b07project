package com.example.b07demosummer2024.quiz_and_results.quiz;

import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class QuestionReader {
    public static ArrayList<Question> loadQuestions(InputStream inputStream) throws RuntimeException{
        // returns an array list of questions read from the questions file
        ArrayList<Question> questions = new ArrayList<Question>();
        try {
            Scanner input = new Scanner(inputStream);
            // skip headers
            if(input.hasNext()) input.nextLine();
            // parse through each line
            while (input.hasNext()) {
                String line = input.nextLine();
                String[] tokens = line.split("\\t");
                // save each question and their respective categories, options, and other information
                String question = tokens[0].replaceAll("\"", "");
                String category = tokens[1].replaceAll("\"", "");
                int branching = Integer.valueOf(tokens[2]);
                String branchingOptions = tokens[3].replaceAll("\"","");
                boolean skippable = Boolean.valueOf(tokens[4]);
                String[] options = new String[tokens.length-5];
                for (int i = 0; i < options.length; i++){
                    options[i] = tokens[i+5].replaceAll("\"","");
                }
                // create a new question object with the read information, then add to the array list
                questions.add(new Question(question, category, branching, branchingOptions, skippable, options));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to read from questions file...");
        }
        return questions;
    }
}
