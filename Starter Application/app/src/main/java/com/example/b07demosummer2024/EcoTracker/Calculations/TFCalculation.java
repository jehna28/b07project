package com.example.b07demosummer2024.EcoTracker.Calculations;

public class TFCalculation {
    public double getCO2eEmission(String typeOfFlight, double val) {
        double C02eEmission = 0;

        if (typeOfFlight.equals("Short-Haul (<1,500km)")) {
            if (val == 0) {
                C02eEmission = 0;
            }
            else if (val <= 2) {
                C02eEmission = 225;
            }
            else if (val <= 5) {
                C02eEmission = 600;
            }
            else if (val <= 10) {
                C02eEmission = 1200;
            }
            else {
                C02eEmission = 1800;
            }
        } else {
            if (val == 0) {
                C02eEmission = 0;
            }
            else if (val <= 2) {
                C02eEmission = 825;
            }
            else if (val <= 5) {
                C02eEmission = 2200;
            }
            else if (val <= 10) {
                C02eEmission = 4400;
            }
            else {
                C02eEmission = 6600;
            }
        }
        return C02eEmission;
    }
}
