package com.example.b07demosummer2024.EcoTracker.Calculations;

public class SECalculation {
    // assumptions: smartphone => 60 kg C02e; tablet => 130 kg C02e; laptop => 250 C02e
    public double getCO2eEmission(String typeDevice, double numItems) {

        double C02eEmission;

        if (typeDevice.equals("Smartphone")) {
            C02eEmission = 60 * numItems;
        }
        else if (typeDevice.equals("Tablet")) {
            C02eEmission = 130 * numItems;
        }
        else {
            C02eEmission = 250 * numItems;
        }

        return C02eEmission;
    }
}
