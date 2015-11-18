package com.example.treinamentomobile.myimdb.model;

/**
 * Created by treinamentomobile on 11/17/15.
 */
public class Rating {

    private double average;

    public Rating() {
        
    }

    public Rating(double average) {

        this.average = average;
    }


    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}
