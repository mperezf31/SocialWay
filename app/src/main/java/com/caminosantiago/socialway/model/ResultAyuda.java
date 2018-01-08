package com.caminosantiago.socialway.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by root on 21/10/2015.
 */
public class ResultAyuda implements Serializable{

    @SerializedName("status")
    String status;

    @SerializedName("ayuda")
    String ayuda;

    @SerializedName("enlace")
    String enlace;

    public String getStatus() {
        return status;
    }

    public String getAyuda() {
        return ayuda;
    }

    public String getEnlace() {
        return enlace;
    }
}