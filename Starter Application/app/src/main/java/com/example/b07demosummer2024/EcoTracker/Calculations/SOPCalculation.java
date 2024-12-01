package com.example.b07demosummer2024.EcoTracker.Calculations;

public class SOPCalculation {
    // assumptions: appliances => 300 kg C02e; furniture => 100 kg C02e
    public double getCO2eEmission(String typePurchase, double numItems) {

        double C02eEmission;

        if (typePurchase.equals("Smartphone")) {
            C02eEmission = 300 * numItems;
        }
        else {
            C02eEmission = 100 * numItems;
        }

        return C02eEmission;
    }
}
