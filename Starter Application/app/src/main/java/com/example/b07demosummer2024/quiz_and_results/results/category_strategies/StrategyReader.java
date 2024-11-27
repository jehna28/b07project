package com.example.b07demosummer2024.quiz_and_results.results.category_strategies;

import android.content.Context;
import android.util.Log;

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
        Log.d("strategyReader", "entered getStrategies");
        ArrayList<CategoryStrategy> formulas = new ArrayList<>();
        try {
            Scanner input = new Scanner(context.getAssets().open(fileName));
            // skip headers
            Log.d("strategyReader", "got scanner");
            if(input.hasNext()) input.nextLine();
            while (input.hasNext()) {
                String line = input.nextLine();
                String[] tokens = line.split("\\t");
                String question = tokens[0].replaceAll("\"", "");
                String strategy = "";
                if (tokens.length > 1) {
                    strategy = tokens[1].replaceAll("\"", "");
                }
                Log.d("strategyReader", "Question: "+question);
                Log.d("strategyReader", "Strategy: "+strategy);
                formulas.add(new CategoryStrategy(question, strategy));
            }
        }
        catch (Exception e) {
            //Log.d("strategyReader", "error");
            throw new RuntimeException(e);
        }
        return formulas;
    }
}
