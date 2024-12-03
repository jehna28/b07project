package com.example.b07demosummer2024.EcoTracker.Calendar;

public class DataClass {

    private String dataTitle; // title is one of the four categories
    private String dataDesc; // same as activity desciption in each input file
    private String dataLang; // this will read the carbon footprint emission
    private String key;
    private String selectedDate;

    private String activityType;

    public DataClass(String dataTitle, String dataDesc, String dataLang, String selectedDate, String activityType) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.selectedDate = selectedDate;
        this.activityType = activityType;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataLang() {
        return dataLang;
    }

    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    public void setDataLang(String dataLang) {
        this.dataLang = dataLang;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public DataClass() {

    }
}
