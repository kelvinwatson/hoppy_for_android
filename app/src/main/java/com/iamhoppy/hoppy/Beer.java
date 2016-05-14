package com.iamhoppy.hoppy;

import java.io.Serializable;
import java.util.List;

/* Beer class*/
public class Beer implements Serializable, Comparable {
    private int id;
    private String name;
    private String type;
    private String ibu;
    private String abv;
    private double rating = -1;
    private double averageRating;
    private String brewery;
    private String breweryLogoURL;
    private String description;
    private String myComment = null;
    private boolean favorited = false;
    private List<String> comments = null;

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBreweryLogoURL() {
        return breweryLogoURL;
    }

    public void setBreweryLogoURL(String breweryLogoURL) {
        this.breweryLogoURL = breweryLogoURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIbu() {
        return ibu;
    }

    public void setIbu(String ibu) {
        this.ibu = ibu;
    }

    public String getAbv() {
        return abv;
    }

    public void setAbv(String abv) {
        this.abv = abv;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getBrewery() {
        return brewery;
    }

    public void setBrewery(String brewery) {
        this.brewery = brewery;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public String getMyComment() {
        return myComment;
    }

    public void setMyComment(String myComment) {
        this.myComment = myComment;
    }

    @Override
    public int compareTo(Object another) {
        return this.name.compareTo(((Beer) another).getName());
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
