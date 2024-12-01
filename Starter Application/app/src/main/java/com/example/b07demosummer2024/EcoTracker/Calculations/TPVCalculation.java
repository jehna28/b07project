package com.example.b07demosummer2024.EcoTracker.Calculations;

public class TPVCalculation {

    // This method takes the input: Type, Unit, Value
    public double getCO2eEmission(String type, String dis, double val) {
        double CO2eEmission = 0;
        if (dis.equals("km")) {
            if (type.equals("Gasoline")) {
                CO2eEmission = 0.24 * val;
            }
            else if (type.equals("Diesel")) {
                CO2eEmission = 0.27 * val;
            }
            else if (type.equals("Hybrid")) {
                CO2eEmission = 0.16 * val;
            }
            else { // i.e. type == "Electric"
                CO2eEmission = 0.05 * val;
            }
        } else if (dis.equals("mi")) {
            if (type.equals("Gasoline")) {
                CO2eEmission = 0.38616 * val;
            }
            else if (type.equals("Diesel")) {
                CO2eEmission = 0.43443 * val;
            }
            else if (type.equals("Hybrid")) {
                CO2eEmission = 0.25744 * val;
            }
            else { // i.e. type == "Electric"
                CO2eEmission = 0.08045 * val;
            }
        }
        return CO2eEmission;
    }
}
