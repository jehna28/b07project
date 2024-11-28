package com.example.b07demosummer2024.quiz_and_results.results.calculators;

import android.content.Context;

import com.example.b07demosummer2024.quiz_and_results.results.category_strategies.CategoryStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.category_strategies.StrategyReader;
import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.consumption_strategies.DeviceStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.consumption_strategies.RecycleStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.consumption_strategies.SecondHandStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.food_strategies.BeefStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.food_strategies.ChickenStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.food_strategies.DietStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.food_strategies.LeftoversStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.food_strategies.PorkStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.food_strategies.SeafoodStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.housing_strategies.HomeEnergyStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.transportation_strategies.DrivingStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.transportation_strategies.LongHaulStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.transportation_strategies.PublicTransportStrategy;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.transportation_strategies.ShortHaulStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class EmissionCalculator {
    ArrayList<QuestionData> data;
    ArrayList<CategoryStrategy> strategies;
    Map<String, QuestionStrategy> strategyMap;
    public EmissionCalculator(){
        this.data = new ArrayList<>();
        this.strategies = new ArrayList<>();
        this.strategyMap = new HashMap<>();
    }
    public EmissionCalculator(ArrayList<QuestionData> data, Context context, String category, String strategyFile){
        this.data = filterData(data, category);
        this.strategies = new StrategyReader(context, strategyFile).getStrategies();
        this.strategyMap = initializeStrategyMap();
    }
    public double getFootprint(){
        double total = 0;

        for (int i = 0; i < data.size(); i++) {
            String strategy = "";
            String question = data.get(i).getQuestion();
            String response = data.get(i).getResponse();
            if (response.isEmpty()) continue;
            for (int j = 0; j < this.strategies.size(); j++) {
                if (question.equals(this.strategies.get(j).getQuestion())) {
                    strategy = this.strategies.get(j).getStrategy();
                    break;
                }
            }
            QuestionStrategy strategyToUse = strategyMap.get(strategy);
            if (strategyToUse != null) {
                total += strategyToUse.getEmissions(data, response);
            }
        }
        return total;
    }
    protected Map<String, QuestionStrategy> initializeStrategyMap(){
        strategyMap = new HashMap<>();
        strategyMap.put("SecondHandStrategy", new SecondHandStrategy());
        strategyMap.put("DeviceStrategy", new DeviceStrategy());
        strategyMap.put("RecycleStrategy", new RecycleStrategy());
        strategyMap.put("DietStrategy", new DietStrategy());
        strategyMap.put("BeefStrategy", new BeefStrategy());
        strategyMap.put("PorkStrategy", new PorkStrategy());
        strategyMap.put("ChickenStrategy", new ChickenStrategy());
        strategyMap.put("SeafoodStrategy", new SeafoodStrategy());
        strategyMap.put("LeftoversStrategy", new LeftoversStrategy());
        strategyMap.put("HomeEnergyStrategy", new HomeEnergyStrategy());
        strategyMap.put("DrivingStrategy", new DrivingStrategy());
        strategyMap.put("PublicTransportStrategy", new PublicTransportStrategy());
        strategyMap.put("ShortHaulStrategy", new ShortHaulStrategy());
        strategyMap.put("LongHaulStrategy", new LongHaulStrategy());
        return strategyMap;
    }
    protected ArrayList<QuestionData> filterData(ArrayList<QuestionData> data, String category){
        ArrayList<QuestionData> filteredData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++){
            if (data.get(i).getCategory().equals(category)) {
                filteredData.add(data.get(i));
            }
        }
        return filteredData;
    }
}
