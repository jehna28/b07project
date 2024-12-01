package com.example.b07demosummer2024.EcoTracker.Calculations;

public class TPTCalculation {
    public double getCO2eEmission(double val) {
        double C02eEmission = 0;
        if (val == 0) {
            C02eEmission = 0;
        }
        // 246 kg, 1.5 times a week is 3.14102564103 kg
        // 573 kg, 3.5 times a week is 3.14835164835 kg
        else if (val <= 1) {
            C02eEmission = 3.14835164835; // taking the max
        }
        // 819 kg, 1.5 times a week is 10.5
        // 1911 kg, 3.5 times a week is 10.5
        else if (val <=3) {
            C02eEmission = 10.5;
        }
        // 1638 kg, 1.5 times a week is 21
        // 3822 kg, 3.5 times a week is 21
        else if (val <= 5) {
            C02eEmission = 21;
        }
        // 3071 kg, 1.5 times a week is 39.3717948718
        // 7166 kg, 3.5 times a week is 39.3736263736
        else if (val <= 10) {
            C02eEmission = 39.3736263736; // taking the max
        }
        // 4095 kg, 1.5 times a week is 52.5
        // 9555 kg, 3.5 times a week is 52.5
        else {
            C02eEmission = 52.5;
        }
        return C02eEmission;
    }
}
