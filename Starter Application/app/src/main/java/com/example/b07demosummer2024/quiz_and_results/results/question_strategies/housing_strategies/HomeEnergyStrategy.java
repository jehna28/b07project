package com.example.b07demosummer2024.quiz_and_results.results.question_strategies.housing_strategies;

import android.content.Context;

import com.example.b07demosummer2024.quiz_and_results.results.object_classes.HousingData;
import com.example.b07demosummer2024.quiz_and_results.results.object_classes.QuestionData;
import com.example.b07demosummer2024.quiz_and_results.results.question_strategies.QuestionStrategy;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class HomeEnergyStrategy implements QuestionStrategy {
    private final double PARTIAL_DEDUCTION = 4000.0;
    private final double PRIMARY_DEDUCTION = 6000.0;
    private final double DIFF_WATER_HEATING = 233.0;
    private String homeType = "";
    private String householdSize = "";
    private String homeSize = "";
    private String heatEnergy = "";
    private String waterEnergy = "";
    private String monthlyBill = "";
    private String renewableEnergy = "";
    private HousingData values;
    private Context context;

    public HomeEnergyStrategy(Context context){
        // need Context to access assets folder
        this.context = context;
    }
    @Override
    public double getEmissions(ArrayList<QuestionData> data, String response) {
        // initialize responses regarding housing
        homeType = response;
        getHouseResponses(data);
        // get the values for the user's home type and size
        String homeTypeAndSize = findHomeTypeAndSize(homeType, homeSize);
        values = getHouseData(context, homeTypeAndSize);
        // get the specific values for the user's household size (number of occupants)
        String[] valuesPerNumOcc = getValuesPerNumOcc(values, householdSize);
        // get the emissions value for the user's heating energy type and monthly bill amount
        int energyIndex = getEnergyIndex(heatEnergy);
        int billIndex = getBillIndex(monthlyBill);
        double emissions = getEmissionValue(valuesPerNumOcc, energyIndex, billIndex);
        // add/deduct from emissions depending on renewable energy usage and water heating energy type
        emissions += addIfWaterHeatDiff(waterEnergy, heatEnergy);
        emissions -= deductIfRenewable(renewableEnergy);
        // return the user's housing emissions, or 0 if emissions is negative
        return Math.max(0, emissions);
    }
    private double deductIfRenewable(String renewableEnergy){
        if (renewableEnergy.equals("Yes, primarily (more than 50% of energy use)")){
            return PRIMARY_DEDUCTION;
        }
        else if (renewableEnergy.equals("Yes, partially (less than 50% of energy use)")){
            return PARTIAL_DEDUCTION;
        }
        return 0.0;
    }
    private double addIfWaterHeatDiff(String waterEnergy, String heatEnergy){
        if (waterEnergy.equals(heatEnergy)){
            return 0;
        }
        return DIFF_WATER_HEATING;
    }
    private double getEmissionValue(String[] values, int energy, int bill){
        return Double.parseDouble(values[(5 * bill) + energy]);
    }
    private int getEnergyIndex(String heatEnergy){
        if (heatEnergy.equals("Natural Gas")) {
            return 0;
        }
        else if (heatEnergy.equals("Electricity")) {
            return 1;
        }
        else if (heatEnergy.equals("Oil")) {
            return 2;
        }
        else if (heatEnergy.equals("Propane")){
            return 3;
        }
        else { // wood
            return 4;
        }
    }
    private int getBillIndex(String monthlyBill){
        if (monthlyBill.equals("Under $50")) {
            return 0;
        }
        else if (monthlyBill.equals("$50-$100")) {
            return 1;
        }
        else if (monthlyBill.equals("$100-$150")) {
            return 2;
        }
        else if (monthlyBill.equals("$150-$200")){
            return 3;
        }
        else { // Over $200
            return 4;
        }
    }
    private String findHomeTypeAndSize(String homeType, String homeSize) {
        String type = "townhouse";
        String size = "over 2000";
        if (homeType.equals("Detached house")) {
            type = "detached";
        }
        else if (homeType.equals("Semi-detached house")) {
            type = "semi-detached";
        }
        else if (homeType.equals("Condo/Apartment")) {
            type = "condoapp";
        }
        if (homeSize.equals("Under 1000 sq. ft.")) {
            size = "under 1000";
        }
        else if (homeSize.equals("1000-2000 sq. ft.")) {
            size = "1000-2000";
        }
        return type + ", " + size;
    }
    private String[] getValuesPerNumOcc(HousingData values, String numOcc){
        if (numOcc.equals("2")){
            return values.getTwoOcc();
        }
        else if (numOcc.equals("3-4")){
            return values.getThreeFourOcc();
        }
        else if (numOcc.equals("5 or more")){
            return values.getFiveOcc();
        }
        return values.getOneOcc();
    }
    private void getHouseResponses(ArrayList<QuestionData> data){
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getQuestion().equals("How many people live in your household?")){
                householdSize = data.get(i).getResponse();
            }
            else if (data.get(i).getQuestion().equals("What is the size of your home?")){
                homeSize = data.get(i).getResponse();
            }
            else if (data.get(i).getQuestion().equals("What type of energy do you use to heat your home?")){
                heatEnergy = data.get(i).getResponse();
            }
            else if (data.get(i).getQuestion().equals("What is your average monthly electricity bill?")){
                monthlyBill = data.get(i).getResponse();
            }
            else if (data.get(i).getQuestion().equals("What type of energy do you use to heat water in your home?")){
                waterEnergy = data.get(i).getResponse();
            }
            else if (data.get(i).getQuestion().equals("Do you use any renewable energy sources for electricity or heating (e.g., solar, wind)?")){
                renewableEnergy = data.get(i).getResponse();
            }
        }
    }
    private HousingData getHouseData(Context context, String homeTypeAndSize){
        HousingData newValues = new HousingData();
        try {
            // try to read from cleaned housing file, make sure file name and path is correct!
            InputStream inputStream = context.getAssets().open("emission_quiz_assets/emission_housing_tables/housing_cleaned.txt");
            Scanner input = new Scanner(inputStream);
            // get home type and size from the first line
            String line = input.nextLine().trim().replaceAll("\"", "");
            // continue to step through file until we have obtained the user's homeTypeAndSize
            while (input.hasNext() && !line.equals(homeTypeAndSize)) {
                line = input.nextLine().trim().replaceAll("\"", "");
            }
            // once we have found the desired homeTypeAndSize,
            if (!line.isEmpty() && line.equals(homeTypeAndSize)) {
                // skip to values
                input.nextLine();
                input.nextLine();

                // save values for one occupant
                String[] oneOccTokens = input.nextLine().split("\\t");
                String[] oneOcc = Arrays.copyOfRange(oneOccTokens, 1, oneOccTokens.length);

                // save values for two occupants
                String[] twoOccTokens = input.nextLine().split("\\t");
                String[] twoOcc = Arrays.copyOfRange(twoOccTokens, 1, twoOccTokens.length);

                // save values for three-four occupants
                String[] threeFourOccTokens = input.nextLine().split("\\t");
                String[] threeFourOcc = Arrays.copyOfRange(threeFourOccTokens, 1, threeFourOccTokens.length);

                // save values for five+ occupants
                String[] fiveOccTokens = input.nextLine().split("\\t");
                String[] fiveOcc = Arrays.copyOfRange(fiveOccTokens, 1, fiveOccTokens.length);

                newValues = new HousingData(line, oneOcc, twoOcc, threeFourOcc, fiveOcc);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to read from housing data file...");
        }
        return newValues;
    }
}
