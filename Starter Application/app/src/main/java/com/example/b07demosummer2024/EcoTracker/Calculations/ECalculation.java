package com.example.b07demosummer2024.EcoTracker.Calculations;

public class ECalculation {
    // treat this as a placeholder calculation for now
        // this calculation only applies to detached homes < 10 000 sq ft with one occupant
    // note: water energy type C02e emission was not specified
    public double getCO2eEmission(String typeEnergy, double bill) {

        double C02eEmission;

        if (bill < 50) {
            if (typeEnergy.equals("Electricity")) {
                C02eEmission = 200;
            }
            else if (typeEnergy.equals("Gas")) {
                C02eEmission = 2400;
            }
            else {
                C02eEmission = 0;
            }
        }
        else if (bill < 100) {
            if (typeEnergy.equals("Electricity")) {
                C02eEmission = 400;
            }
            else if (typeEnergy.equals("Gas")) {
                C02eEmission = 2440;
            }
            else {
                C02eEmission = 0;
            }
        }
        else if (bill < 150) {
            if (typeEnergy.equals("Electricity")) {
                C02eEmission = 1200;
            }
            else if (typeEnergy.equals("Gas")) {
                C02eEmission = 2610;
            }
            else {
                C02eEmission = 0;
            }
        }
        else if (bill < 200) {
            if (typeEnergy.equals("Electricity")) {
                C02eEmission = 1700;
            }
            else if (typeEnergy.equals("Gas")) {
                C02eEmission = 2780;
            }
            else {
                C02eEmission = 0;
            }
        }
        else {
            if (typeEnergy.equals("Electricity")) {
                C02eEmission = 2300;
            }
            else if (typeEnergy.equals("Gas")) {
                C02eEmission = 3100;
            }
            else {
                C02eEmission = 0;
            }
        }

        return C02eEmission;
    }
}
