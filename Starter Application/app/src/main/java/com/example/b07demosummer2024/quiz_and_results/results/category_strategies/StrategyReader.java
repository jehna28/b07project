package com.example.b07demosummer2024.quiz_and_results.results.category_strategies;

import android.content.Context;

import java.util.ArrayList;
import java.util.Scanner;

public class StrategyReader {
    Context context;
    String fileName;
    public StrategyReader(Context context, String fileName){
        this.context = context;
        this.fileName = fileName;
    }
    public ArrayList<CategoryStrategy> getStrategies(){
        ArrayList<CategoryStrategy> formulas = new ArrayList<>();
        try {
            Scanner input = new Scanner(context.getAssets().open(fileName));
            // skip headers
            if(input.hasNext()) input.nextLine();
            while (input.hasNext()) {
                String line = input.nextLine();
                String[] tokens = line.split("\\t");
                String question = tokens[0].replaceAll("\"", "");
                String strategy = "";
                if (tokens.length > 1) {
                    strategy = tokens[1].replaceAll("\"", "");
                }
                formulas.add(new CategoryStrategy(question, strategy));
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return formulas;
    }
}
