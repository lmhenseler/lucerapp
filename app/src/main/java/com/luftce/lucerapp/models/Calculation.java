package com.luftce.lucerapp.models;

import com.google.firebase.database.PropertyName;

public class Calculation {
    @PropertyName("edom")
    private String edom;
    @PropertyName("finishHour")
    private String finishHour;
    @PropertyName("price")
    private double price;
    @PropertyName("saving")
    private double saving;
    @PropertyName("startHour")
    private String startHour;
    private String user;

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public String getEdom() {
        return edom;
    }

    public void setEdom(String edom) {
        this.edom = edom;
    }

    public String getFinishHour() {
        return finishHour;
    }

    public void setFinishHour(String finishHour) {
        this.finishHour = finishHour;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSaving() {
        return saving;
    }

    public void setSaving(double saving) {
        this.saving = saving;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }


}
