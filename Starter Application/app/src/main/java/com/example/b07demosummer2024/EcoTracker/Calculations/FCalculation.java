package com.example.b07demosummer2024.EcoTracker.Calculations;

public class FCalculation {

    // the following C02eEmission is based off of https://www.co2everything.com/category/food
    public double getCO2eEmission(String foodType, double numServings) {
        double CO2eEmission = 0;

        if (foodType.equals("Beef")) {
            CO2eEmission = 15.5 * numServings;
        }
        else if (foodType.equals("Pork")) {
            CO2eEmission = 2.4 * numServings;
        }
        else if (foodType.equals("Chicken")) {
            CO2eEmission = 1.82 * numServings;
        }
        else if (foodType.equals("Fish")) {
            CO2eEmission = 1.34 * numServings;
        }
        else {
            CO2eEmission = 0.1 * numServings;
        }
        return CO2eEmission;
    }
}
