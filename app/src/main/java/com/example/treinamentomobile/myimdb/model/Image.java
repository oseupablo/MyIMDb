package com.example.treinamentomobile.myimdb.model;

/**
 * Created by treinamentomobile on 11/17/15.
 */
public class Image {

    private String medium;
    private String original;

    public Image() {
    }

    public Image(String medium, String original) {
        this.medium = medium;
        this.original = original;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }
}
