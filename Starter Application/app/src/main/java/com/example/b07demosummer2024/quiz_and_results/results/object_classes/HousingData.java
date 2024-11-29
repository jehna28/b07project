package com.example.b07demosummer2024.quiz_and_results.results.object_classes;

public class HousingData {
    private String homeTypeAndSize;
    private String[] oneOcc;
    private String[] twoOcc;
    private String[] threeFourOcc;
    private String[] fiveOcc;

    public HousingData(){
        this.homeTypeAndSize = "";
        this.oneOcc = new String[]{""};
        this.twoOcc = new String[]{""};
        this.threeFourOcc = new String[]{""};
        this.fiveOcc = new String[]{""};
    }
    public HousingData(String homeTypeAndSize, String[] oneOcc, String[] twoOcc, String[] threeFourOcc, String[] fiveOcc){
        this.homeTypeAndSize = homeTypeAndSize;
        this.oneOcc = oneOcc;
        this.twoOcc = twoOcc;
        this.threeFourOcc = threeFourOcc;
        this.fiveOcc = fiveOcc;
    }

    public String getHomeTypeAndSize() {
        return homeTypeAndSize;
    }

    public String[] getOneOcc() {
        return oneOcc;
    }

    public String[] getTwoOcc() {
        return twoOcc;
    }

    public String[] getThreeFourOcc() {
        return threeFourOcc;
    }

    public String[] getFiveOcc() {
        return fiveOcc;
    }
}
