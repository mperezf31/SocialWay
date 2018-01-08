package com.caminosantiago.socialway.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by root on 17/10/2015.
 */
public class Comment implements Serializable {

    @SerializedName("id")
    int id;

    @SerializedName("user")
    User user;

    @SerializedName("texto")
    String texto;

    @SerializedName("fecha")
    String fecha;

    public String getFecha() {
        return fecha;
    }

    public String getTexto() {
        return texto;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }


}
