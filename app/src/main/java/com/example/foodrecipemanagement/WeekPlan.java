package com.example.foodrecipemanagement;

import com.google.gson.annotations.SerializedName;

public class WeekPlan {
    // NOTE: The keys in the API response are the names of the days of the week (monday, tuesday...)
    @SerializedName("monday")
    private DayPlan monday;
    @SerializedName("tuesday")
    private DayPlan tuesday;
    @SerializedName("wednesday")
    private DayPlan wednesday;
    @SerializedName("thursday")
    private DayPlan thursday;
    @SerializedName("friday")
    private DayPlan friday;
    @SerializedName("saturday")
    private DayPlan saturday;
    @SerializedName("sunday")
    private DayPlan sunday;

    // Getters...
    public DayPlan getMonday() { return monday; }
    public DayPlan getTuesday() { return tuesday; }
    public DayPlan getWednesday() { return wednesday; }
    public DayPlan getThursday() { return thursday; }
    public DayPlan getFriday() { return friday; }
    public DayPlan getSaturday() { return saturday; }
    public DayPlan getSunday() { return sunday; }
}