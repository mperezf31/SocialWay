package com.caminosantiago.socialway.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 17/10/2015.
 */
public class Publication  implements  Serializable{

    int id;
    User user;
    String description;
    List<String> listImages;
    Double lat;
    Double lon;
    String fecha;

    int numFavourites;
    int numComments;

    public int getNumComments() {
        return numComments;
    }

    public int getNumFavourites() {
        return numFavourites;
    }

    public String getFecha() {
        return fecha;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<String> getListImages() {
        return listImages;
    }

    public String getDescription() {
        return description;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public void setNumFavourites(int numFavourites) {
        this.numFavourites = numFavourites;
    }
}
