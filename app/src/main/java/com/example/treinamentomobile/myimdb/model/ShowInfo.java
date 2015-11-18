package com.example.treinamentomobile.myimdb.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

/**
 * Created by treinamentomobile on 11/17/15.
 */
@Table(name = "showinfo")
public class ShowInfo extends Model {

    @Column(index = true, unique = true, name = "_id")
    private int id;

    @Column
    private String name;

    @Column
    private String language;

    @Column
    private List<String> genres;

    @Column
    private Date premiered;

    private Rating rating;

    @Column
    private String s_rating;

    private Image image;

    @Column
    private String medium_image;

    @Column
    private String summary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public Date getPremiered() {
        return premiered;
    }

    public void setPremiered(Date premiered) {
        this.premiered = premiered;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getS_rating() {
        return s_rating;
    }

    public void setS_rating(String s_rating) {
        this.s_rating = s_rating;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getMedium_image() {
        return medium_image;
    }

    public void setMedium_image(String medium_image) {
        this.medium_image = medium_image;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int get_Id() {
        return id;
    }

    public ShowInfo getById(int id) {
        return new Select()
                .from(ShowInfo.class)
                .where("_id = ?", id)
                .executeSingle();
    }
}
