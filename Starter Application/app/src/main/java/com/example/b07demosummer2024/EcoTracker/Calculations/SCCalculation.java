package com.example.b07demosummer2024.EcoTracker.Calculations;

public class SCCalculation {
    // assumptions: one clothing article => 10 kg C02e
    public double getCO2eEmission(double numItems) {
        return numItems*10;
    }
}
